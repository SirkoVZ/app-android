package org.volkszaehler.volkszaehlerapp.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import javax.inject.Inject

/**
 * ViewModel for Channel Detail Screen
 */
@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _channel = MutableStateFlow<Channel?>(null)
    val channel: StateFlow<Channel?> = _channel.asStateFlow()

    private val _channelData = MutableStateFlow<ChannelData?>(null)
    val channelData: StateFlow<ChannelData?> = _channelData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadChannel(uuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getChannel(uuid).collect { result ->
                result.fold(
                    onSuccess = { channel ->
                        _channel.value = channel
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to load channel"
                        _isLoading.value = false
                    }
                )
            }
        }
    }

    fun loadChannelData(uuid: String) {
        viewModelScope.launch {
            repository.getChannelData(uuid).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _channelData.value = data
                    },
                    onFailure = { exception ->
                        // Optional: Handle data loading error separately
                        _error.value = exception.message ?: "Failed to load data"
                    }
                )
            }
        }
    }
}
