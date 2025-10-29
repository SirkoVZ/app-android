package org.volkszaehler.volkszaehlerapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.volkszaehler.volkszaehlerapp.data.local.SettingsDataStore
import org.volkszaehler.volkszaehlerapp.data.remote.VolkszaehlerApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        settingsDataStore: SettingsDataStore
    ): OkHttpClient {
        // Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Basic Auth Interceptor
        val authInterceptor = Interceptor { chain ->
            val settings = runBlocking {
                settingsDataStore.settings.first()
            }

            val request = if (settings.username.isNotEmpty()) {
                val credentials = Credentials.basic(settings.username, settings.password)
                chain.request().newBuilder()
                    .header("Authorization", credentials)
                    .build()
            } else {
                chain.request()
            }

            chain.proceed(request)
        }

        // Dynamic BaseURL Interceptor
        val dynamicBaseUrlInterceptor = Interceptor { chain ->
            val settings = runBlocking {
                settingsDataStore.settings.first()
            }

            val serverUrl = settings.serverUrl.ifEmpty {
                "https://demo.volkszaehler.org/middleware.php"
            }

            val originalRequest = chain.request()
            val originalUrl = originalRequest.url.toString()

            // Extract the path from the original request
            val path = originalUrl.substringAfter("placeholder/")

            // Build new URL with server URL + path
            val newUrlString = serverUrl.removeSuffix("/") + "/" + path

            val newRequest = originalRequest.newBuilder()
                .url(newUrlString)
                .build()

            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(dynamicBaseUrlInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        // Placeholder BaseURL (wird durch Interceptor ersetzt)
        return Retrofit.Builder()
            .baseUrl("https://placeholder/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideVolkszaehlerApi(retrofit: Retrofit): VolkszaehlerApi {
        return retrofit.create(VolkszaehlerApi::class.java)
    }
}
