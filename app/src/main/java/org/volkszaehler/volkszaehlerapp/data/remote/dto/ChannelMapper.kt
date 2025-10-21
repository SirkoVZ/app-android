package org.volkszaehler.volkszaehlerapp.data.remote.dto

import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelType

/**
 * Extension function to convert ChannelDto to Domain Model
 */
fun ChannelDto.toChannel(): Channel {
    return Channel(
        uuid = uuid,
        type = type?.let { ChannelType.fromString(it) } ?: ChannelType.UNKNOWN,
        title = title ?: "Unknown",
        description = description,
        color = color,
        isPublic = isPublic ?: true,
        unit = properties?.unit ?: "W",
        cost = properties?.cost,
        initialValue = properties?.initialValue,
        children = children?.map { it.toChannel() } ?: emptyList()
    )
}

/**
 * Extension function to convert ChannelResponseDto to list of Channels
 */
fun ChannelResponseDto.toChannels(): List<Channel> {
    return entity?.map { it.toChannel() } ?: emptyList()
}
