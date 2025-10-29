package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response DTO for /entity/{uuid}/data.json
 * Contains actual measurement data (tuples, min, max, etc.)
 */
@JsonClass(generateAdapter = true)
data class ChannelDataResponseDto(
    @Json(name = "data")
    val data: DataDto
)

/**
 * Data DTO containing measurements
 */
@JsonClass(generateAdapter = true)
data class DataDto(
    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "from")
    val from: Long,

    @Json(name = "to")
    val to: Long,

    @Json(name = "min")
    val min: List<Double>?,  // [timestamp, value]

    @Json(name = "max")
    val max: List<Double>?,  // [timestamp, value]

    @Json(name = "average")
    val average: Double?,

    @Json(name = "consumption")
    val consumption: Double?,

    @Json(name = "tuples")
    val tuples: List<List<Double>>  // [[timestamp, value], ...]
)
