package com.beesense.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beesense.data.db.dao.DiaryEntryDao
import com.beesense.data.db.dao.SettingsDao
import com.beesense.data.db.entities.DiaryEntryEntity
import com.beesense.data.db.entities.SettingsEntity

@Database(entities = [SettingsEntity::class, DiaryEntryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun diaryEntryDao(): DiaryEntryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
