package com.tmrisdaone.studybuddy.data.repo

import android.content.Context
import com.tmrisdaone.studybuddy.data.local.*
import com.tmrisdaone.studybuddy.data.remote.GroqClient
import com.tmrisdaone.studybuddy.data.remote.ProotScraper
import com.tmrisdaone.studybuddy.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.json.JSONArray

class StudyBuddyRepository(private val db: StudyBuddyDatabase, private val context: Context) {
    private val scraper = ProotScraper()
    private var _groq: GroqClient? = null
    private val groq: GroqClient
        get() {
            if (_groq == null) {
                val key = db.preferenceDao().getSync("groq_api_key") ?: ""
                _groq = GroqClient(key)
            }
            return _groq!!
        }

    val sessions: Flow<List<StudySession>> =
        db.studySessionDao().getAll().map { it.map { s -> s.toDomain() } }

    val documents: Flow<List<Document>> =
        db.documentDao().getAll().map { it.map { d -> d.toDomain() } }

    fun flashcards(sessionId: Long): Flow<List<FlashCard>> =
        db.flashCardDao().getBySession(sessionId).map { it.map { c -> c.toDomain() } }

    suspend fun scrapeUrl(url: String): String = scraper.fetchText(url)

    suspend fun scrapeYoutube(videoId: String): String = scraper.fetchYoutube(videoId)

    suspend fun summarizeText(text: String, model: String = "llama-3.1-8b-instant"): String {
        val system = "Summarize the following text concisely for a student."
        return groq.chat(system, text.take(4000), model)
    }

    suspend fun extractPdfText(localPath: String): String = scraper.fetchPdfText(localPath)

    suspend fun chat(sessionId: Long, userMsg: String, systemPrompt: String, model: String): String {
        val response = groq.chat(systemPrompt, userMsg, model)
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
                session.copy(summary = response.take(200))
            )
        }
        return response
    }

    suspend fun generateQuiz(sessionId: Long, context: String, title: String): Long {
        val quizId = db.quizDao().insertQuiz(
            QuizEntity(
                sessionId = sessionId,
                title = title,
                questionCount = 0,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
        val response = groq.generateQuiz(context)
        val questions = parseQuizJson(response)
        db.quizDao().insertQuestions(
            questions.mapIndexed { idx, q ->
                val options = (q["options"] as? List<*>) ?: emptyList()
                QuizQuestionEntity(
                    quizId = quizId,
                    questionText = q["question"] as? String ?: "",
                    optionA = (options.getOrNull(0) as? String) ?: "",
                    optionB = (options.getOrNull(1) as? String) ?: "",
                    optionC = (options.getOrNull(2) as? String) ?: "",
                    optionD = (options.getOrNull(3) as? String) ?: "",
                    correctAnswer = (q["correct"] as? Number)?.toInt() ?: 0,
                    explanation = q["explanation"] as? String ?: ""
                )
            }
        )
        return quizId
    }

    suspend fun generateFlashcards(sessionId: Long, context: String, title: String, count: Int = 10): Long {
        val deckId = db.studySessionDao().insert(
            StudySessionEntity(
                type = "flashcards",
                title = title,
                inputType = "text",
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
        val raw = groq.generateFlashcards(context, count)
        val cards = parseFlashcards(raw)
        cards.forEach { card ->
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

    suspend fun getApiKey(): String? {
        return db.preferenceDao().getSync("groq_api_key")
    }

    suspend fun saveApiKey(key: String) {
        db.preferenceDao().put(PreferenceEntity("groq_api_key", key))
        // Reset GroqClient so it picks up the new key
        _groq = null
    }

    suspend fun createSession(type: String, title: String, inputType: String): Long {
        return db.studySessionDao().insert(
            StudySessionEntity(
                type = type,
                title = title,
                inputType = inputType,
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