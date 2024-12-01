package com.compose.jettrivia.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JetTriviaApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

}