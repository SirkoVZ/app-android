// DataTuple.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/data/model/DataTuple.kt

package org.volkszaehler.volkszaehlerapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a single data point (tuple) from the Volkszähler API.
 * Each tuple contains a timestamp and a corresponding value.
 *
 * Example JSON:
 * [1234567890000, 123.45]
 *
 * @property timestamp Unix timestamp in milliseconds
 * @property value The measured value at this timestamp
 */
@JsonClass(generateAdapter = true)
data class DataTuple(
    @Json(name = "0")
    val timestamp: Long,

    @Json(name = "1")
    val value: Double
) {
    /**
     * Converts the Unix timestamp to a human-readable date string.
     *
     * @return Formatted date string (e.g., "2024-01-15 14:30")
     */
    fun getFormattedDate(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    /**
     * Converts the Unix timestamp to a short time string.
     *
     * @return Formatted time string (e.g., "14:30")
     */
    fun getFormattedTime(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    /**
     * Converts the Unix timestamp to a short date string.
     *
     * @return Formatted date string (e.g., "15.01.2024")
     */
    fun getFormattedShortDate(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        return format.format(date)
    }

    /**
     * Rounds the value to a specified number of decimal places.
     *
     * @param decimals Number of decimal places (default: 2)
     * @return Rounded value
     */
    fun getRoundedValue(decimals: Int = 2): Double {
        val multiplier = Math.pow(10.0, decimals.toDouble())
        return Math.round(value * multiplier) / multiplier
    }

    companion object {
        /**
         * Creates a DataTuple from a JSON array.
         * The Volkszähler API returns data points as arrays: [timestamp, value]
         *
         * @param array JSON array containing timestamp and value
         * @return DataTuple instance
         */
        fun fromArray(array: List<Any>): DataTuple? {
            return try {
                if (array.size >= 2) {
                    val timestamp = when (val ts = array[0]) {
                        is Number -> ts.toLong()
                        else -> return null
                    }
                    val value = when (val v = array[1]) {
                        is Number -> v.toDouble()
                        else -> return null
                    }
                    DataTuple(timestamp, value)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Extension function to convert a list of DataTuples to chart-compatible data.
 *
 * @return List of Pair<Float, Float> where first is timestamp (as float) and second is value
 */
fun List<DataTuple>.toChartData(): List<Pair<Float, Float>> {
    return this.map { tuple ->
        Pair(tuple.timestamp.toFloat(), tuple.value.toFloat())
    }
}

/**
 * Extension function to calculate statistics from a list of DataTuples.
 *
 * @return DataStatistics object containing min, max, average, and sum
 */
fun List<DataTuple>.calculateStatistics(): DataStatistics {
    if (this.isEmpty()) {
        return DataStatistics(0.0, 0.0, 0.0, 0.0, 0)
    }

    val values = this.map { it.value }
    return DataStatistics(
        min = values.minOrNull() ?: 0.0,
        max = values.maxOrNull() ?: 0.0,
        average = values.average(),
        sum = values.sum(),
        count = this.size
    )
}

/**
 * Data class representing statistical information about a dataset.
 */
data class DataStatistics(
    val min: Double,
    val max: Double,
    val average: Double,
    val sum: Double,
    val count: Int
) {
    /**
     * Formats the statistics as a human-readable string.
     */
    fun format(decimals: Int = 2): String {
        return """
            Min: ${String.format("%.${decimals}f", min)}
            Max: ${String.format("%.${decimals}f", max)}
            Avg: ${String.format("%.${decimals}f", average)}
            Sum: ${String.format("%.${decimals}f", sum)}
            Count: $count
        """.trimIndent()
    }
}