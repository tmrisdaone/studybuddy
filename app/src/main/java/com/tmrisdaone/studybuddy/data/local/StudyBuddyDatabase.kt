package com.tmrisdaone.studybuddy.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        StudySessionEntity::class,
        FlashCardEntity::class,
        QuizEntity::class,
        QuizQuestionEntity::class,
        DocumentEntity::class,
        ChatMessageEntity::class,
        PreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverters::class)
abstract class StudyBuddyDatabase : RoomDatabase() {
    abstract fun studySessionDao(): StudySessionDao
    abstract fun flashCardDao(): FlashCardDao
    abstract fun quizDao(): QuizDao
    abstract fun documentDao(): DocumentDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun preferenceDao(): PreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: StudyBuddyDatabase? = null

        fun get(context: android.content.Context): StudyBuddyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudyBuddyDatabase::class.java,
                    "studybuddy.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
