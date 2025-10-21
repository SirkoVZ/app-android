package org.volkszaehler.volkszaehlerapp.data.remote.api

import org.volkszaehler.volkszaehlerapp.data.remote.dto.ChannelResponseDto
import org.volkszaehler.volkszaehlerapp.data.remote.dto.DataResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VolkszaehlerApi {

    /**
     * Get all public channels
     */
    @GET("channel.json")
    suspend fun getChannels(): Response<ChannelResponseDto>

    /**
     * Get specific channel by UUID
     */
    @GET("channel/{uuid}.json")
    suspend fun getChannel(
        @Path("uuid") uuid: String
    ): Response<ChannelResponseDto>

    /**
     * Get channel data with time range
     * @param uuid Channel UUID
     * @param from Start timestamp (milliseconds)
     * @param to End timestamp (milliseconds)
     * @param tuples Number of data points (default: 1000)
     * @param group Grouping mode (e.g., "day", "hour")
     */
    @GET("data/{uuid}.json")
    suspend fun getChannelData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null,
        @Query("tuples") tuples: Int? = null,
        @Query("group") group: String? = null
    ): Response<DataResponseDto>

    /**
     * Get multiple channels data (group)
     */
    @GET("data.json")
    suspend fun getMultipleChannelData(
        @Query("uuid[]") uuids: List<String>,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null,
        @Query("tuples") tuples: Int? = null,
        @Query("group") group: String? = null
    ): Response<DataResponseDto>
}