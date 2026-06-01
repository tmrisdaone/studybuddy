package com.tmrisdaone.studybuddy.domain

import kotlinx.datetime.Instant

data class StudySession(
    val id: Long = 0,
    val type: String,
    val title: String,
    val summary: String?,
    val inputType: String,
    val sourceUri: String?,
    val modelUsed: String?,
    val createdAt: Instant
)

data class FlashCard(
    val id: Long = 0,
    val sessionId: Long,
    val deckName: String,
    val front: String,
    val back: String,
    val tags: String,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReview: Instant,
    val createdAt: Instant
)

data class Quiz(
    val id: Long = 0,
    val sessionId: Long,
    val title: String,
    val questionCount: Int,
    val createdAt: Instant
)

data class QuizQuestion(
    val id: Long = 0,
    val quizId: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: Int,
    val explanation: String
)

data class Document(
    val id: Long = 0,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val textContent: String,
    val createdAt: Instant
)

data class ChatMessage(
    val id: Long = 0,
    val sessionId: Long,
    val role: String,
    val content: String,
    val createdAt: Instant
)
