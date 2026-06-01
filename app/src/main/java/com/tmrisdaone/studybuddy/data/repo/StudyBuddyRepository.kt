package com.tmrisdaone.studybuddy.data.repo

import android.content.Context
import com.tmrisdaone.studybuddy.data.local.*
import com.tmrisdaone.studybuddy.data.remote.GroqClient
import com.tmrisdaone.studybuddy.data.remote.ProotScraper
import com.tmrisdaone.studybuddy.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class StudyBuddyRepository(private val db: StudyBuddyDatabase, private val context: Context) {
    private val scraper = ProotScraper()
    private val groq: GroqClient
        get() = GroqClient(db.preferenceDao().get("groq_api_key", "") ?: "")

    val sessions: Flow<List<StudySession>> =
        db.studySessionDao().getAll().map { it.map { s -> s.toDomain() } }

    val documents: Flow<List<Document>> =
        db.documentDao().getAll().map { it.map { d -> d.toDomain() } }

    fun flashcards(sessionId: Long): Flow<List<FlashCard>> =
        db.flashCardDao().getBySession(sessionId).map { it.map { c -> c.toDomain() } }

    suspend fun scrapeUrl(url: String): String = scraper.fetchText(url)

    suspend fun scrapeYoutube(videoId: String): String = scraper.fetchYoutube(videoId)

    suspend fun chat(sessionId: Long, userMsg: String, systemPrompt: String, model: String): String {
        val response = groq.chat(systemPrompt, userMsg, model)
        db.chatMessageDao().insert(
            ChatMessageEntity(sessionId = sessionId, role = "user", content = userMsg, createdAt = Clock.System.now())
        )
        db.chatMessageDao().insert(
            ChatMessageEntity(sessionId = sessionId, role = "assistant", content = response, createdAt = Clock.System.now())
        )
        val session = db.studySessionDao().get(sessionId)
        if (session != null) {
            db.studySessionDao().insert(session.copy(summary = response.take(200)))
        }
        return response
    }

    suspend fun generateQuiz(sessionId: Long, context: String, title: String): Long {
        val quizId = db.quizDao().insertQuiz(
            QuizEntity(sessionId = sessionId, title = title, questionCount = 0, createdAt = Clock.System.now())
        )
        val response = groq.generateQuiz(context)
        val questions = parseQuizJson(response)
        db.quizDao().insertQuestions(
            questions.mapIndexed { idx, q ->
                QuizQuestionEntity(
                    quizId = quizId,
                    questionText = q["question"] ?: "",
                    optionA = q["options"]?.getOrNull(0)?.toString() ?: "",
                    optionB = q["options"]?.getOrNull(1)?.toString() ?: "",
                    optionC = q["options"]?.getOrNull(2)?.toString() ?: "",
                    optionD = q["options"]?.getOrNull(3)?.toString() ?: "",
                    correctAnswer = (q["correct"] as Number?)?.toInt() ?: 0,
                    explanation = q["explanation"] ?: ""
                )
            }
        )
        return quizId
    }

    private fun parseQuizJson(raw: String): List<Map<String, Any?>> {
        return try {
            val arr = org.json.JSONArray(raw)
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
}
