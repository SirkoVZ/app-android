package org.volkszaehler.volkszaehlerapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import org.volkszaehler.volkszaehlerapp.domain.model.AppSettings
import org.volkszaehler.volkszaehlerapp.domain.model.SortMode
import javax.inject.Inject

/**
 * ViewModel for Settings Screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.settings.collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.updateServerUrl(url)
        }
    }

    fun updateAuth(username: String, password: String) {
        viewModelScope.launch {
            settingsDataStore.updateAuth(username, password)
        }
    }

    fun updatePrivateChannelUuids(uuids: String) {
        viewModelScope.launch {
            settingsDataStore.updatePrivateChannelUuids(uuids)
        }
    }

    fun updateZeroBasedYAxis(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateZeroBasedYAxis(enabled)
        }
    }

    fun updateAutoReload(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateAutoReload(enabled)
        }
    }

    fun updateSortChannelMode(mode: SortMode) {
        viewModelScope.launch {
            settingsDataStore.updateSortChannelMode(mode)
        }
    }

    fun updateTuples(tuples: Int) {
        viewModelScope.launch {
            settingsDataStore.updateTuples(tuples)
        }
    }

    fun clearAllSettings() {
        viewModelScope.launch {
            settingsDataStore.clearAll()
        }
    }
}
