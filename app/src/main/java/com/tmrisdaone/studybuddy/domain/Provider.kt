package com.tmrisdaone.studybuddy.domain

/**
 * Provider archetypes. All speak the OpenAI-compatible /v1/chat/completions
 * surface; the enum just controls default auth/headers/URL behavior.
 */
enum class ProviderType {
    OPENAI_COMPATIBLE,
    NVIDIA_NIM,
    OLLAMA,
    CUSTOM
}

/**
 * A user-configured API endpoint. The app supports many of these at once and
 * lets the user switch the "active" one from the model selector.
 *
 * @param id        stable identifier (uuid)
 * @param name      display name, e.g. "Local NIM Instance"
 * @param type       controls default auth handling (NIM = Bearer, Ollama = none)
 * @param baseUrl    OpenAI-compatible base, e.g. https://integrate.api.nvidia.com/v1
 * @param apiKey     secret, stored encrypted
 * @param defaultModel default model id used when chat doesn't specify one
 * @param headers    optional overrides applied on every request
 */
data class ApiProvider(
    val id: String,
    val name: String,
    val type: ProviderType,
    val baseUrl: String,
    val apiKey: String,
    val defaultModel: String,
    val headers: Map<String, String> = emptyMap()
)

data class ModelInfo(
    val id: String,
    val label: String,
    val providerId: String
)

object NvidiaNim {
    const val DEFAULT_BASE_URL = "https://integrate.api.nvidia.com/v1"
    val SUGGESTED_MODELS = listOf(
        "meta/llama-3.1-70b-instruct",
        "meta/llama-3.1-408b-instruct",
        "mistralai/mixtral-8x22b-instruct",
        "nvidia/neva-22b"
    )
}

object DefaultProviders {
    fun preset(id: String, type: ProviderType, baseUrl: String, name: String): ApiProvider =
        ApiProvider(
            id = id,
            name = name,
            type = type,
            baseUrl = baseUrl,
            apiKey = "",
            defaultModel = "",
            headers = emptyMap()
        )
}
