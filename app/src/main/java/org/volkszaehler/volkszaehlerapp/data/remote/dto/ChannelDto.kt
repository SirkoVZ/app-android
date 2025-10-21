package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelResponseDto(
    @Json(name = "version")
    val version: String?,
    @Json(name = "entity")
    val entity: List<ChannelDto>?
)

@JsonClass(generateAdapter = true)
data class ChannelDto(
    @Json(name = "uuid")
    val uuid: String,
    @Json(name = "type")
    val type: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "color")
    val color: String?,
    @Json(name = "style")
    val style: String?,
    @Json(name = "public")
    val isPublic: Boolean?,
    @Json(name = "active")
    val active: Boolean?,
    @Json(name = "children")
    val children: List<ChannelDto>?,
    @Json(name = "properties")
    val properties: PropertiesDto?
)

@JsonClass(generateAdapter = true)
data class PropertiesDto(
    @Json(name = "resolution")
    val resolution: Int?,
    @Json(name = "cost")
    val cost: Double?,
    @Json(name = "unit")
    val unit: String?,
    @Json(name = "initialvalue")
    val initialValue: Double?,
    @Json(name = "tolerance")
    val tolerance: Double?
)
