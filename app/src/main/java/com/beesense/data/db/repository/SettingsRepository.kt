package com.beesense.data.db.repository

import com.beesense.data.db.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettingsStream(): Flow<SettingsEntity?>

    suspend fun insertSettings(settings: SettingsEntity)
}
