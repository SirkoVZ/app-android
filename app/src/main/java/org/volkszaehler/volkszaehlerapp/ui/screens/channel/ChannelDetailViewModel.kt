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
import org.volkszaehler.volkszaehlerapp.util.Result
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

    /**
     * Loads channel information (without data)
     */
    fun loadChannel(uuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getChannel(uuid).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _channel.value = result.data
                        _isLoading.value = false
                    }
                    is Result.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    /**
     * Loads channel data (measurements)
     */
    fun loadChannelData(uuid: String, from: Long? = null, to: Long? = null, tuples: Int? = null) {
        viewModelScope.launch {
            repository.getChannelData(uuid, from, to, tuples).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // ✅ getChannelData gibt jetzt ChannelData zurück (nicht Channel!)
                        _channelData.value = result.data
                    }
                    is Result.Error -> {
                        _error.value = result.message
                    }
                    is Result.Loading -> {
                        // Optional: separate loading state für data
                    }
                }
            }
        }
    }
}
