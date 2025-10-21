package org.volkszaehler.volkszaehlerapp.di

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.volkszaehler.volkszaehlerapp.data.remote.VolkszaehlerApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module fuer Network Dependencies
 *
 * Stellt bereit:
 * - Retrofit Instanz
 * - OkHttpClient
 * - Moshi JSON Converter
 * - API Service
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DEFAULT_BASE_URL = "http://demo.volkszaehler.org/middleware.php/"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideBaseUrl(
        sharedPreferences: SharedPreferences
    ): String {
        return sharedPreferences.getString(
            "server_url",
            DEFAULT_BASE_URL
        ) ?: DEFAULT_BASE_URL
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideVolkszaehlerApiService(
        retrofit: Retrofit
    ): VolkszaehlerApiService {
        return retrofit.create(VolkszaehlerApiService::class.java)
    }
}