package com.tmrisdaone.studybuddy.data.remote

import com.tmrisdaone.studybuddy.domain.ApiProvider
import com.tmrisdaone.studybuddy.domain.ModelInfo
import com.tmrisdaone.studybuddy.domain.ProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Unified adapter between the app and any OpenAI-compatible endpoint
 * (including NVIDIA NIM, local Ollama, custom gateways). All calls hit the
 * standard /v1/chat/completions and /v1/models routes on the active
 * provider's [ApiProvider.baseUrl].
 */
class ChatGateway {

    private val JSON = "application/json; charset=utf-8".toMediaType()

    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    // ---- non-streaming chat -------------------------------------------------

    suspend fun chat(
        provider: ApiProvider,
        system: String,
        user: String,
        model: String
    ): String = withContext(Dispatchers.IO) {
        val payload = buildChatPayload(provider, system, user, model, stream = false)
        val req = buildRequest(provider, payload.toRequestBody(JSON), stream = false)
        http.newCall(req).execute().use { r ->
            if (!r.isSuccessful) {
                throw IOException(describeError(provider, r))
            }
            val body = r.body?.string().orEmpty()
            try {
                JSONObject(body).getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content")
            } catch (e: Exception) {
                throw IOException("Malformed response from ${provider.name}: ${body.take(300)}")
            }
        }
    }

    // ---- streaming chat (SSE) ------------------------------------------------

    /**
     * Streams assistant content fragments as a Flow of String deltas.
     * Errors (auth / network / rate limit) are propagated by closing the
     * flow with an IOException.
     */
    fun chatStream(
        provider: ApiProvider,
        system: String,
        user: String,
        model: String
    ): Flow<String> = callbackFlow {
        val payload = buildChatPayload(provider, system, user, model, stream = true)
        val req = buildRequest(provider, payload.toRequestBody(JSON), stream = true)

        val call = http.newCall(req)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                close(IOException("Network error talking to ${provider.name}: ${e.message}"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val msg = describeError(provider, response)
                    response.close()
                    close(IOException(msg))
                    return
                }
                try {
                    response.body.use { b ->
                        val source = b.source()
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line() ?: break
                            if (line.isBlank() || !line.startsWith("data:")) continue
                            val data = line.removePrefix("data:").trim()
                            if (data == "[DONE]") break
                            val delta = parseDelta(data) ?: continue
                            trySend(delta)
                        }
                    }
                    close()
                } catch (e: Exception) {
                    close(IOException("Stream interrupted from ${provider.name}: ${e.message}"))
                }
            }
        })

        awaitClose { call.cancel() }
    }.flowOn(Dispatchers.IO)

    // ---- model discovery ----------------------------------------------------

    suspend fun listModels(provider: ApiProvider): List<ModelInfo> = withContext(Dispatchers.IO) {
        val req = buildRequest(provider, "".toRequestBody(JSON), stream = false, models = true)
        val models = mutableListOf<ModelInfo>()
        try {
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@withContext emptyList()
                val j = JSONObject(r.body!!.string())
                val arr = j.optJSONArray("data") ?: return@withContext emptyList()
                for (i in 0 until arr.length()) {
                    val id = arr.getJSONObject(i).optString("id")
                    if (id.isNotBlank()) {
                        models.add(ModelInfo(id = id, label = id, providerId = provider.id))
                    }
                }
            }
        } catch (_: Exception) { /* fallthrough to empty */ }
        models
    }

    suspend fun testConnection(provider: ApiProvider): Boolean = try {
        listModels(provider).isNotEmpty()
    } catch (_: Exception) {
        false
    }

    // ---- request building ---------------------------------------------------

    private fun buildChatPayload(
        provider: ApiProvider,
        system: String,
        user: String,
        model: String,
        stream: Boolean
    ): String {
        val resolvedModel = model.ifBlank { provider.defaultModel }
        return JSONObject().apply {
            put("model", resolvedModel)
            put("temperature", 0.7)
            put("max_tokens", 4096)
            put("stream", stream)
            put("messages", JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", system))
                put(JSONObject().put("role", "user").put("content", user))
            })
        }.toString()
    }

    private fun buildRequest(
        provider: ApiProvider,
        body: okhttp3.RequestBody,
        stream: Boolean,
        models: Boolean = false
    ): Request {
        val path = if (models) "/v1/models" else "/v1/chat/completions"
        val url = provider.baseUrl.trimEnd('/') + path

        val builder = Request.Builder().url(url)

        when (provider.type) {
            ProviderType.OLLAMA -> {
                // Ollama's OpenAI shim needs no Authorization header.
            }
            ProviderType.NVIDIA_NIM,
            ProviderType.OPENAI_COMPATIBLE,
            ProviderType.CUSTOM -> {
                if (provider.apiKey.isNotBlank()) {
                    builder.addHeader("Authorization", "Bearer ${provider.apiKey}")
                }
            }
        }

        if (!models && stream) builder.addHeader("Accept", "text/event-stream")
        provider.headers.forEach { (k, v) -> builder.addHeader(k, v) }

        return if (models && body.contentLength() == 0L) builder.get().build()
        else builder.post(body).build()
    }

    private fun describeError(provider: ApiProvider, r: Response): String {
        val code = r.code
        val snippet = try { r.body?.string()?.take(500).orEmpty() } catch (_: Exception) { "" }
        return when (code) {
            401, 403 -> "Authentication failed for ${provider.name} (HTTP $code). Check the API key."
            404 -> "${provider.name} endpoint not found (HTTP 404). Verify the Base URL: ${provider.baseUrl}"
            429 -> "Rate limited by ${provider.name} (HTTP 429). Slow down or check quota."
            in 400..499 -> "${provider.name} rejected the request (HTTP $code). $snippet"
            in 500..599 -> "${provider.name} server error (HTTP $code). Try again later."
            else -> "${provider.name} request failed (HTTP $code). $snippet"
        }
    }

    private fun parseDelta(data: String): String? {
        return try {
            val obj = JSONObject(data)
            val choices = obj.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null
            choices.getJSONObject(0).optJSONObject("delta")?.optString("content")
        } catch (_: Exception) { null }
    }
}
