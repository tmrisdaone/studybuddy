package com.tmrisdaone.studybuddy.data.local

import androidx.room.*
import kotlinx.datetime.Instant

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // chat, pdf, youtube, scan
    val title: String,
    val summary: String?,
    val inputType: String, // text, pdf, docx, youtube_url, image, audio
    val sourceUri: String?,
    val modelUsed: String?,
    val createdAt: Instant
)

@Entity(tableName = "flashcards")
data class FlashCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val deckName: String,
    val front: String,
    val back: String,
    val tags: String, // comma separated
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReview: Instant,
    val createdAt: Instant
)

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val title: String,
    val questionCount: Int,
    val createdAt: Instant
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: Int, // 0-3 index
    val explanation: String
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val textContent: String,
    val createdAt: Instant
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val role: String, // user, assistant
    val content: String,
    val createdAt: Instant
)
