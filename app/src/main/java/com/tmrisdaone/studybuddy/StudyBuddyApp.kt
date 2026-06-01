package com.tmrisdaone.studybuddy

import android.app.Application
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase

class StudyBuddyApp : Application() {
    val database by lazy { StudyBuddyDatabase.get(this) }
}
