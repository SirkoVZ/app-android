package org.volkszaehler.volkszaehlerapp.data.remote

import org.volkszaehler.volkszaehlerapp.data.remote.dto.ChannelDataResponseDto
import org.volkszaehler.volkszaehlerapp.data.remote.dto.EntitiesResponse
import org.volkszaehler.volkszaehlerapp.data.remote.dto.DefinitionsResponse
import org.volkszaehler.volkszaehlerapp.data.remote.dto.PrivateChannelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VolkszaehlerApi {

    /**
     * Get channel data/measurements
     * GET /entity/{uuid}/data.json
     * ✅ Returns ChannelDataResponseDto (not ChannelDto!)
     */
    @GET("entity/{uuid}/data.json")
    suspend fun getChannelData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null,
        @Query("tuples") tuples: Int? = null
    ): Response<ChannelDataResponseDto>

    /**
     * Get all public entities/channels
     * GET /entity.json
     */
    @GET("entity.json")
    suspend fun getChannels(): Response<EntitiesResponse>

    /**
     * Get a single channel by UUID (info only, no data)
     * GET /entity/{uuid}.json
     */
    @GET("entity/{uuid}.json")
    suspend fun getChannel(
        @Path("uuid") uuid: String
    ): Response<PrivateChannelResponse>

    /**
     * Get capability definitions (units, etc.)
     * GET /capabilities/definitions/entities.json
     */
    @GET("capabilities/definitions/entities.json")
    suspend fun getDefinitions(): Response<DefinitionsResponse>
}
