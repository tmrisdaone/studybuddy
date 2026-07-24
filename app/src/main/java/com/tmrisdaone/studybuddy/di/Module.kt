package com.tmrisdaone.studybuddy.di

import android.content.Context
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.data.remote.ProotScraper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@Suppress("UNUSED_PARAMETER") context: Context): StudyBuddyDatabase {
        return StudyBuddyDatabase.get(context)
    }

    @Provides
    @Singleton
    fun provideRepository(database: StudyBuddyDatabase, context: Context): StudyBuddyRepository {
        return StudyBuddyRepository(database, context)
    }

    @Provides
    @Singleton
    fun provideScraper(): ProotScraper {
        return ProotScraper()
    }
}