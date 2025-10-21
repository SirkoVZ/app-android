package org.volkszaehler.volkszaehlerapp.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import javax.inject.Inject

/**
 * ViewModel for Channel List Screen
 */
@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getChannels().collect { result ->
                result.fold(
                    onSuccess = { channelList ->
                        _channels.value = channelList
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to load channels"
                        _isLoading.value = false
                    }
                )
            }
        }
    }
}
