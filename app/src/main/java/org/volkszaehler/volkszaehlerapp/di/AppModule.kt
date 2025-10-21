// AppModule.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/di/AppModule.kt

package org.volkszaehler.volkszaehlerapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module für allgemeine App-Dependencies
 *
 * Stellt bereit:
 * - Application Context
 * - SharedPreferences
 * - Weitere App-weite Dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Stellt den Application Context bereit
     *
     * @param context Der Application Context (automatisch von Hilt injiziert)
     * @return Application Context
     */
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context

    /**
     * Stellt SharedPreferences bereit
     *
     * Wird verwendet für:
     * - Server-URL Speicherung
     * - App-Einstellungen
     * - User Preferences
     *
     * @param context Application Context
     * @return SharedPreferences Instanz
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "volkszaehler_prefs",
            Context.MODE_PRIVATE
        )
    }

    /**
     * Stellt einen PreferencesManager bereit
     *
     * Wrapper um SharedPreferences für typsichere Zugriffe
     *
     * @param sharedPreferences SharedPreferences Instanz
     * @return PreferencesManager
     */
    @Provides
    @Singleton
    fun providePreferencesManager(
        sharedPreferences: SharedPreferences
    ): PreferencesManager {
        return PreferencesManager(sharedPreferences)
    }
}

/**
 * PreferencesManager
 *
 * Typsicherer Wrapper um SharedPreferences
 * Verwaltet alle App-Einstellungen
 */
class PreferencesManager(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AUTO_REFRESH = "auto_refresh"
        private const val KEY_REFRESH_INTERVAL = "refresh_interval"

        private const val DEFAULT_SERVER_URL = "http://demo.volkszaehler.org/middleware.php/"
        private const val DEFAULT_THEME_MODE = "system"
        private const val DEFAULT_AUTO_REFRESH = true
        private const val DEFAULT_REFRESH_INTERVAL = 60 // Sekunden
    }

    /**
     * Server URL
     */
    var serverUrl: String
        get() = sharedPreferences.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
        set(value) = sharedPreferences.edit().putString(KEY_SERVER_URL, value).apply()

    /**
     * Theme Mode (system, light, dark)
     */
    var themeMode: String
        get() = sharedPreferences.getString(KEY_THEME_MODE, DEFAULT_THEME_MODE) ?: DEFAULT_THEME_MODE
        set(value) = sharedPreferences.edit().putString(KEY_THEME_MODE, value).apply()

    /**
     * Auto-Refresh aktiviert
     */
    var autoRefresh: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_REFRESH, DEFAULT_AUTO_REFRESH)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_REFRESH, value).apply()

    /**
     * Refresh-Interval in Sekunden
     */
    var refreshInterval: Int
        get() = sharedPreferences.getInt(KEY_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL)
        set(value) = sharedPreferences.edit().putInt(KEY_REFRESH_INTERVAL, value).apply()

    /**
     * Setzt alle Einstellungen auf Standardwerte zurück
     */
    fun resetToDefaults() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Prüft ob Server-URL gesetzt ist
     */
    fun hasCustomServerUrl(): Boolean {
        return serverUrl != DEFAULT_SERVER_URL
    }
}