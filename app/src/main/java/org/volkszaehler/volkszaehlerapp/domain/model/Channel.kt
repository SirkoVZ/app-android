package org.volkszaehler.volkszaehlerapp.domain.model

/**
 * Domain model representing a Volkszähler channel
 */
data class Channel(
    val uuid: String,
    val type: String,
    val title: String,
    val description: String?,
    val color: String?,
    val isPublic: Boolean,
    val unit: String,
    val cost: Double?,
    val initialValue: Double?,
    val children: List<Channel>,

    // ✅ NEU: Optional data field for measurements
    val data: ChannelData? = null
)

/**
 * Supported channel types in Volkszähler
 */
enum class ChannelType(val value: String, val displayName: String) {
    POWER("power", "Power"),
    ELECTRIC_METER("electric meter", "Electric Meter"),
    GAS("gas", "Gas"),
    HEAT("heat", "Heat"),
    WATER("water", "Water"),
    TEMPERATURE("temperature", "Temperature"),
    PRESSURE("pressure", "Pressure"),
    HUMIDITY("humidity", "Humidity"),
    WIND_SPEED("windspeed", "Wind Speed"),
    RADIATION("radiation", "Radiation"),
    LUMINOSITY("luminosity", "Luminosity"),
    WORKINGHOURS("workinghours", "Working Hours"),
    VALVE("valve", "Valve"),
    FLOW_RATE("flowrate", "Flow Rate"),
    VOLTAGE("voltage", "Voltage"),
    CURRENT("current", "Current"),
    FREQUENCY("frequency", "Frequency"),
    FILLING_LEVEL("fillinglevel", "Filling Level"),
    UNKNOWN("unknown", "Unknown");

    companion object {
        fun fromString(value: String): ChannelType {
            return entries.find {
                it.value.equals(value, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}

/**
 * Domain model representing channel data with measurements
 */
data class ChannelData(
    val uuid: String,
    val from: Long,
    val to: Long,
    val min: Double?,
    val max: Double?,
    val average: Double?,
    val consumption: Double?,
    val tuples: List<DataTuple>
)

/**
 * Represents a single data point (timestamp, value)
 */
data class DataTuple(
    val timestamp: Long,
    val value: Double
)
