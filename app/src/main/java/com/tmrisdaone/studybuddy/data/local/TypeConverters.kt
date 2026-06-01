package com.tmrisdaone.studybuddy.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class TypeConverters {
    @TypeConverter fun fromInstant(value: Instant): Long = value.toEpochMilliseconds()
    @TypeConverter fun toInstant(value: Long): Instant = Instant.fromEpochMilliseconds(value)
}
