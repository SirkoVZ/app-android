package org.volkszaehler.volkszaehlerapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain Model für Kanal-Messdaten
 * Enthält die Zeitreihen-Daten eines Kanals
 */
@JsonClass(generateAdapter = true)
data class ChannelData(
    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "from")
    val from: Long,

    @Json(name = "to")
    val to: Long,

    @Json(name = "tuples")
    val tuples: List<DataTuple> = emptyList(),

    // Statistik-Felder (optional, von API bereitgestellt)
    val min: Double? = null,
    val max: Double? = null,
    val average: Double? = null,
    val consumption: Double? = null,
    val rows: Int? = null
) {
    /**
     * Gibt die Anzahl der Datenpunkte zurück
     */
    fun getDataPointCount(): Int = tuples.size

    /**
     * Prüft ob Daten vorhanden sind
     */
    fun hasData(): Boolean = tuples.isNotEmpty()

    /**
     * Gibt den Zeitraum in Millisekunden zurück
     */
    fun getTimeRangeMillis(): Long = to - from

    /**
     * Gibt den Zeitraum in Stunden zurück
     */
    fun getTimeRangeHours(): Long = getTimeRangeMillis() / (1000 * 60 * 60)

    /**
     * Gibt den Zeitraum in Tagen zurück
     */
    fun getTimeRangeDays(): Long = getTimeRangeMillis() / (1000 * 60 * 60 * 24)

    /**
     * Berechnet Statistiken aus den Tuples (falls nicht von API bereitgestellt)
     */
    fun calculateStatistics(): Statistics {
        if (tuples.isEmpty()) {
            return Statistics(
                min = 0.0,
                max = 0.0,
                average = 0.0,
                sum = 0.0,
                count = 0
            )
        }

        val values = tuples.map { it.value }
        return Statistics(
            min = min ?: values.minOrNull() ?: 0.0,
            max = max ?: values.maxOrNull() ?: 0.0,
            average = average ?: values.average(),
            sum = consumption ?: values.sum(),
            count = rows ?: tuples.size
        )
    }

    /**
     * Konvertiert Daten für Chart-Darstellung
     */
    fun toChartData(): List<Pair<Float, Float>> {
        return tuples.map { tuple ->
            Pair(tuple.timestamp.toFloat(), tuple.value.toFloat())
        }
    }

    /**
     * Filtert Daten nach Zeitbereich
     */
    fun filterByTimeRange(fromTimestamp: Long, toTimestamp: Long): ChannelData {
        val filteredTuples = tuples.filter { tuple ->
            tuple.timestamp in fromTimestamp..toTimestamp
        }

        return copy(
            from = fromTimestamp,
            to = toTimestamp,
            tuples = filteredTuples
        )
    }

    /**
     * Reduziert die Anzahl der Datenpunkte durch Sampling
     * Nützlich für Performance bei großen Datensätzen
     */
    fun downsample(targetSize: Int): ChannelData {
        if (tuples.size <= targetSize) return this

        val step = tuples.size / targetSize
        val sampledTuples = tuples.filterIndexed { index, _ ->
            index % step == 0
        }

        return copy(tuples = sampledTuples)
    }
}

/**
 * Datenklasse für Statistiken
 */
data class Statistics(
    val min: Double,
    val max: Double,
    val average: Double,
    val sum: Double,
    val count: Int
) {
    /**
     * Formatiert die Statistiken als String
     */
    fun format(unit: String = "", decimals: Int = 2): String {
        val fmt = "%.${decimals}f"
        return """
            Minimum: ${String.format(fmt, min)} $unit
            Maximum: ${String.format(fmt, max)} $unit
            Durchschnitt: ${String.format(fmt, average)} $unit
            Summe: ${String.format(fmt, sum)} $unit
            Anzahl: $count
        """.trimIndent()
    }
}

/**
 * Extension Functions
 */

/**
 * Gruppiert Daten nach Stunden
 */
fun ChannelData.groupByHour(): Map<Long, List<DataTuple>> {
    return tuples.groupBy { tuple ->
        tuple.timestamp / (1000 * 60 * 60) // Stunden-Timestamp
    }
}

/**
 * Gruppiert Daten nach Tagen
 */
fun ChannelData.groupByDay(): Map<Long, List<DataTuple>> {
    return tuples.groupBy { tuple ->
        tuple.timestamp / (1000 * 60 * 60 * 24) // Tages-Timestamp
    }
}

/**
 * Berechnet gleitenden Durchschnitt
 */
fun ChannelData.movingAverage(windowSize: Int): List<DataTuple> {
    if (tuples.size < windowSize) return tuples

    return tuples.windowed(windowSize, 1) { window ->
        val avgValue = window.map { it.value }.average()
        DataTuple(
            timestamp = window[window.size / 2].timestamp,
            value = avgValue
        )
    }
}