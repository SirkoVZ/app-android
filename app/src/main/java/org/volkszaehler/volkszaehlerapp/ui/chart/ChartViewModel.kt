package org.volkszaehler.volkszaehlerapp.ui.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.volkszaehler.volkszaehlerapp.data.model.ChannelData
import org.volkszaehler.volkszaehlerapp.data.repository.VolkszaehlerRepository
import org.volkszaehler.volkszaehlerapp.util.NetworkResult
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ChartUiState(
    val channelData: ChannelData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val timeRange: TimeRange = TimeRange.DAY,
    val channelUuid: String = ""
)

enum class TimeRange(val label: String, val milliseconds: Long) {
    HOUR("1 Stunde", TimeUnit.HOURS.toMillis(1)),
    DAY("1 Tag", TimeUnit.DAYS.toMillis(1)),
    WEEK("1 Woche", TimeUnit.DAYS.toMillis(7)),
    MONTH("1 Monat", TimeUnit.DAYS.toMillis(30)),
    YEAR("1 Jahr", TimeUnit.DAYS.toMillis(365))
}

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: VolkszaehlerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val channelUuid: String = savedStateHandle.get<String>("channelUuid") ?: ""

    private val _uiState = MutableStateFlow(ChartUiState(channelUuid = channelUuid))
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        if (channelUuid.isNotEmpty()) {
            loadData()
        }
    }

    fun loadData(timeRange: TimeRange = _uiState.value.timeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(timeRange = timeRange) }

            val now = System.currentTimeMillis()
            val from = now - timeRange.milliseconds

            repository.getChannelData(
                uuid = channelUuid,
                from = from,
                to = now,
                group = getGrouping(timeRange)
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                channelData = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        Timber.d("Loaded ${result.data?.tuples?.size} data points")
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        Timber.e("Error loading data: ${result.message}")
                    }
                }
            }
        }
    }

    private fun getGrouping(timeRange: TimeRange): String? {
        return when (timeRange) {
            TimeRange.HOUR -> null
            TimeRange.DAY -> "hour"
            TimeRange.WEEK -> "day"
            TimeRange.MONTH -> "day"
            TimeRange.YEAR -> "week"
        }
    }

    fun changeTimeRange(timeRange: TimeRange) {
        loadData(timeRange)
    }
}