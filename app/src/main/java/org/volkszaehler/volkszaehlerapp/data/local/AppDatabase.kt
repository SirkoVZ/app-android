// AppDatabase.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/data/local/AppDatabase.kt

package org.volkszaehler.volkszaehlerapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.volkszaehler.volkszaehlerapp.data.model.Channel

@Database(
    entities = [Channel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}