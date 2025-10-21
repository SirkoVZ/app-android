package org.volkszaehler.volkszaehlerapp.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.volkszaehler.volkszaehlerapp.data.local.ChannelDao
import org.volkszaehler.volkszaehlerapp.data.model.Channel
import org.volkszaehler.volkszaehlerapp.data.model.ChannelData
import org.volkszaehler.volkszaehlerapp.data.remote.VolkszaehlerApiService
import org.volkszaehler.volkszaehlerapp.data.remote.dto.toChannels
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository für Volkszähler-Daten
 *
 * Implementiert das Repository Pattern und dient als Single Source of Truth
 * für alle Kanal- und Messdaten. Koordiniert zwischen API und lokaler Datenbank.
 */
@Singleton
class VolkszaehlerRepository @Inject constructor(
    private val apiService: VolkszaehlerApiService,
    private val channelDao: ChannelDao
) {

    /**
     * Lädt alle Kanäle von der API und speichert sie in der lokalen Datenbank
     *
     * @return Result mit Liste von Channels oder Fehler
     */
    suspend fun refreshChannels(): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEntities()

            if (response.isSuccessful) {
                val entityListResponse = response.body()
                if (entityListResponse != null) {
                    // DTOs zu Domain Models konvertieren
                    val channels = entityListResponse.toChannels()

                    // In lokaler Datenbank speichern
                    channelDao.insertAll(channels)

                    Result.success(channels)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gibt alle Kanäle aus der lokalen Datenbank als Flow zurück
     *
     * @return Flow mit Liste von Channels
     */
    fun getChannelsFlow(): Flow<List<Channel>> {
        return channelDao.getAllChannelsFlow()
    }

    /**
     * Gibt alle Kanäle aus der lokalen Datenbank zurück (einmalig)
     *
     * @return Liste von Channels
     */
    suspend fun getChannels(): List<Channel> = withContext(Dispatchers.IO) {
        channelDao.getAllChannels()
    }

    /**
     * Sucht einen Kanal anhand seiner UUID in der lokalen Datenbank
     *
     * @param uuid Die UUID des Kanals
     * @return Channel oder null
     */
    suspend fun getChannelByUuid(uuid: String): Channel? = withContext(Dispatchers.IO) {
        channelDao.getChannelByUuid(uuid)
    }

    /**
     * Lädt Details eines spezifischen Kanals von der API
     *
     * @param uuid Die UUID des Kanals
     * @return Result mit Channel oder Fehler
     */
    suspend fun getChannelDetails(uuid: String): Result<Channel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEntity(uuid)

            if (response.isSuccessful) {
                val entityListResponse = response.body()
                if (entityListResponse != null) {
                    val channels = entityListResponse.toChannels()
                    val channel = channels.firstOrNull()

                    if (channel != null) {
                        // In Datenbank aktualisieren
                        channelDao.insert(channel)
                        Result.success(channel)
                    } else {
                        Result.failure(Exception("Channel not found in response"))
                    }
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lädt Messdaten für einen Kanal von der API
     *
     * @param uuid Die UUID des Kanals
     * @param from Start-Zeitstempel in Millisekunden (optional)
     * @param to End-Zeitstempel in Millisekunden (optional)
     * @param group Gruppierung der Daten (optional)
     * @return Result mit ChannelData oder Fehler
     */
    suspend fun getChannelData(
        uuid: String,
        from: Long? = null,
        to: Long? = null,
        group: String? = null
    ): Result<ChannelData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getChannelData(
                uuid = uuid,
                from = from,
                to = to,
                group = group
            )

            if (response.isSuccessful) {
                val dataResponse = response.body()
                if (dataResponse != null) {
                    // DTO zu Domain Model konvertieren
                    val channelData = dataResponse.data.toChannelData()

                    // Letzten Wert in Channel aktualisieren (falls vorhanden)
                    if (channelData.tuples.isNotEmpty()) {
                        val lastTuple = channelData.tuples.last()
                        channelDao.getChannelByUuid(uuid)?.let { channel ->
                            val updatedChannel = channel.updateLastReading(
                                value = lastTuple.value,
                                timestamp = lastTuple.timestamp
                            )
                            channelDao.update(updatedChannel)
                        }
                    }

                    Result.success(channelData)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lädt Daten für die letzte Stunde
     *
     * @param uuid Die UUID des Kanals
     * @return Flow mit Result<ChannelData>
     */
    fun getLastHourData(uuid: String): Flow<Result<ChannelData>> = flow {
        try {
            val response = apiService.getChannelData(
                uuid = uuid,
                from = System.currentTimeMillis() - (60 * 60 * 1000),
                to = System.currentTimeMillis()
            )

            if (response.isSuccessful) {
                val dataResponse = response.body()
                if (dataResponse != null) {
                    emit(Result.success(dataResponse.data.toChannelData()))
                } else {
                    emit(Result.failure(Exception("Response body is null")))
                }
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Lädt Daten für den letzten Tag
     *
     * @param uuid Die UUID des Kanals
     * @return Flow mit Result<ChannelData>
     */
    fun getLastDayData(uuid: String): Flow<Result<ChannelData>> = flow {
        try {
            val response = apiService.getChannelData(
                uuid = uuid,
                from = System.currentTimeMillis() - (24 * 60 * 60 * 1000),
                to = System.currentTimeMillis()
            )

            if (response.isSuccessful) {
                val dataResponse = response.body()
                if (dataResponse != null) {
                    emit(Result.success(dataResponse.data.toChannelData()))
                } else {
                    emit(Result.failure(Exception("Response body is null")))
                }
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Lädt Daten für die letzte Woche
     *
     * @param uuid Die UUID des Kanals
     * @return Flow mit Result<ChannelData>
     */
    fun getLastWeekData(uuid: String): Flow<Result<ChannelData>> = flow {
        try {
            val response = apiService.getChannelData(
                uuid = uuid,
                from = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000),
                to = System.currentTimeMillis()
            )

            if (response.isSuccessful) {
                val dataResponse = response.body()
                if (dataResponse != null) {
                    emit(Result.success(dataResponse.data.toChannelData()))
                } else {
                    emit(Result.failure(Exception("Response body is null")))
                }
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Aktualisiert einen Kanal in der lokalen Datenbank
     *
     * @param channel Der zu aktualisierende Kanal
     */
    suspend fun updateChannel(channel: Channel) = withContext(Dispatchers.IO) {
        channelDao.update(channel)
    }

    /**
     * Löscht einen Kanal aus der lokalen Datenbank
     *
     * @param channel Der zu löschende Kanal
     */
    suspend fun deleteChannel(channel: Channel) = withContext(Dispatchers.IO) {
        channelDao.delete(channel)
    }

    /**
     * Löscht alle Kanäle aus der lokalen Datenbank
     */
    suspend fun deleteAllChannels() = withContext(Dispatchers.IO) {
        channelDao.deleteAll()
    }

    /**
     * Sucht Kanäle anhand eines Suchbegriffs
     *
     * @param query Der Suchbegriff
     * @return Flow mit gefilterten Channels
     */
    fun searchChannels(query: String): Flow<List<Channel>> = flow {
        val channels = channelDao.getAllChannels()
        val filtered = channels.filter { channel ->
            channel.title.contains(query, ignoreCase = true) ||
                    channel.uuid.contains(query, ignoreCase = true) ||
                    channel.type.contains(query, ignoreCase = true) ||
                    channel.description?.contains(query, ignoreCase = true) == true
        }
        emit(filtered)
    }.flowOn(Dispatchers.IO)
}