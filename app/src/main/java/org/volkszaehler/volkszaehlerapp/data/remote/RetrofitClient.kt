package org.volkszaehler.volkszaehlerapp.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client Singleton
 *
 * Stellt eine konfigurierte Retrofit-Instanz für die Volkszähler API bereit
 */
object RetrofitClient {

    /**
     * Base URL der Volkszähler Middleware API
     *
     * Standardmäßig wird die Demo-Instanz verwendet.
     * Für Production sollte dies über die Einstellungen konfigurierbar sein.
     */
    private const val BASE_URL = "http://demo.volkszaehler.org/middleware.php/"

    /**
     * Timeout-Konfiguration in Sekunden
     */
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Moshi JSON Converter
     *
     * Konfiguriert mit KotlinJsonAdapterFactory für Kotlin Data Classes
     */
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * HTTP Logging Interceptor
     *
     * Loggt alle HTTP-Requests und Responses für Debugging
     * In Production sollte dies deaktiviert oder auf BASIC gesetzt werden
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * OkHttp Client
     *
     * Konfiguriert mit:
     * - Timeouts
     * - Logging Interceptor
     * - Retry on Connection Failure
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Retrofit Instanz
     *
     * Lazy initialisiert mit:
     * - Base URL
     * - Moshi Converter
     * - OkHttp Client
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * API Service Instanz
     *
     * Erstellt die Retrofit-Implementierung des VolkszaehlerApiService
     */
    val apiService: VolkszaehlerApiService by lazy {
        retrofit.create(VolkszaehlerApiService::class.java)
    }

    /**
     * Erstellt eine neue Retrofit-Instanz mit custom Base URL
     *
     * Nützlich für dynamische Server-Konfiguration über Einstellungen
     *
     * @param baseUrl Die Base URL der Volkszähler API
     * @return VolkszaehlerApiService Instanz
     */
    fun createApiService(baseUrl: String): VolkszaehlerApiService {
        val customRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return customRetrofit.create(VolkszaehlerApiService::class.java)
    }

    /**
     * Aktualisiert die Logging-Level
     *
     * @param level Der neue Logging-Level
     */
    fun setLoggingLevel(level: HttpLoggingInterceptor.Level) {
        loggingInterceptor.level = level
    }
}

/**
 * Extension Function für einfachen Zugriff auf den API Service
 */
fun getVolkszaehlerApi(): VolkszaehlerApiService {
    return RetrofitClient.apiService
}