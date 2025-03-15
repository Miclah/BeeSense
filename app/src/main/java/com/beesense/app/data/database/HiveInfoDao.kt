package com.beesense.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.beesense.app.data.model.HiveInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface HiveInfoDao {
    @Query("SELECT * FROM hive_info")
    fun getAllHives(): Flow<List<HiveInfo>>

    @Query("SELECT * FROM hive_info WHERE id = :hiveId")
    suspend fun getHiveById(hiveId: Int): HiveInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHive(hive: HiveInfo)

    @Update
    suspend fun updateHive(hive: HiveInfo)
}
