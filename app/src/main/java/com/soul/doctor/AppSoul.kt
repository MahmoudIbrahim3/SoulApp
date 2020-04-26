package com.soul.doctor

import android.app.Application
import com.google.firebase.FirebaseApp

class AppSoul: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}