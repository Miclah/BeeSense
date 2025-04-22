package com.beesense.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beesense.data.db.dao.SettingsDao
import com.beesense.data.db.entities.SettingsEntity

@Database(entities = [SettingsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bee_sense_app_settings_db"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
