// SettingsViewModel.kt - KORRIGIERTE VERSION
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/ui/viewmodel/SettingsViewModel.kt

package org.volkszaehler.volkszaehlerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore.SortMode
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    data class SettingsUiState(
        val serverUrl: String = "",
        val username: String = "",
        val password: String = "",
        val privateChannelUUIDs: String = "",
        val zeroBasedYAxis: Boolean = false,
        val autoReload: Boolean = false,
        val sortMode: SortMode = SortMode.NAME,
        val tuplesLimit: Int = 1000,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val errorMessage: String? = null
    )

    // ========== LÖSUNG 1: Einzelne Flows (Einfach) ==========

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.serverUrl,
        settingsDataStore.username,
        settingsDataStore.password,
        settingsDataStore.privateChannelUUIDs,
        settingsDataStore.zeroBasedYAxis
    ) { serverUrl, username, password, uuids, zeroY ->
        SettingsUiState(
            serverUrl = serverUrl,
            username = username,
            password = password,
            privateChannelUUIDs = uuids.joinToString(", "),
            zeroBasedYAxis = zeroY
        )
    }.combine(settingsDataStore.autoReload) { state, autoReload ->
        state.copy(autoReload = autoReload)
    }.combine(settingsDataStore.sortChannelsMode) { state, sortMode ->
        state.copy(sortMode = sortMode)
    }.combine(settingsDataStore.tuplesLimit) { state, limit ->
        state.copy(tuplesLimit = limit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    // ========== UPDATE FUNCTIONS ==========

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.setServerUrl(url)
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            settingsDataStore.setUsername(username)
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            settingsDataStore.setPassword(password)
        }
    }

    fun updatePrivateChannelUUIDs(uuids: String) {
        viewModelScope.launch {
            val uuidList = uuids.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            settingsDataStore.setPrivateChannelUUIDs(uuidList)
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

    fun updateSortMode(mode: SortMode) {
        viewModelScope.launch {
            settingsDataStore.setSortChannelsMode(mode)
        }
    }

    fun updateTuplesLimit(limit: Int) {
        viewModelScope.launch {
            settingsDataStore.setTuplesLimit(limit)
        }
    }

    // ========== TEST CONNECTION ==========

    private val _testState = MutableStateFlow<TestState>(TestState.Idle)
    val testState: StateFlow<TestState> = _testState.asStateFlow()

    sealed class TestState {
        object Idle : TestState()
        object Testing : TestState()
        object Success : TestState()
        data class Error(val message: String) : TestState()
    }

    fun testConnection() {
        viewModelScope.launch {
            try {
                _testState.value = TestState.Testing

                // TODO: Implementiere API-Test
                kotlinx.coroutines.delay(1000)

                _testState.value = TestState.Success

                // Reset nach 2 Sekunden
                kotlinx.coroutines.delay(2000)
                _testState.value = TestState.Idle

            } catch (e: Exception) {
                _testState.value = TestState.Error(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            settingsDataStore.clearAll()
        }
    }
}