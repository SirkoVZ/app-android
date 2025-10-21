package org.volkszaehler.volkszaehlerapp.data.remote

import org.volkszaehler.volkszaehlerapp.data.remote.dto.DataResponse
import org.volkszaehler.volkszaehlerapp.data.remote.dto.EntityListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service für die Volkszähler Middleware API
 *
 * Base URL Format: http://demo.volkszaehler.org/middleware.php/
 *
 * API Dokumentation: https://wiki.volkszaehler.org/development/api/reference
 */
interface VolkszaehlerApiService {

    /**
     * Lädt alle verfügbaren Entities (Kanäle und Gruppen)
     *
     * Endpoint: GET /entity.json
     *
     * @return EntityListResponse mit allen Entities in hierarchischer Struktur
     */
    @GET("entity.json")
    suspend fun getEntities(): Response<EntityListResponse>

    /**
     * Lädt Details einer spezifischen Entity
     *
     * Endpoint: GET /entity/{uuid}.json
     *
     * @param uuid Die UUID der Entity
     * @return EntityListResponse mit der spezifischen Entity
     */
    @GET("entity/{uuid}.json")
    suspend fun getEntity(
        @Path("uuid") uuid: String
    ): Response<EntityListResponse>

    /**
     * Lädt Messdaten für einen Kanal
     *
     * Endpoint: GET /data/{uuid}.json
     *
     * @param uuid Die UUID des Kanals
     * @param from Start-Zeitstempel in Millisekunden (optional)
     * @param to End-Zeitstempel in Millisekunden (optional)
     * @param group Gruppierung der Daten (hour, day, week, month, year) (optional)
     * @param options Zusätzliche Optionen (optional)
     * @return DataResponse mit den Messdaten
     */
    @GET("data/{uuid}.json")
    suspend fun getChannelData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null,
        @Query("group") group: String? = null,
        @Query("options") options: String? = null
    ): Response<DataResponse>

    /**
     * Lädt aggregierte Daten für mehrere Kanäle
     *
     * Endpoint: GET /group/{uuid}.json
     *
     * @param uuid Die UUID der Gruppe
     * @param from Start-Zeitstempel in Millisekunden (optional)
     * @param to End-Zeitstempel in Millisekunden (optional)
     * @return DataResponse mit aggregierten Daten
     */
    @GET("group/{uuid}.json")
    suspend fun getGroupData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long? = null,
        @Query("to") to: Long? = null
    ): Response<DataResponse>

    /**
     * Lädt Capabilities der API
     *
     * Endpoint: GET /capabilities.json
     *
     * @return Response mit API-Capabilities
     */
    @GET("capabilities.json")
    suspend fun getCapabilities(): Response<Map<String, Any>>
}

/**
 * Hilfsklasse für vordefinierte Zeitbereiche
 */
object TimeRanges {
    private const val HOUR_MILLIS = 60 * 60 * 1000L
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    private const val WEEK_MILLIS = 7 * DAY_MILLIS

    /**
     * Gibt den Zeitstempel für "jetzt" zurück
     */
    fun now(): Long = System.currentTimeMillis()

    /**
     * Gibt den Zeitstempel für "vor X Stunden" zurück
     */
    fun hoursAgo(hours: Int): Long = now() - (hours * HOUR_MILLIS)

    /**
     * Gibt den Zeitstempel für "vor X Tagen" zurück
     */
    fun daysAgo(days: Int): Long = now() - (days * DAY_MILLIS)

    /**
     * Gibt den Zeitstempel für "vor X Wochen" zurück
     */
    fun weeksAgo(weeks: Int): Long = now() - (weeks * WEEK_MILLIS)

    /**
     * Gibt den Zeitstempel für "vor X Monaten" zurück (approximativ)
     */
    fun monthsAgo(months: Int): Long = now() - (months * 30 * DAY_MILLIS)

    /**
     * Gibt den Zeitstempel für "vor X Jahren" zurück (approximativ)
     */
    fun yearsAgo(years: Int): Long = now() - (years * 365 * DAY_MILLIS)
}

/**
 * Enum für Gruppierungsoptionen
 */
enum class DataGrouping(val value: String) {
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    override fun toString(): String = value
}

/**
 * Extension Functions für einfachere API-Calls
 */

/**
 * Lädt Daten für die letzte Stunde
 */
suspend fun VolkszaehlerApiService.getLastHourData(uuid: String): Response<DataResponse> {
    return getChannelData(
        uuid = uuid,
        from = TimeRanges.hoursAgo(1),
        to = TimeRanges.now()
    )
}

/**
 * Lädt Daten für den letzten Tag
 */
suspend fun VolkszaehlerApiService.getLastDayData(uuid: String): Response<DataResponse> {
    return getChannelData(
        uuid = uuid,
        from = TimeRanges.daysAgo(1),
        to = TimeRanges.now()
    )
}

/**
 * Lädt Daten für die letzte Woche
 */
suspend fun VolkszaehlerApiService.getLastWeekData(uuid: String): Response<DataResponse> {
    return getChannelData(
        uuid = uuid,
        from = TimeRanges.weeksAgo(1),
        to = TimeRanges.now()
    )
}

/**
 * Lädt Daten für den letzten Monat
 */
suspend fun VolkszaehlerApiService.getLastMonthData(uuid: String): Response<DataResponse> {
    return getChannelData(
        uuid = uuid,
        from = TimeRanges.monthsAgo(1),
        to = TimeRanges.now()
    )
}

/**
 * Lädt Daten für das letzte Jahr
 */
suspend fun VolkszaehlerApiService.getLastYearData(uuid: String): Response<DataResponse> {
    return getChannelData(
        uuid = uuid,
        from = TimeRanges.yearsAgo(1),
        to = TimeRanges.now()
    )
}