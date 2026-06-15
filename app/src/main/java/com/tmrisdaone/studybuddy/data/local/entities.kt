package com.tmrisdaone.studybuddy.data.local

import androidx.room.*
import com.tmrisdaone.studybuddy.domain.*
import kotlinx.datetime.Instant

@TypeConverters(TypeConverters::class)
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

@TypeConverters(TypeConverters::class)
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

@TypeConverters(TypeConverters::class)
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

@TypeConverters(TypeConverters::class)
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val textContent: String,
    val createdAt: Instant
)

@TypeConverters(TypeConverters::class)
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val role: String, // user, assistant
    val content: String,
    val createdAt: Instant
)

// Extension functions for Entity -> Domain mapping
fun StudySessionEntity.toDomain(): StudySession = StudySession(
    id = id,
    type = type,
    title = title,
    summary = summary,
    inputType = inputType,
    sourceUri = sourceUri,
    modelUsed = modelUsed,
    createdAt = createdAt
)

fun FlashCardEntity.toDomain(): FlashCard = FlashCard(
    id = id,
    sessionId = sessionId,
    deckName = deckName,
    front = front,
    back = back,
    tags = tags,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextReview = nextReview,
    createdAt = createdAt
)

fun DocumentEntity.toDomain(): Document = Document(
    id = id,
    name = name,
    mimeType = mimeType,
    sizeBytes = sizeBytes,
    textContent = textContent,
    createdAt = createdAt
)

fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    id = id,
    sessionId = sessionId,
    role = role,
    content = content,
    createdAt = createdAt
)

fun QuizEntity.toDomain() = Quiz(
    id = id,
    sessionId = sessionId,
    title = title,
    questionCount = questionCount,
    createdAt = createdAt
)

fun QuizQuestionEntity.toDomain() = QuizQuestion(
    id = id,
    quizId = quizId,
    questionText = questionText,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    correctAnswer = correctAnswer,
    explanation = explanation
)