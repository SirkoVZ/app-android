package org.volkszaehler.volkszaehlerapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val TUPLES = intPreferencesKey("tuples")
        val ZERO_BASED_Y_AXIS = booleanPreferencesKey("zero_based_y_axis")
        val AUTO_RELOAD = booleanPreferencesKey("auto_reload")
        val PRIVATE_CHANNEL_UUIDS = stringPreferencesKey("private_channel_uuids")
        val SORT_CHANNEL_MODE = stringPreferencesKey("sort_channel_mode")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { preferences ->
        Settings(
            serverUrl = preferences[PreferencesKeys.SERVER_URL] ?: "https://demo.volkszaehler.org/middleware.php",
            username = preferences[PreferencesKeys.USERNAME] ?: "",
            password = preferences[PreferencesKeys.PASSWORD] ?: "",
            tuples = preferences[PreferencesKeys.TUPLES] ?: 1000,
            zeroBasedYAxis = preferences[PreferencesKeys.ZERO_BASED_Y_AXIS] ?: false,
            autoReload = preferences[PreferencesKeys.AUTO_RELOAD] ?: false,
            privateChannelUUIDs = preferences[PreferencesKeys.PRIVATE_CHANNEL_UUIDS] ?: "",
            sortChannelMode = preferences[PreferencesKeys.SORT_CHANNEL_MODE] ?: "off"
        )
    }

    suspend fun updateServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_URL] = url
        }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun updatePassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PASSWORD] = password
        }
    }

    suspend fun updateTuples(tuples: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TUPLES] = tuples
        }
    }

    suspend fun updateZeroBasedYAxis(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ZERO_BASED_Y_AXIS] = enabled
        }
    }

    suspend fun updateAutoReload(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_RELOAD] = enabled
        }
    }

    suspend fun updatePrivateChannelUUIDs(uuids: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIVATE_CHANNEL_UUIDS] = uuids
        }
    }

    suspend fun updateSortChannelMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_CHANNEL_MODE] = mode
        }
    }
}
