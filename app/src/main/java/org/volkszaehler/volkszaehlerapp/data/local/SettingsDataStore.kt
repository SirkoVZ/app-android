package org.volkszaehler.volkszaehlerapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings DataStore - Ersetzt volkszaehler_preferences.xml
 *
 * Moderne Alternative zu SharedPreferences/XML Preferences.
 * Verwendet Kotlin Coroutines und Flow für reaktive Updates.
 *
 * Alle Settings aus der alten XML-Datei:
 * - volkszaehlerURL
 * - username / password (Basic Auth)
 * - privateChannelUUIDs
 * - ZeroBasedYAxis
 * - autoReload
 * - sortChannelsMode
 * - tuplesLimit
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "volkszaehler_settings"
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ========== PREFERENCE KEYS ==========

    companion object {
        // Server Settings
        val SERVER_URL = stringPreferencesKey("volkszaehlerURL")

        // Authentication
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")

        // Channel Settings
        val PRIVATE_CHANNEL_UUIDS = stringPreferencesKey("privateChannelUUIDs")

        // Chart Settings
        val ZERO_BASED_Y_AXIS = booleanPreferencesKey("ZeroBasedYAxis")

        // Reload Settings
        val AUTO_RELOAD = booleanPreferencesKey("autoReload")

        // Sort Settings
        val SORT_CHANNELS_MODE = stringPreferencesKey("sortChannelsMode")

        // Data Settings
        val TUPLES_LIMIT = intPreferencesKey("tuplesLimit")

        // Defaults
        const val DEFAULT_SERVER_URL = "http://demo.volkszaehler.org/middleware.php/"
        const val DEFAULT_TUPLES_LIMIT = 1000
    }

    // ========== SERVER URL ==========

    val serverUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: DEFAULT_SERVER_URL
    }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    // ========== AUTHENTICATION ==========

    val username: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    suspend fun setUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    val password: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD] ?: ""
    }

    suspend fun setPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD] = password
        }
    }

    // ========== PRIVATE CHANNELS ==========

    val privateChannelUUIDs: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[PRIVATE_CHANNEL_UUIDS]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    suspend fun setPrivateChannelUUIDs(uuids: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PRIVATE_CHANNEL_UUIDS] = uuids.joinToString(",")
        }
    }

    // ========== CHART SETTINGS ==========

    val zeroBasedYAxis: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ZERO_BASED_Y_AXIS] ?: false
    }

    suspend fun setZeroBasedYAxis(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ZERO_BASED_Y_AXIS] = enabled
        }
    }

    // ========== AUTO RELOAD ==========

    val autoReload: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_RELOAD] ?: false
    }

    suspend fun setAutoReload(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_RELOAD] = enabled
        }
    }

    // ========== SORT MODE ==========

    enum class SortMode {
        NAME, TYPE, LAST_VALUE
    }

    val sortChannelsMode: Flow<SortMode> = context.dataStore.data.map { preferences ->
        when (preferences[SORT_CHANNELS_MODE]) {
            "type" -> SortMode.TYPE
            "lastValue" -> SortMode.LAST_VALUE
            else -> SortMode.NAME
        }
    }

    suspend fun setSortChannelsMode(mode: SortMode) {
        context.dataStore.edit { preferences ->
            preferences[SORT_CHANNELS_MODE] = when (mode) {
                SortMode.NAME -> "name"
                SortMode.TYPE -> "type"
                SortMode.LAST_VALUE -> "lastValue"
            }
        }
    }

    // ========== TUPLES LIMIT ==========

    val tuplesLimit: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TUPLES_LIMIT] ?: DEFAULT_TUPLES_LIMIT
    }

    suspend fun setTuplesLimit(limit: Int) {
        context.dataStore.edit { preferences ->
            preferences[TUPLES_LIMIT] = limit
        }
    }

    // ========== CLEAR ALL ==========

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}