package org.volkszaehler.volkszaehlerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VolkszaehlerApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}