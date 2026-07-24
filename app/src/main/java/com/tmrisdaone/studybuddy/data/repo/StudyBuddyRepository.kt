package com.tmrisdaone.studybuddy.data.repo

import android.content.Context
import com.tmrisdaone.studybuddy.data.local.*
import com.tmrisdaone.studybuddy.data.remote.ChatGateway
import com.tmrisdaone.studybuddy.data.remote.ProotScraper
import com.tmrisdaone.studybuddy.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.json.JSONArray

class StudyBuddyRepository(
    private val db: StudyBuddyDatabase,
    private val context: Context,
    private val providerStore: ProviderStore = ProviderStore(db.preferenceDao()),
    private val gateway: ChatGateway = ChatGateway()
) {
    private val scraper = ProotScraper()

    val sessions: Flow<List<StudySession>> =
        db.studySessionDao().getAll().map { it.map { s -> s.toDomain() } }

    val documents: Flow<List<Document>> =
        db.documentDao().getAll().map { it.map { d -> d.toDomain() } }

    val providers: StateFlow<List<ApiProvider>> = providerStore.providers
    val activeProviderId: StateFlow<String?> = providerStore.activeId

    suspend fun initProviders() = providerStore.load()

    fun activeProvider(): ApiProvider? = providerStore.active()
    suspend fun setActiveProvider(id: String) = providerStore.setActive(id)
    suspend fun addProvider(p: ApiProvider) = providerStore.add(p)
    suspend fun updateProvider(id: String, transform: (ApiProvider) -> ApiProvider) =
        providerStore.update(id, transform)
    suspend fun deleteProvider(id: String) = providerStore.delete(id)
    suspend fun listModels(provider: ApiProvider): List<ModelInfo> = gateway.listModels(provider)
    suspend fun testConnection(provider: ApiProvider): Boolean = gateway.testConnection(provider)

    fun flashcards(sessionId: Long): Flow<List<FlashCard>> =
        db.flashCardDao().getBySession(sessionId).map { it.map { c -> c.toDomain() } }

    suspend fun scrapeUrl(url: String): String = scraper.fetchText(url)

    suspend fun scrapeYoutube(videoId: String): String = scraper.fetchYoutube(videoId)

    suspend fun summarizeText(text: String, model: String): String {
        val provider = activeProvider() ?: error("No active API provider configured")
        val system = "Summarize the following text concisely for a student."
        return gateway.chat(provider, system, text.take(4000), model)
    }

    suspend fun extractPdfText(localPath: String): String = scraper.fetchPdfText(localPath)

    suspend fun chat(sessionId: Long, userMsg: String, systemPrompt: String, model: String): String {
        val provider = activeProvider() ?: error("No active API provider configured")
        val response = gateway.chat(provider, systemPrompt, userMsg, model)
        val now = Clock.System.now().toEpochMilliseconds()
        db.chatMessageDao().insert(
            ChatMessageEntity(sessionId = sessionId, role = "user", content = userMsg, createdAt = now)
        )
        db.chatMessageDao().insert(
            ChatMessageEntity(sessionId = sessionId, role = "assistant", content = response, createdAt = now)
        )
        val session = db.studySessionDao().getSync(sessionId)
        if (session != null) {
            db.studySessionDao().insert(
                session.copy(
                    summary = response.take(200),
                    sourceUri = session.sourceUri,
                    modelUsed = model
                )
            )
        }
        return response
    }

    fun chatStream(systemPrompt: String, userMsg: String, model: String): Flow<String> {
        val provider = activeProvider() ?: error("No active API provider configured")
        return gateway.chatStream(provider, systemPrompt, userMsg, model)
    }

    suspend fun generateQuiz(sessionId: Long, context: String, title: String): Long {
        val provider = activeProvider() ?: error("No active API provider configured")
        val quizId = db.quizDao().insertQuiz(
            QuizEntity(
                sessionId = sessionId,
                title = title,
                questionCount = 0,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
        val response = withContext(Dispatchers.IO) {
            gateway.chat(
                provider,
                "You are a study assistant. Generate 5 multiple choice questions based on the provided context.",
                buildString {
                    appendLine("Create a quiz from this material:\n")
                    appendLine(context.take(12000))
                    appendLine("\nFormat per question as JSON array of objects with keys: question, options [A-D], correct (0-3), explanation.")
                    appendLine("Return ONLY pure JSON array.")
                },
                provider.defaultModel.ifBlank { "llama-3.1-8b-instant" }
            )
        }
        val questions = parseQuizJson(response)
        db.quizDao().insertQuestions(
            questions.mapIndexed { idx, q ->
                val options = (q["options"] as? List<String>) ?: emptyList()
                QuizQuestionEntity(
                    quizId = quizId,
                    questionText = q["question"] as? String ?: "",
                    optionA = options.getOrNull(0) ?: "",
                    optionB = options.getOrNull(1) ?: "",
                    optionC = options.getOrNull(2) ?: "",
                    optionD = options.getOrNull(3) ?: "",
                    correctAnswer = (q["correct"] as? Number)?.toInt() ?: 0,
                    explanation = q["explanation"] as? String ?: ""
                )
            }
        )
        return quizId
    }

    suspend fun generateFlashcards(sessionId: Long, context: String, title: String, count: Int = 10): Long {
        val provider = activeProvider() ?: error("No active API provider configured")
        val deckId = db.studySessionDao().insert(
            StudySessionEntity(
                type = "flashcards",
                title = title,
                summary = null,
                inputType = "text",
                sourceUri = null,
                modelUsed = null,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
        val raw = withContext(Dispatchers.IO) {
            gateway.chat(
                provider,
                "You are a study assistant. Generate $count flashcards from the context.",
                buildString {
                    appendLine("Create flashcards from this material:\n")
                    appendLine(context.take(12000))
                    appendLine("\nFormat as JSON array of objects: {front: String, back: String}.")
                    appendLine("Return ONLY pure JSON array.")
                },
                provider.defaultModel.ifBlank { "llama-3.1-8b-instant" }
            )
        }
        parseFlashcards(raw).forEach { card ->
            val now = Clock.System.now().toEpochMilliseconds()
            db.flashCardDao().insert(
                FlashCardEntity(
                    sessionId = sessionId,
                    deckName = title,
                    front = card["front"] as? String ?: "",
                    back = card["back"] as? String ?: "",
                    tags = "",
                    nextReview = now,
                    createdAt = now
                )
            )
        }
        return deckId
    }

    // Legacy single-key helpers retained for SettingsViewModel compatibility.
    suspend fun getApiKey(): String? = activeProvider()?.apiKey
    suspend fun saveApiKey(key: String) {
        val active = activeProvider() ?: return
        providerStore.update(active.id) { it.copy(apiKey = key) }
    }

    suspend fun createSession(type: String, title: String, inputType: String): Long {
        return db.studySessionDao().insert(
            StudySessionEntity(
                type = type,
                title = title,
                summary = null,
                inputType = inputType,
                sourceUri = null,
                modelUsed = null,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    private fun parseQuizJson(raw: String): List<Map<String, Any?>> {
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                mapOf(
                    "question" to obj.optString("question"),
                    "options" to listOf(
                        obj.optString("A"),
                        obj.optString("B"),
                        obj.optString("C"),
                        obj.optString("D")
                    ),
                    "correct" to obj.optInt("correctAnswer", 0),
                    "explanation" to obj.optString("explanation")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseFlashcards(raw: String): List<Map<String, String?>> {
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                mapOf(
                    "front" to obj.optString("front"),
                    "back" to obj.optString("back")
                )
            }
        } catch (e: Exception) { emptyList() }
    }
}
