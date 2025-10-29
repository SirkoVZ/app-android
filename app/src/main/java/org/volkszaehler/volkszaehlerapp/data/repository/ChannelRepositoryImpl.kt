package org.volkszaehler.volkszaehlerapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import org.volkszaehler.volkszaehlerapp.data.remote.VolkszaehlerApi
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannel
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannelData
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import org.volkszaehler.volkszaehlerapp.util.Result
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val api: VolkszaehlerApi,
    private val settingsDataStore: SettingsDataStore
) : ChannelRepository {

    /**
     * Get all channels (public + configured private channels)
     * ✅ Uses settings to determine which channels to load
     */
    override suspend fun getChannels(): Flow<Result<List<Channel>>> = flow {
        emit(Result.Loading)
        try {
            // Read settings
            val settings = settingsDataStore.settings.firstOrNull()

            if (settings == null) {
                emit(Result.Error("Settings not available"))
                return@flow
            }

            // Load channels based on settings
            val channels = when {
                settings.privateChannelUUIDs.isNotBlank() -> {
                    // Load private channels
                    loadPrivateChannels(settings.privateChannelUUIDs)
                }
                else -> {
                    // Load public channels
                    loadPublicChannels()
                }
            }

            emit(Result.Success(channels))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Get a single channel by UUID (channel info only)
     */
    override suspend fun getChannel(uuid: String): Flow<Result<Channel>> = flow {
        emit(Result.Loading)
        try {
            val response = api.getChannel(uuid)
            if (response.isSuccessful) {
                // ✅ PrivateChannelResponse has "entity" (SINGULAR!)
                val entityDto = response.body()?.entity
                if (entityDto != null) {
                    emit(Result.Success(entityDto.toChannel()))
                } else {
                    emit(Result.Error("Channel not found"))
                }
            } else {
                emit(Result.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Get channel measurements/data
     * ✅ Returns ChannelData (not Channel!)
     */
    override suspend fun getChannelData(
        uuid: String,
        from: Long?,
        to: Long?,
        tuples: Int?
    ): Flow<Result<ChannelData>> = flow {
        emit(Result.Loading)
        try {
            val response = api.getChannelData(uuid, from, to, tuples)
            if (response.isSuccessful) {
                // ✅ ChannelDataResponseDto has "data" field
                val dataDto = response.body()?.data
                if (dataDto != null) {
                    emit(Result.Success(dataDto.toChannelData()))
                } else {
                    emit(Result.Error("No data available"))
                }
            } else {
                emit(Result.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Fetches all channels (public entities + private channels from UUIDs)
     */
    override suspend fun fetchAllChannels(privateChannelUUIDs: String): Flow<Result<List<Channel>>> = flow {
        emit(Result.Loading)
        try {
            val channels = if (privateChannelUUIDs.isBlank()) {
                loadPublicChannels()
            } else {
                loadPrivateChannels(privateChannelUUIDs)
            }
            emit(Result.Success(channels))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Fetches only public channels
     */
    override suspend fun fetchEntities(): Flow<Result<List<Channel>>> = flow {
        emit(Result.Loading)
        try {
            val channels = loadPublicChannels()
            emit(Result.Success(channels))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Fetches a single private channel by UUID
     */
    override suspend fun fetchPrivateChannel(uuid: String): Flow<Result<Channel>> = flow {
        emit(Result.Loading)
        try {
            val response = api.getChannel(uuid)
            if (response.isSuccessful) {
                // ✅ PrivateChannelResponse has "entity" (SINGULAR!)
                val channel = response.body()?.entity?.toChannel()
                if (channel != null) {
                    emit(Result.Success(channel))
                } else {
                    emit(Result.Error("Channel not found"))
                }
            } else {
                emit(Result.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Fetches API definitions
     */
    override suspend fun fetchDefinitions(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            // TODO: Implementiere wenn API-Endpoint bekannt ist
            emit(Result.Error("Not implemented yet"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    // ========== PRIVATE HELPER FUNCTIONS ==========

    /**
     * Load public channels from /entity.json
     */
    private suspend fun loadPublicChannels(): List<Channel> {
        val response = api.getChannels()
        return if (response.isSuccessful) {
            // ✅ EntitiesResponse has "entities" (PLURAL!)
            response.body()?.entities?.map { it.toChannel() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Load private channels by UUIDs
     */
    private suspend fun loadPrivateChannels(uuids: String): List<Channel> {
        val privateUUIDs = uuids.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return privateUUIDs.mapNotNull { uuid ->
            try {
                val response = api.getChannel(uuid)
                if (response.isSuccessful) {
                    // ✅ PrivateChannelResponse has "entity" (SINGULAR!)
                    response.body()?.entity?.toChannel()
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
