package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntitiesResponse(
    @Json(name = "version")
    val version: String?,

    @Json(name = "entities")
    val entities: List<EntityDto>?
)

/**
 * EntityDto - Vollständige Struktur für Public & Private Entities
 * ⚠️ WICHTIG: Alle Felder sind optional, da verschiedene Entity-Typen unterschiedliche Felder haben
 */
@JsonClass(generateAdapter = true)
data class EntityDto(
    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "title")
    val title: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "color")
    val color: String?,

    @Json(name = "style")
    val style: String?,

    @Json(name = "public")
    val public: Boolean?,

    @Json(name = "active")
    val active: Boolean?,

    @Json(name = "resolution")
    val resolution: Double?,

    @Json(name = "cost")
    val cost: Double?,

    @Json(name = "initialconsumption")
    val initialValue: Double?,

    @Json(name = "tolerance")
    val tolerance: Double?,

    @Json(name = "fillstyle")
    val fillStyle: Double?,

    @Json(name = "linestyle")
    val lineStyle: String?,

    @Json(name = "linewidth")
    val lineWidth: Int?,

    @Json(name = "yaxis")
    val yAxis: String?,

    @Json(name = "unit")
    val unit: String?,

    @Json(name = "children")
    val children: List<EntityDto>?
)
