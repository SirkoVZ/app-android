package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response wrapper for channel data from Volkszähler API
 */
@JsonClass(generateAdapter = true)
data class DataResponseDto(
    @Json(name = "version")
    val version: String?,
    @Json(name = "data")
    val data: ChannelDataDto?
)

/**
 * Channel data DTO containing measurements and statistics
 */
@JsonClass(generateAdapter = true)
data class ChannelDataDto(
    @Json(name = "uuid")
    val uuid: String?,
    @Json(name = "from")
    val from: Long?,
    @Json(name = "to")
    val to: Long?,
    @Json(name = "min")
    val min: List<Double>?,
    @Json(name = "max")
    val max: List<Double>?,
    @Json(name = "average")
    val average: Double?,
    @Json(name = "consumption")
    val consumption: Double?,
    @Json(name = "rows")
    val rows: Int?,
    @Json(name = "tuples")
    val tuples: List<List<Double>>?
)
