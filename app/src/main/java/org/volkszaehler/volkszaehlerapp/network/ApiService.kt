package org.volkszaehler.volkszaehlerapp.network

import org.volkszaehler.volkszaehlerapp.model.Channel
import retrofit2.http.GET
import retrofit2.http.Path

data class ChannelResponse(
    val channels: List<Channel>
)

interface ApiService {
    @GET("channel.json")
    suspend fun getChannels(): ChannelResponse

    @GET("data/{uuid}.json")
    suspend fun getChannelData(@Path("uuid") uuid: String): Any
}
