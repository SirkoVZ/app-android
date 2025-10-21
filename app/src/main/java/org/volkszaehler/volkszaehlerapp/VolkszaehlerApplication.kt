// VolkszaehlerApplication.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/VolkszaehlerApplication.kt

package org.volkszaehler.volkszaehlerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application-Klasse für die Volkszähler App
 *
 * Diese Klasse wird beim App-Start initialisiert und ist der
 * Entry Point für Hilt Dependency Injection.
 *
 * @HiltAndroidApp triggert die Code-Generierung für Hilt
 * und ermöglicht Dependency Injection in der gesamten App.
 */
@HiltAndroidApp
class VolkszaehlerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Hier können globale Initialisierungen durchgeführt werden
        // z.B. Logging, Crash Reporting, Analytics, etc.

        // Beispiel: Timber für Logging (optional)
        // if (BuildConfig.DEBUG) {
        //     Timber.plant(Timber.DebugTree())
        // }
    }
}