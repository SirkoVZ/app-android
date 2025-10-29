package org.volkszaehler.volkszaehlerapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefinitionsResponse(
    @Json(name = "capabilities")
    val capabilities: DefinitionsCapabilities
)

@JsonClass(generateAdapter = true)
data class DefinitionsCapabilities(
    @Json(name = "definitions")
    val definitions: Map<String, Any>
)
