package org.volkszaehler.volkszaehlerapp.data.remote

import org.volkszaehler.volkszaehlerapp.data.remote.dto.ChannelResponseDto
import org.volkszaehler.volkszaehlerapp.data.remote.dto.DataResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Volkszähler backend
 */
interface VolkszaehlerApi {

    /**
     * Get all channels
     */
    @GET("channel.json")
    suspend fun getChannels(): ChannelResponseDto

    /**
     * Get a specific channel by UUID
     */
    @GET("channel/{uuid}.json")
    suspend fun getChannel(
        @Path("uuid") uuid: String
    ): ChannelResponseDto

    /**
     * Get channel data with optional parameters
     */
    @GET("data/{uuid}.json")
    suspend fun getChannelData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null,
        @Query("group") group: String? = null
    ): DataResponseDto
}
