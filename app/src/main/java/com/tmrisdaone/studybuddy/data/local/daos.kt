package com.tmrisdaone.studybuddy.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    suspend fun get(id: Long): StudySessionEntity?

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    fun getSync(id: Long): StudySessionEntity?

    @Insert
    suspend fun insert(session: StudySessionEntity): Long
}

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM flashcards WHERE sessionId = :sessionId ORDER BY nextReview ASC")
    fun getBySession(sessionId: Long): Flow<List<FlashCardEntity>>

    @Insert
    suspend fun insert(card: FlashCardEntity): Long

    @Update
    suspend fun update(card: FlashCardEntity)
}

@Dao
interface QuizDao {
    @Insert
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Insert
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM quizzes WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getBySession(sessionId: Long): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    suspend fun getQuestions(quizId: Long): List<QuizQuestionEntity>
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun get(id: Long): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: DocumentEntity): Long

    @Delete
    suspend fun delete(doc: DocumentEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getBySession(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insert(msg: ChatMessageEntity): Long
}
