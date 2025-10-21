package org.volkszaehler.volkszaehlerapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData

/**
 * Repository interface for Volkszähler channel operations
 */
interface ChannelRepository {

    /**
     * Get all available channels
     */
    fun getChannels(): Flow<Result<List<Channel>>>

    /**
     * Get a specific channel by UUID
     */
    fun getChannel(uuid: String): Flow<Result<Channel>>

    /**
     * Get channel data with optional time range and grouping
     *
     * @param uuid Channel UUID
     * @param from Start timestamp (optional)
     * @param to End timestamp (optional)
     * @param group Grouping interval (optional, e.g., "hour", "day")
     */
    fun getChannelData(
        uuid: String,
        from: Long? = null,
        to: Long? = null,
        group: String? = null
    ): Flow<Result<ChannelData>>
}
