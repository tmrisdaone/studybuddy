package com.tmrisdaone.studybuddy.data.local

import com.tmrisdaone.studybuddy.domain.ApiProvider
import com.tmrisdaone.studybuddy.domain.NvidiaNim
import com.tmrisdaone.studybuddy.domain.ProviderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Persistent store of ApiProvider configs. Serializes the list to JSON in the
 * existing PreferenceDao (key [PROVIDERS_KEY]) and the active selection to
 * [ACTIVE_KEY]. API keys are encrypted with [SecretStore] at rest.
 */
class ProviderStore(
    private val prefs: PreferenceDao,
    private val secrets: SecretStore = SecretStore()
) {
    private val _providers = MutableStateFlow<List<ApiProvider>>(emptyList())
    val providers: StateFlow<List<ApiProvider>> = _providers.asStateFlow()

    private val _activeId = MutableStateFlow<String?>(null)
    val activeId: StateFlow<String?> = _activeId.asStateFlow()

    suspend fun load() {
        val raw = prefs.getSync(PROVIDERS_KEY)
        val list = if (raw.isNullOrBlank()) emptyList() else parse(raw)
        if (list.isEmpty()) {
            // Seed sensible defaults on first run so the app works out of the box.
            val seeded = listOf(
                ApiProvider(
                    id = ID_GROQ,
                    name = "Groq",
                    type = ProviderType.OPENAI_COMPATIBLE,
                    baseUrl = "https://api.groq.com/openai/v1",
                    apiKey = prefs.getSync("groq_api_key") ?: "",
                    defaultModel = "llama-3.1-8b-instant"
                ),
                ApiProvider(
                    id = ID_NIM,
                    name = "NVIDIA NIM",
                    type = ProviderType.NVIDIA_NIM,
                    baseUrl = NvidiaNim.DEFAULT_BASE_URL,
                    apiKey = "",
                    defaultModel = NvidiaNim.SUGGESTED_MODELS.first()
                ),
                ApiProvider(
                    id = ID_OLLAMA,
                    name = "Ollama (local)",
                    type = ProviderType.OLLAMA,
                    baseUrl = "http://localhost:11434/v1",
                    apiKey = "",
                    defaultModel = "llama3"
                )
            )
            persist(seeded)
            _providers.value = seeded
        } else {
            _providers.value = list
        }
        val active = prefs.getSync(ACTIVE_KEY) ?: _providers.value.firstOrNull()?.id
        _activeId.value = active
        // Migrate legacy plaintext Groq key into the providers list if present.
        val legacy = prefs.getSync("groq_api_key")
        if (!legacy.isNullOrBlank()) {
            update(ID_GROQ) { it.copy(apiKey = legacy) }
            prefs.put(PreferenceEntity("groq_api_key", ""))
        }
    }

    fun snapshot(): List<ApiProvider> = _providers.value
    fun active(): ApiProvider? = _providers.value.firstOrNull { it.id == _activeId.value }

    suspend fun setActive(id: String) {
        _activeId.value = id
        prefs.put(PreferenceEntity(ACTIVE_KEY, id))
    }

    suspend fun add(provider: ApiProvider): ApiProvider {
        val withId = if (provider.id.isBlank()) provider.copy(id = UUID.randomUUID().toString()) else provider
        val next = _providers.value + withId
        persist(next)
        _providers.value = next
        if (_activeId.value == null) setActive(withId.id)
        return withId
    }

    suspend fun update(id: String, transform: (ApiProvider) -> ApiProvider) {
        val next = _providers.value.map { if (it.id == id) transform(it) else it }
        persist(next)
        _providers.value = next
    }

    suspend fun delete(id: String) {
        val next = _providers.value.filterNot { it.id == id }
        persist(next)
        _providers.value = next
        if (_activeId.value == id) {
            _activeId.value = next.firstOrNull()?.id
            _activeId.value?.let { prefs.put(PreferenceEntity(ACTIVE_KEY, it)) }
        }
    }

    private suspend fun persist(list: List<ApiProvider>) {
        prefs.put(PreferenceEntity(PROVIDERS_KEY, encode(list)))
    }

    private fun encode(list: List<ApiProvider>): String {
        val arr = JSONArray()
        list.forEach { p ->
            arr.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("type", p.type.name)
                put("baseUrl", p.baseUrl)
                put("apiKey", secrets.encrypt(p.apiKey))
                put("defaultModel", p.defaultModel)
                val h = JSONObject()
                p.headers.forEach { (k, v) -> h.put(k, v) }
                put("headers", h)
            })
        }
        return arr.toString()
    }

    private fun parse(raw: String): List<ApiProvider> {
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                val headers = mutableMapOf<String, String>()
                val h = o.optJSONObject("headers")
                if (h != null) {
                    val keys = h.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        headers[k] = h.getString(k)
                    }
                }
                ApiProvider(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    type = ProviderType.valueOf(o.optString("type", ProviderType.CUSTOM.name)),
                    baseUrl = o.getString("baseUrl"),
                    apiKey = secrets.decrypt(o.optString("apiKey", "")),
                    defaultModel = o.optString("defaultModel", ""),
                    headers = headers
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        const val PROVIDERS_KEY = "providers_json"
        const val ACTIVE_KEY = "active_provider_id"
        const val ID_GROQ = "builtin_groq"
        const val ID_NIM = "builtin_nim"
        const val ID_OLLAMA = "builtin_ollama"
    }
}
