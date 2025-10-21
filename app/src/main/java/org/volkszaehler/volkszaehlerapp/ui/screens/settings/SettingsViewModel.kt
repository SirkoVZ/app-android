package org.volkszaehler.volkszaehlerapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.volkszaehler.app.data.local.Settings
import org.volkszaehler.app.data.local.SettingsDataStore
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsDataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Settings()
    )

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.updateServerUrl(url)
        }
    }

    fun updateDefaultPeriod(period: String) {
        viewModelScope.launch {
            settingsDataStore.updateDefaultPeriod(period)
        }
    }

    fun updateChartType(type: String) {
        viewModelScope.launch {
            settingsDataStore.updateChartType(type)
        }
    }

    fun updateEnableCache(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateEnableCache(enabled)
        }
    }

    fun updateCacheDuration(duration: Int) {
        viewModelScope.launch {
            settingsDataStore.updateCacheDuration(duration)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.updateThemeMode(mode)
        }
    }

    fun updateUseDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateUseDynamicColors(enabled)
        }
    }

    fun updateAutoRefresh(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateAutoRefresh(enabled)
        }
    }

    fun updateRefreshInterval(interval: Int) {
        viewModelScope.launch {
            settingsDataStore.updateRefreshInterval(interval)
        }
    }

    fun updateShowGrid(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateShowGrid(show)
        }
    }

    fun updateShowLegend(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateShowLegend(show)
        }
    }
}
