package org.volkszaehler.volkszaehlerapp.data.remote.dto

import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.model.DataTuple

/**
 * Extension function to convert DataResponseDto to ChannelData
 */
fun DataResponseDto.toChannelData(): ChannelData? {
    return data?.toChannelData()
}

/**
 * Extension function to convert ChannelDataDto to ChannelData
 */
fun ChannelDataDto.toChannelData(): ChannelData {
    return ChannelData(
        uuid = uuid ?: "",
        from = from ?: 0L,
        to = to ?: 0L,
        min = min?.firstOrNull(),
        max = max?.firstOrNull(),
        average = average,
        consumption = consumption,
        tuples = tuples?.mapNotNull { tuple ->
            if (tuple.size >= 2) {
                DataTuple(
                    timestamp = tuple[0].toLong(),
                    value = tuple[1]
                )
            } else null
        } ?: emptyList()
    )
}
