// ChannelDao.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/data/local/ChannelDao.kt

package org.volkszaehler.volkszaehlerapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.volkszaehler.volkszaehlerapp.data.model.Channel

/**
 * Data Access Object (DAO) für Channel-Entitäten
 *
 * Definiert alle Datenbankoperationen für Channels
 */
@Dao
interface ChannelDao {

    /**
     * Fügt einen Channel in die Datenbank ein
     * Bei Konflikt wird der existierende Eintrag ersetzt
     *
     * @param channel Der einzufügende Channel
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel)

    /**
     * Fügt mehrere Channels in die Datenbank ein
     * Bei Konflikt werden die existierenden Einträge ersetzt
     *
     * @param channels Liste von Channels
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<Channel>)

    /**
     * Aktualisiert einen existierenden Channel
     *
     * @param channel Der zu aktualisierende Channel
     */
    @Update
    suspend fun update(channel: Channel)

    /**
     * Aktualisiert mehrere Channels
     *
     * @param channels Liste von Channels
     */
    @Update
    suspend fun updateAll(channels: List<Channel>)

    /**
     * Löscht einen Channel aus der Datenbank
     *
     * @param channel Der zu löschende Channel
     */
    @Delete
    suspend fun delete(channel: Channel)

    /**
     * Löscht alle Channels aus der Datenbank
     */
    @Query("DELETE FROM channels")
    suspend fun deleteAll()

    /**
     * Gibt alle Channels als Flow zurück
     * Der Flow emittiert automatisch bei Änderungen
     *
     * @return Flow mit Liste aller Channels
     */
    @Query("SELECT * FROM channels ORDER BY title ASC")
    fun getAllChannelsFlow(): Flow<List<Channel>>

    /**
     * Gibt alle Channels einmalig zurück (nicht reaktiv)
     *
     * @return Liste aller Channels
     */
    @Query("SELECT * FROM channels ORDER BY title ASC")
    suspend fun getAllChannels(): List<Channel>

    /**
     * Alternative Methode für getAllChannels (für Repository-Kompatibilität)
     *
     * @return Liste aller Channels
     */
    @Query("SELECT * FROM channels ORDER BY title ASC")
    suspend fun getAllChannelsOnce(): List<Channel>

    /**
     * Sucht einen Channel anhand seiner UUID
     *
     * @param uuid Die UUID des Channels
     * @return Channel oder null
     */
    @Query("SELECT * FROM channels WHERE uuid = :uuid LIMIT 1")
    suspend fun getChannelByUuid(uuid: String): Channel?

    /**
     * Sucht einen Channel anhand seiner UUID als Flow
     *
     * @param uuid Die UUID des Channels
     * @return Flow mit Channel oder null
     */
    @Query("SELECT * FROM channels WHERE uuid = :uuid LIMIT 1")
    fun getChannelByUuidFlow(uuid: String): Flow<Channel?>

    /**
     * Sucht Channels anhand des Typs
     *
     * @param type Der Typ (z.B. "power", "sensor")
     * @return Liste von Channels mit diesem Typ
     */
    @Query("SELECT * FROM channels WHERE type = :type ORDER BY title ASC")
    suspend fun getChannelsByType(type: String): List<Channel>

    /**
     * Sucht Channels anhand des Typs als Flow
     *
     * @param type Der Typ
     * @return Flow mit Liste von Channels
     */
    @Query("SELECT * FROM channels WHERE type = :type ORDER BY title ASC")
    fun getChannelsByTypeFlow(type: String): Flow<List<Channel>>

    /**
     * Filtert Channels die keine Gruppen sind
     *
     * @return Liste von Channels (ohne Gruppen)
     */
    @Query("SELECT * FROM channels WHERE isGroup = 0 ORDER BY title ASC")
    suspend fun getNonGroupChannels(): List<Channel>

    /**
     * Filtert Channels die keine Gruppen sind als Flow
     *
     * @return Flow mit Liste von Channels (ohne Gruppen)
     */
    @Query("SELECT * FROM channels WHERE isGroup = 0 ORDER BY title ASC")
    fun getNonGroupChannelsFlow(): Flow<List<Channel>>

    /**
     * Gibt nur Gruppen zurück
     *
     * @return Liste von Gruppen-Channels
     */
    @Query("SELECT * FROM channels WHERE isGroup = 1 ORDER BY title ASC")
    suspend fun getGroupChannels(): List<Channel>

    /**
     * Sucht Channels anhand des Titels (LIKE-Suche)
     *
     * @param query Suchbegriff
     * @return Liste von gefundenen Channels
     */
    @Query("SELECT * FROM channels WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    suspend fun searchByTitle(query: String): List<Channel>

    /**
     * Sucht Channels anhand mehrerer Felder (Titel, UUID, Typ, Beschreibung)
     *
     * @param query Suchbegriff
     * @return Liste von gefundenen Channels
     */
    @Query("""
        SELECT * FROM channels 
        WHERE title LIKE '%' || :query || '%' 
        OR uuid LIKE '%' || :query || '%' 
        OR type LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    suspend fun search(query: String): List<Channel>

    /**
     * Sucht Channels als Flow
     *
     * @param query Suchbegriff
     * @return Flow mit Liste von gefundenen Channels
     */
    @Query("""
        SELECT * FROM channels 
        WHERE title LIKE '%' || :query || '%' 
        OR uuid LIKE '%' || :query || '%' 
        OR type LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchFlow(query: String): Flow<List<Channel>>

    /**
     * Gibt ausgewählte Channels zurück (isChecked = true)
     *
     * @return Liste von ausgewählten Channels
     */
    @Query("SELECT * FROM channels WHERE isChecked = 1 ORDER BY title ASC")
    suspend fun getCheckedChannels(): List<Channel>

    /**
     * Gibt ausgewählte Channels als Flow zurück
     *
     * @return Flow mit Liste von ausgewählten Channels
     */
    @Query("SELECT * FROM channels WHERE isChecked = 1 ORDER BY title ASC")
    fun getCheckedChannelsFlow(): Flow<List<Channel>>

    /**
     * Setzt den isChecked-Status für einen Channel
     *
     * @param uuid Die UUID des Channels
     * @param isChecked Der neue Status
     */
    @Query("UPDATE channels SET isChecked = :isChecked WHERE uuid = :uuid")
    suspend fun updateCheckedStatus(uuid: String, isChecked: Boolean)

    /**
     * Aktualisiert den letzten Messwert eines Channels
     *
     * @param uuid Die UUID des Channels
     * @param value Der neue Wert
     * @param timestamp Der Zeitstempel
     */
    @Query("UPDATE channels SET lastValue = :value, lastTimestamp = :timestamp WHERE uuid = :uuid")
    suspend fun updateLastReading(uuid: String, value: Double, timestamp: Long)

    /**
     * Gibt die Anzahl aller Channels zurück
     *
     * @return Anzahl der Channels
     */
    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int

    /**
     * Gibt die Anzahl der Channels nach Typ zurück
     *
     * @param type Der Typ
     * @return Anzahl der Channels mit diesem Typ
     */
    @Query("SELECT COUNT(*) FROM channels WHERE type = :type")
    suspend fun getChannelCountByType(type: String): Int

    /**
     * Prüft ob ein Channel mit der UUID existiert
     *
     * @param uuid Die UUID
     * @return true wenn Channel existiert
     */
    @Query("SELECT EXISTS(SELECT 1 FROM channels WHERE uuid = :uuid)")
    suspend fun channelExists(uuid: String): Boolean

    /**
     * Löscht Channels anhand ihrer UUIDs
     *
     * @param uuids Liste von UUIDs
     */
    @Query("DELETE FROM channels WHERE uuid IN (:uuids)")
    suspend fun deleteByUuids(uuids: List<String>)

    /**
     * Gibt Channels sortiert nach letztem Update zurück
     *
     * @return Liste von Channels (neueste zuerst)
     */
    @Query("SELECT * FROM channels WHERE lastTimestamp IS NOT NULL ORDER BY lastTimestamp DESC")
    suspend fun getChannelsSortedByLastUpdate(): List<Channel>

    /**
     * Gibt Channels sortiert nach letztem Update als Flow zurück
     *
     * @return Flow mit Liste von Channels (neueste zuerst)
     */
    @Query("SELECT * FROM channels WHERE lastTimestamp IS NOT NULL ORDER BY lastTimestamp DESC")
    fun getChannelsSortedByLastUpdateFlow(): Flow<List<Channel>>
}