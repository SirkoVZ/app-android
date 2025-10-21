package org.volkszaehler.volkszaehlerapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val serverUrl: StateFlow<String?> = settingsDataStore.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val username: StateFlow<String?> = settingsDataStore.username
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val password: StateFlow<String?> = settingsDataStore.password
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val zeroBasedYAxis: StateFlow<Boolean> = settingsDataStore.zeroBasedYAxis
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoReload: StateFlow<Boolean> = settingsDataStore.autoReload
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val sortMode: StateFlow<Int> = settingsDataStore.sortMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val tuplesLimit: StateFlow<Int> = settingsDataStore.tuplesLimit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1000)

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.setServerUrl(url)
        }
    }

    fun updateUsername(user: String) {
        viewModelScope.launch {
            settingsDataStore.setUsername(user)
        }
    }

    fun updatePassword(pass: String) {
        viewModelScope.launch {
            settingsDataStore.setPassword(pass)
        }
    }

    fun updateZeroBasedYAxis(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setZeroBasedYAxis(enabled)
        }
    }

    fun updateAutoReload(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAutoReload(enabled)
        }
    }

    fun updateSortMode(mode: Int) {
        viewModelScope.launch {
            settingsDataStore.setSortMode(mode)
        }
    }

    fun updateTuplesLimit(limit: Int) {
        viewModelScope.launch {
            settingsDataStore.setTuplesLimit(limit)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            try {
                // Settings are automatically saved via DataStore
                _saveStatus.value = SaveStatus.Success
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearSettings() {
        viewModelScope.launch {
            settingsDataStore.clearAll()
        }
    }
}

sealed class SaveStatus {
    data object Idle : SaveStatus()
    data object Saving : SaveStatus()
    data object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}