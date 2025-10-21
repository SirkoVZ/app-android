package org.volkszaehler.volkszaehlerapp.ui.channellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.data.model.Channel
import org.volkszaehler.volkszaehlerapp.data.repository.VolkszaehlerRepository
import org.volkszaehler.volkszaehlerapp.util.NetworkResult
import timber.log.Timber
import javax.inject.Inject

data class ChannelListUiState(
    val channels: List<Channel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val repository: VolkszaehlerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelListUiState())
    val uiState: StateFlow<ChannelListUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getChannels(forceRefresh).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                channels = result.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                        Timber.d("Loaded ${result.data?.size} channels")
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        Timber.e("Error loading channels: ${result.message}")
                    }
                }
            }
        }
    }

    fun toggleChannelChecked(channel: Channel) {
        viewModelScope.launch {
            val newCheckedState = !channel.isChecked
            repository.updateChannelCheckedStatus(channel.uuid, newCheckedState)

            // Update local state
            _uiState.update { state ->
                state.copy(
                    channels = state.channels.map {
                        if (it.uuid == channel.uuid) {
                            it.copy(isChecked = newCheckedState)
                        } else {
                            it
                        }
                    }
                )
            }
            Timber.d("Toggled channel ${channel.title}: $newCheckedState")
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    val filteredChannels: StateFlow<List<Channel>> = _uiState.map { state ->
        if (state.searchQuery.isBlank()) {
            state.channels
        } else {
            state.channels.filter { channel ->
                channel.title.contains(state.searchQuery, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}