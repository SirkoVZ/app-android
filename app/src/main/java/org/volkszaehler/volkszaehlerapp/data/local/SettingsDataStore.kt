package org.volkszaehler.volkszaehlerapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val PRIVATE_CHANNELS = stringPreferencesKey("private_channels")
        private val ZERO_BASED_Y_AXIS = booleanPreferencesKey("zero_based_y_axis")
        private val AUTO_RELOAD = booleanPreferencesKey("auto_reload")
        private val SORT_MODE = intPreferencesKey("sort_mode")
        private val TUPLES_LIMIT = intPreferencesKey("tuples_limit")
    }

    // Server URL
    val serverUrl: Flow<String?> = dataStore.data.map { it[SERVER_URL] }
    suspend fun setServerUrl(url: String) {
        dataStore.edit { it[SERVER_URL] = url }
    }
    fun getServerUrl(): String? = runBlocking { serverUrl.first() }

    // Username
    val username: Flow<String?> = dataStore.data.map { it[USERNAME] }
    suspend fun setUsername(user: String) {
        dataStore.edit { it[USERNAME] = user }
    }
    fun getUsername(): String? = runBlocking { username.first() }

    // Password
    val password: Flow<String?> = dataStore.data.map { it[PASSWORD] }
    suspend fun setPassword(pass: String) {
        dataStore.edit { it[PASSWORD] = pass }
    }
    fun getPassword(): String? = runBlocking { password.first() }

    // Private Channels (comma-separated UUIDs)
    val privateChannels: Flow<String?> = dataStore.data.map { it[PRIVATE_CHANNELS] }
    suspend fun setPrivateChannels(channels: String) {
        dataStore.edit { it[PRIVATE_CHANNELS] = channels }
    }

    // Zero-based Y-Axis
    val zeroBasedYAxis: Flow<Boolean> = dataStore.data.map { it[ZERO_BASED_Y_AXIS] ?: false }
    suspend fun setZeroBasedYAxis(enabled: Boolean) {
        dataStore.edit { it[ZERO_BASED_Y_AXIS] = enabled }
    }

    // Auto Reload
    val autoReload: Flow<Boolean> = dataStore.data.map { it[AUTO_RELOAD] ?: false }
    suspend fun setAutoReload(enabled: Boolean) {
        dataStore.edit { it[AUTO_RELOAD] = enabled }
    }

    // Sort Mode (0 = Off, 1 = Groups, 2 = All)
    val sortMode: Flow<Int> = dataStore.data.map { it[SORT_MODE] ?: 0 }
    suspend fun setSortMode(mode: Int) {
        dataStore.edit { it[SORT_MODE] = mode }
    }

    // Tuples Limit
    val tuplesLimit: Flow<Int> = dataStore.data.map { it[TUPLES_LIMIT] ?: 1000 }
    suspend fun setTuplesLimit(limit: Int) {
        dataStore.edit { it[TUPLES_LIMIT] = limit }
    }

    // Clear all settings
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}