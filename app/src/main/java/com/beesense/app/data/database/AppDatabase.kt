package com.beesense.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beesense.app.data.model.HiveInfo

@Database(entities = [HiveInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hiveInfoDao(): HiveInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bee_sense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
