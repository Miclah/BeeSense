package com.beesense.app.data.repository

import android.content.Context
import com.beesense.app.data.database.AppDatabase
import com.beesense.app.data.model.HiveInfo
import kotlinx.coroutines.flow.Flow

class HiveInfoRepository(context: Context) {
    private val hiveInfoDao = AppDatabase.getDatabase(context).hiveInfoDao()

    fun getAllHives(): Flow<List<HiveInfo>> = hiveInfoDao.getAllHives()

    suspend fun getHiveById(id: Int): HiveInfo? = hiveInfoDao.getHiveById(id)

    suspend fun saveHive(id: Int, name: String) {
        val existingHive = hiveInfoDao.getHiveById(id)
        if (existingHive == null) {
            hiveInfoDao.insertHive(HiveInfo(id, name))
        } else if (existingHive.name != name) {
            hiveInfoDao.updateHive(HiveInfo(id, name))
        }
    }
}
