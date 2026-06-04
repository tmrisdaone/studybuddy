package com.tmrisdaone.studybuddy

import android.app.Application
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudyBuddyApp : Application() {
    val database by lazy { StudyBuddyDatabase.get(this) }
}
