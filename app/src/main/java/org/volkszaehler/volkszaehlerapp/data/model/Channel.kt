package org.volkszaehler.volkszaehlerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain Model für einen Volkszähler-Kanal
 * Wird sowohl für API-Responses als auch für Room Database verwendet
 */
@Entity(tableName = "channels")
@JsonClass(generateAdapter = true)
data class Channel(
    @PrimaryKey
    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "unit")
    val unit: String? = null,

    @Json(name = "color")
    val color: String? = null,

    @Json(name = "cost")
    val cost: Double? = null,

    @Json(name = "description")
    val description: String? = null,

    // UI-spezifische Felder (nicht von API)
    val isGroup: Boolean = false,
    val isChecked: Boolean = true,
    val lastValue: Double? = null,
    val lastTimestamp: Long? = null
) {
    /**
     * Gibt einen formatierten Display-Namen zurück
     */
    fun getDisplayName(): String {
        return if (title.isNotBlank()) title else "Channel $uuid"
    }

    /**
     * Gibt die Einheit mit Fallback zurück
     */
    fun getUnitOrDefault(): String {
        return unit ?: "kWh"
    }

    /**
     * Prüft ob der Kanal ein Sensor ist
     */
    fun isSensor(): Boolean {
        return type.contains("sensor", ignoreCase = true)
    }

    /**
     * Prüft ob der Kanal ein Zähler ist
     */
    fun isCounter(): Boolean {
        return type.contains("counter", ignoreCase = true) ||
                type.contains("meter", ignoreCase = true)
    }

    /**
     * Gibt einen formatierten String mit allen Informationen zurück
     */
    fun getInfoString(): String {
        return buildString {
            append("Title: $title\n")
            append("UUID: $uuid\n")
            append("Type: $type\n")
            unit?.let { append("Unit: $it\n") }
            color?.let { append("Color: $it\n") }
            cost?.let { append("Cost: $it\n") }
            description?.let { append("Description: $it\n") }
            if (isGroup) append("Is Group: Yes\n")
            lastValue?.let { append("Last Value: $it\n") }
            lastTimestamp?.let {
                val date = java.util.Date(it)
                append("Last Update: $date\n")
            }
        }
    }

    /**
     * Erstellt eine Kopie mit aktualisierten Werten
     */
    fun updateLastReading(value: Double, timestamp: Long): Channel {
        return copy(
            lastValue = value,
            lastTimestamp = timestamp
        )
    }
}

/**
 * Extension function für Filterung
 */
fun List<Channel>.filterByQuery(query: String): List<Channel> {
    if (query.isBlank()) return this

    return filter { channel ->
        channel.title.contains(query, ignoreCase = true) ||
                channel.uuid.contains(query, ignoreCase = true) ||
                channel.type.contains(query, ignoreCase = true) ||
                channel.description?.contains(query, ignoreCase = true) == true
    }
}

/**
 * Extension function zum Filtern nach Typ
 */
fun List<Channel>.filterByType(type: String): List<Channel> {
    return filter { it.type.equals(type, ignoreCase = true) }
}

/**
 * Extension function zum Ausschließen von Gruppen
 */
fun List<Channel>.excludeGroups(): List<Channel> {
    return filter { !it.isGroup }
}

/**
 * Extension function zum Sortieren nach Titel
 */
fun List<Channel>.sortedByTitle(): List<Channel> {
    return sortedBy { it.title.lowercase() }
}

/**
 * Extension function zum Sortieren nach letztem Update
 */
fun List<Channel>.sortedByLastUpdate(): List<Channel> {
    return sortedByDescending { it.lastTimestamp ?: 0L }
}