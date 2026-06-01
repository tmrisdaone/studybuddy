package com.tmrisdaone.studybuddy.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Dao
interface PreferenceDao {
    @Query("SELECT value FROM preferences WHERE key = :key")
    suspend fun get(key: String, default: String? = null): String?

    @Query("SELECT * FROM preferences")
    fun getAll(): Flow<List<PreferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(pref: PreferenceEntity)
}
