package org.volkszaehler.volkszaehlerapp.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "volkszaehler_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_BASE_URL = "base_url"
        private const val DEFAULT_BASE_URL = "http://demo.volkszaehler.org/middleware.php/"
    }

    fun getBaseUrl(): String {
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun setBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url).apply()
    }

    fun hasCustomBaseUrl(): Boolean {
        return prefs.contains(KEY_BASE_URL)
    }
}