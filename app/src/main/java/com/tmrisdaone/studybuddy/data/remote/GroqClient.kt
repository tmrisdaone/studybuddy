package com.tmrisdaone.studybuddy.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GroqClient(private val apiKey: String) {

    private val http = okhttp3.OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun chat(system: String, user: String, model: String): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("model", model)
            put("temperature", 0.7)
            put("max_tokens", 4096)
            put("messages", JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", system))
                put(JSONObject().put("role", "user").put("content", user))
            })
        }.toString()

        val req = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body.toRequestBody(JSON))
            .build()

        http.newCall(req).execute().use { r ->
            if (!r.isSuccessful) throw IOException("Groq error ${r.code}")
            val j = JSONObject(r.body!!.string())
            j.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message").getString("content")
        }
    }

    suspend fun generateQuiz(context: String, count: Int = 5): String = chat(
        system = "You are a study assistant. Generate $count multiple choice questions based on the provided context.",
        user = buildString {
            appendLine("Create a quiz from this material:\n")
            appendLine(context.take(12000))
            appendLine("\nFormat per question as JSON array of objects with keys: question, options [A-D], correct (0-3), explanation.")
            appendLine("Return ONLY pure JSON array.")
        },
        model = "llama-3.1-8b-instant"
    )

    suspend fun generateFlashcards(context: String, count: Int = 10): String = chat(
        system = "You are a study assistant. Generate $count flashcards from the context.",
        user = buildString {
            appendLine("Create flashcards from this material:\n")
            appendLine(context.take(12000))
            appendLine("\nFormat as JSON array of objects: {front: String, back: String}.")
            appendLine("Return ONLY pure JSON array.")
        },
        model = "llama-3.1-8b-instant"
    )
}
