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
import org.volkszaehler.volkszaehlerapp.util.Result
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
            repository.getChannels().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _channels.value = result.data
                        _isLoading.value = false
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                }
            }
        }
    }
}
