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
import org.volkszaehler.volkszaehlerapp.data.local.Settings
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import org.volkszaehler.volkszaehlerapp.util.Result
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsDataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Settings()
    )

    private val _fetchChannelsState = MutableStateFlow<FetchChannelsState>(FetchChannelsState.Idle)
    val fetchChannelsState: StateFlow<FetchChannelsState> = _fetchChannelsState.asStateFlow()

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.updateServerUrl(url)
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            settingsDataStore.updateUsername(username)
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            settingsDataStore.updatePassword(password)
        }
    }

    fun updateTuples(tuples: Int) {
        viewModelScope.launch {
            settingsDataStore.updateTuples(tuples)
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

    fun updatePrivateChannelUUIDs(uuids: String) {
        viewModelScope.launch {
            settingsDataStore.updatePrivateChannelUUIDs(uuids)
        }
    }

    fun updateSortChannelMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.updateSortChannelMode(mode)
        }
    }

    fun fetchChannels() {
        viewModelScope.launch {
            _fetchChannelsState.value = FetchChannelsState.Loading

            val currentSettings = settings.value

            channelRepository.fetchAllChannels(currentSettings.privateChannelUUIDs)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _fetchChannelsState.value = FetchChannelsState.Loading
                        }
                        is Result.Success -> {
                            _fetchChannelsState.value = FetchChannelsState.Success(
                                message = "Successfully fetched ${result.data.size} channels"
                            )
                        }
                        is Result.Error -> {
                            _fetchChannelsState.value = FetchChannelsState.Error(
                                message = result.message
                            )
                        }
                    }
                }
        }
    }

    fun resetFetchChannelsState() {
        _fetchChannelsState.value = FetchChannelsState.Idle
    }
}

sealed class FetchChannelsState {
    object Idle : FetchChannelsState()
    object Loading : FetchChannelsState()
    data class Success(val message: String) : FetchChannelsState()
    data class Error(val message: String) : FetchChannelsState()
}
