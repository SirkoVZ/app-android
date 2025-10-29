package org.volkszaehler.volkszaehlerapp.data.remote.dto

import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.model.DataTuple


/**
 * Extension function to convert EntityDto (public/private entities) to Domain Model
 */
fun EntityDto.toChannel(): Channel {
    return Channel(
        uuid = uuid,
        title = title ?: "Unnamed Channel",
        description = description,
        type = type,
        color = color,
        isPublic = public ?: false,
        unit = unit ?: deriveUnit(type),
        cost = cost,
        initialValue = initialValue,
        children = children?.map { it.toChannel() } ?: emptyList(),
        data = null // Entities haben keine Messdaten
    )
}

/**
 * Extension function to convert ChannelDto to Domain Model
 */
fun ChannelDto.toChannel(): Channel {
    return Channel(
        uuid = uuid,
        title = title ?: "Unnamed Channel",
        description = description,
        type = type ?: "unknown",
        color = color,
        isPublic = isPublic ?: false,
        unit = deriveUnit(type),
        cost = cost,
        initialValue = initialConsumption,
        children = children?.map { it.toChannel() } ?: emptyList(),
        data = null // ChannelDto hat KEIN data Feld (kommt separat via /data API)
    )
}

/**
 * Derive unit from channel type
 */
private fun deriveUnit(type: String?): String {
    return when (type?.lowercase()) {
        "temperature" -> "°C"
        "humidity" -> "%"
        "power", "powersensor" -> "W"
        "electric meter" -> "kWh"
        "gas", "gas meter" -> "m³"
        "voltage" -> "V"
        "current" -> "A"
        "watertotal" -> "L"
        "windspeed" -> "km/h"
        "heattotal" -> "kWh"
        "workinghours", "workinghourssensor" -> "h"
        "filllevel" -> "%"
        "universalsensor" -> ""
        else -> ""
    }
}


/**
 * Extension function to convert DataDto to ChannelData (Domain Model)
 */
fun DataDto.toChannelData(): ChannelData {
    return ChannelData(
        uuid = uuid,
        from = from,
        to = to,
        min = min?.getOrNull(1), // [timestamp, value] → take value
        max = max?.getOrNull(1), // [timestamp, value] → take value
        average = average,
        consumption = consumption,
        tuples = tuples.mapNotNull { tuple ->
            // Tuple format: [timestamp, value]
            if (tuple.size >= 2) {
                DataTuple(
                    timestamp = tuple[0].toLong(),
                    value = tuple[1]
                )
            } else null
        }
    )
}
