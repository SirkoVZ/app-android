package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PrivateChannelResponse(
    @Json(name = "entity")
    val entity: EntityDto
)
