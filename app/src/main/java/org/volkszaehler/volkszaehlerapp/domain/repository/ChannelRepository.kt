package org.volkszaehler.volkszaehlerapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.util.Result

interface ChannelRepository {
    /**
     * Get all channels (public + configured private channels)
     */
    suspend fun getChannels(): Flow<Result<List<Channel>>>

    /**
     * Get a single channel by UUID (channel info only, no data)
     */
    suspend fun getChannel(uuid: String): Flow<Result<Channel>>

    /**
     * Get channel measurements/data
     * ⚠️ Returns ChannelData (not Channel!)
     */
    suspend fun getChannelData(
        uuid: String,
        from: Long? = null,
        to: Long? = null,
        tuples: Int? = null
    ): Flow<Result<ChannelData>>

    /**
     * Fetch all public entities from server
     */
    suspend fun fetchEntities(): Flow<Result<List<Channel>>>

    /**
     * Fetch capability definitions from server
     */
    suspend fun fetchDefinitions(): Flow<Result<String>>

    /**
     * Fetch a single private channel by UUID
     */
    suspend fun fetchPrivateChannel(uuid: String): Flow<Result<Channel>>

    /**
     * Fetch all channels (public + private)
     * This combines public entities and private channels
     */
    suspend fun fetchAllChannels(privateChannelUUIDs: String): Flow<Result<List<Channel>>>
}
