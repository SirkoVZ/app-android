package org.volkszaehler.volkszaehlerapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val DEFAULT_PERIOD = stringPreferencesKey("default_period")
        val CHART_TYPE = stringPreferencesKey("chart_type")
        val ENABLE_CACHE = booleanPreferencesKey("enable_cache")
        val CACHE_DURATION = intPreferencesKey("cache_duration")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
        val SHOW_GRID = booleanPreferencesKey("show_grid")
        val SHOW_LEGEND = booleanPreferencesKey("show_legend")
    }

    val settings: Flow<Settings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Settings(
                serverUrl = preferences[PreferencesKeys.SERVER_URL] ?: "",
                defaultPeriod = preferences[PreferencesKeys.DEFAULT_PERIOD] ?: "day",
                chartType = preferences[PreferencesKeys.CHART_TYPE] ?: "line",
                enableCache = preferences[PreferencesKeys.ENABLE_CACHE] ?: true,
                cacheDuration = preferences[PreferencesKeys.CACHE_DURATION] ?: 300,
                themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "system",
                useDynamicColors = preferences[PreferencesKeys.USE_DYNAMIC_COLORS] ?: true,
                autoRefresh = preferences[PreferencesKeys.AUTO_REFRESH] ?: false,
                refreshInterval = preferences[PreferencesKeys.REFRESH_INTERVAL] ?: 30,
                showGrid = preferences[PreferencesKeys.SHOW_GRID] ?: true,
                showLegend = preferences[PreferencesKeys.SHOW_LEGEND] ?: true
            )
        }

    suspend fun updateServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_URL] = url
        }
    }

    suspend fun updateDefaultPeriod(period: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PERIOD] = period
        }
    }

    suspend fun updateChartType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CHART_TYPE] = type
        }
    }

    suspend fun updateEnableCache(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_CACHE] = enabled
        }
    }

    suspend fun updateCacheDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CACHE_DURATION] = duration
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun updateUseDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun updateAutoRefresh(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH] = enabled
        }
    }

    suspend fun updateRefreshInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REFRESH_INTERVAL] = interval
        }
    }

    suspend fun updateShowGrid(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_GRID] = show
        }
    }

    suspend fun updateShowLegend(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_LEGEND] = show
        }
    }
}

data class Settings(
    val serverUrl: String = "",
    val defaultPeriod: String = "day",
    val chartType: String = "line",
    val enableCache: Boolean = true,
    val cacheDuration: Int = 300,
    val themeMode: String = "system",
    val useDynamicColors: Boolean = true,
    val autoRefresh: Boolean = false,
    val refreshInterval: Int = 30,
    val showGrid: Boolean = true,
    val showLegend: Boolean = true
)
