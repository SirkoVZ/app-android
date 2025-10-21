package org.volkszaehler.volkszaehlerapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.volkszaehler.volkszaehlerapp.data.remote.VolkszaehlerApi
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannel
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannels
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannelData
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import javax.inject.Inject

/**
 * Implementation of ChannelRepository
 * Handles data operations for Volkszähler channels
 */
class ChannelRepositoryImpl @Inject constructor(
    private val api: VolkszaehlerApi
) : ChannelRepository {

    override fun getChannels(): Flow<Result<List<Channel>>> = flow {
        try {
            val response = api.getChannels()
            val channels = response.toChannels()
            emit(Result.success(channels))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getChannel(uuid: String): Flow<Result<Channel>> = flow {
        try {
            val response = api.getChannel(uuid)
            val channel = response.toChannels().firstOrNull()
            if (channel != null) {
                emit(Result.success(channel))
            } else {
                emit(Result.failure(Exception("Channel not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getChannelData(
        uuid: String,
        from: Long?,
        to: Long?,
        group: String?
    ): Flow<Result<ChannelData>> = flow {
        try {
            val response = api.getChannelData(
                uuid = uuid,
                from = from,
                to = to,
                group = group
            )
            val channelData = response.toChannelData()
            if (channelData != null) {
                emit(Result.success(channelData))
            } else {
                emit(Result.failure(Exception("No data available")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
