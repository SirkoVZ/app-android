package org.volkszaehler.volkszaehlerapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.volkszaehler.volkszaehlerapp.domain.model.AppSettings
import org.volkszaehler.volkszaehlerapp.domain.model.SortMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for app settings
 */
@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val PRIVATE_CHANNEL_UUIDS = stringPreferencesKey("private_channel_uuids")
        private val ZERO_BASED_Y_AXIS = booleanPreferencesKey("zero_based_y_axis")
        private val AUTO_RELOAD = booleanPreferencesKey("auto_reload")
        private val SORT_CHANNEL_MODE = stringPreferencesKey("sort_channel_mode")
        private val TUPLES = intPreferencesKey("tuples")
    }

    /**
     * Get settings as Flow
     */
    val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            serverUrl = preferences[SERVER_URL] ?: "http://demo.volkszaehler.org/middleware.php",
            username = preferences[USERNAME] ?: "",
            password = preferences[PASSWORD] ?: "",
            privateChannelUuids = preferences[PRIVATE_CHANNEL_UUIDS] ?: "",
            zeroBasedYAxis = preferences[ZERO_BASED_Y_AXIS] ?: false,
            autoReload = preferences[AUTO_RELOAD] ?: false,
            sortChannelMode = SortMode.valueOf(
                preferences[SORT_CHANNEL_MODE] ?: SortMode.GROUPS.name
            ),
            tuples = preferences[TUPLES] ?: 1000
        )
    }

    /**
     * Update server URL
     */
    suspend fun updateServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    /**
     * Update authentication credentials
     */
    suspend fun updateAuth(username: String, password: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[PASSWORD] = password
        }
    }

    /**
     * Update private channel UUIDs
     */
    suspend fun updatePrivateChannelUuids(uuids: String) {
        dataStore.edit { preferences ->
            preferences[PRIVATE_CHANNEL_UUIDS] = uuids
        }
    }

    /**
     * Update zero based Y axis setting
     */
    suspend fun updateZeroBasedYAxis(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ZERO_BASED_Y_AXIS] = enabled
        }
    }

    /**
     * Update auto reload setting
     */
    suspend fun updateAutoReload(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_RELOAD] = enabled
        }
    }

    /**
     * Update sort channel mode
     */
    suspend fun updateSortChannelMode(mode: SortMode) {
        dataStore.edit { preferences ->
            preferences[SORT_CHANNEL_MODE] = mode.name
        }
    }

    /**
     * Update tuples setting
     */
    suspend fun updateTuples(tuples: Int) {
        dataStore.edit { preferences ->
            preferences[TUPLES] = tuples
        }
    }

    /**
     * Clear all settings
     */
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
