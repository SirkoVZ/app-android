package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.volkszaehler.volkszaehlerapp.data.model.Channel
import org.volkszaehler.volkszaehlerapp.data.model.ChannelData
import org.volkszaehler.volkszaehlerapp.data.model.DataTuple

/**
 * API Response DTOs (Data Transfer Objects)
 * Diese Klassen repräsentieren die exakte Struktur der Volkszähler API Responses
 */

// Entity List Response
@JsonClass(generateAdapter = true)
data class EntityListResponse(
    @Json(name = "version") val version: String?,
    @Json(name = "entity") val entity: List<EntityDto>?
)

// Entity DTO
@JsonClass(generateAdapter = true)
data class EntityDto(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "title") val title: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "unit") val unit: String?,
    @Json(name = "color") val color: String?,
    @Json(name = "cost") val cost: Double?,
    @Json(name = "description") val description: String?,
    @Json(name = "children") val children: List<EntityDto>?,
    @Json(name = "public") val isPublic: Boolean?
) {
    /**
     * Konvertiert EntityDto zu Domain Model Channel
     */
    fun toChannel(): Channel {
        return Channel(
            uuid = uuid,
            title = title ?: "Unknown",
            type = type ?: "unknown",
            unit = unit,
            color = color,
            cost = cost,
            description = description,
            isGroup = !children.isNullOrEmpty(),
            isChecked = true,
            lastValue = null,
            lastTimestamp = null
        )
    }

    /**
     * Rekursiv alle Channels aus der Hierarchie extrahieren
     * Gruppen werden übersprungen, nur echte Kanäle werden zurückgegeben
     */
    fun getAllChannels(): List<Channel> {
        val channels = mutableListOf<Channel>()

        // Nur hinzufügen wenn es kein Gruppen-Element ist
        if (children.isNullOrEmpty()) {
            channels.add(toChannel())
        }

        // Rekursiv durch Kinder iterieren
        children?.forEach { child ->
            channels.addAll(child.getAllChannels())
        }

        return channels
    }

    /**
     * Alle Channels inklusive Gruppen extrahieren
     */
    fun getAllChannelsIncludingGroups(): List<Channel> {
        val channels = mutableListOf<Channel>()
        channels.add(toChannel())

        children?.forEach { child ->
            channels.addAll(child.getAllChannelsIncludingGroups())
        }

        return channels
    }
}

// Data Response
@JsonClass(generateAdapter = true)
data class DataResponse(
    @Json(name = "version") val version: String?,
    @Json(name = "data") val data: DataDto
)

@JsonClass(generateAdapter = true)
data class DataDto(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "from") val from: Long,
    @Json(name = "to") val to: Long,
    @Json(name = "min") val min: List<Double>?,
    @Json(name = "max") val max: List<Double>?,
    @Json(name = "average") val average: Double?,
    @Json(name = "consumption") val consumption: Double?,
    @Json(name = "tuples") val tuples: List<List<Any>>?,
    @Json(name = "rows") val rows: Int?
) {
    /**
     * Konvertiert DataDto zu Domain Model ChannelData
     */
    fun toChannelData(): ChannelData {
        val parsedTuples = tuples?.mapNotNull { tuple ->
            try {
                if (tuple.size >= 2) {
                    val timestamp = (tuple[0] as? Number)?.toLong() ?: return@mapNotNull null
                    val value = (tuple[1] as? Number)?.toDouble() ?: return@mapNotNull null
                    DataTuple(timestamp, value)
                } else null
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()

        return ChannelData(
            uuid = uuid,
            from = from,
            to = to,
            tuples = parsedTuples,
            min = min?.getOrNull(1),
            max = max?.getOrNull(1),
            average = average,
            consumption = consumption,
            rows = rows
        )
    }
}

/**
 * Extension Functions für einfachere Konvertierung
 */

/**
 * Konvertiert eine Liste von EntityDto zu Channel Domain Models
 */
fun List<EntityDto>.toChannels(): List<Channel> {
    return this.flatMap { it.getAllChannels() }
}

/**
 * Konvertiert EntityListResponse zu Channel Liste
 */
fun EntityListResponse.toChannels(): List<Channel> {
    return entity?.toChannels() ?: emptyList()
}