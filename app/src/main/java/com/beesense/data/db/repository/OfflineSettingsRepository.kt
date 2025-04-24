package com.beesense.data.db.repository

import com.beesense.data.db.dao.SettingsDao
import com.beesense.data.db.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

class OfflineSettingsRepository(private val dao: SettingsDao) : SettingsRepository {
    override fun getSettingsStream(): Flow<SettingsEntity?> =
        dao.getSettings()

    override suspend fun insertSettings(settings: SettingsEntity) {
        dao.insertSettings(settings)
    }
}
