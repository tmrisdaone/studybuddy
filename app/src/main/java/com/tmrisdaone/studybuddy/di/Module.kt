package com.tmrisdaone.studybuddy.di

import android.content.Context
import com.tmrisdaone.studybuddy.data.local.ProviderStore
import com.tmrisdaone.studybuddy.data.local.SecretStore
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import com.tmrisdaone.studybuddy.data.remote.ChatGateway
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.data.remote.ProotScraper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StudyBuddyDatabase {
        return StudyBuddyDatabase.get(context)
    }

    @Provides
    @Singleton
    fun provideSecretStore(): SecretStore = SecretStore()

    @Provides
    @Singleton
    fun provideProviderStore(
        database: StudyBuddyDatabase,
        secrets: SecretStore
    ): ProviderStore = ProviderStore(database.preferenceDao(), secrets)

    @Provides
    @Singleton
    fun provideChatGateway(): ChatGateway = ChatGateway()

    @Provides
    @Singleton
    fun provideRepository(
        database: StudyBuddyDatabase,
        @ApplicationContext context: Context,
        providerStore: ProviderStore,
        gateway: ChatGateway
    ): StudyBuddyRepository {
        return StudyBuddyRepository(database, context, providerStore, gateway)
    }

    @Provides
    @Singleton
    fun provideScraper(): ProotScraper {
        return ProotScraper()
    }
}
