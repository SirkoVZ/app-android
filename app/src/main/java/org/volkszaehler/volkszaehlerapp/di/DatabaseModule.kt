// DatabaseModule.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/di/DatabaseModule.kt

package org.volkszaehler.volkszaehlerapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.volkszaehler.volkszaehlerapp.data.local.AppDatabase
import org.volkszaehler.volkszaehlerapp.data.local.ChannelDao
import javax.inject.Singleton

/**
 * Hilt Module fuer Database Dependencies
 *
 * Stellt bereit:
 * - Room Database Instanz
 * - DAOs (Data Access Objects)
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Stellt die Room Database Instanz bereit
     *
     * @param context Application Context
     * @return AppDatabase Instanz
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "volkszaehler_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Stellt den ChannelDao bereit
     *
     * @param database AppDatabase Instanz
     * @return ChannelDao
     */
    @Provides
    @Singleton
    fun provideChannelDao(
        database: AppDatabase
    ): ChannelDao {
        return database.channelDao()
    }
}