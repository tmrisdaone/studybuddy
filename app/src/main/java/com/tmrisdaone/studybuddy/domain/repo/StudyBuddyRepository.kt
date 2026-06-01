package com.tmrisdaone.studybuddy.domain.repo

import com.tmrisdaone.studybuddy.domain.*

interface StudyBuddyRepository {
    val sessions: kotlinx.coroutines.flow.Flow<List<StudySession>>
    val documents: kotlinx.coroutines.flow.Flow<List<Document>>
    fun flashcards(sessionId: Long): kotlinx.coroutines.flow.Flow<List<FlashCard>>
    suspend fun scrapeUrl(url: String): String
    suspend fun scrapeYoutube(videoId: String): String
    suspend fun chat(sessionId: Long, userMsg: String, systemPrompt: String, model: String): String
    suspend fun generateQuiz(sessionId: Long, context: String, title: String): Long
}
