package com.beesense.data.db.repository

import com.beesense.data.db.dao.SettingsDao
import com.beesense.data.db.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementacia SettingsRepository pracujuca s lokalnou Room databazou.
 *
 * Tato implementacia zabezpecuje operacie s nastaveniami aplikacie
 * prostrednictvom lokalneho DAO (Data Access Object) a Room databazy.
 * Umoznuje ulozit a nacitat nastavenia aj bez pripojenia k internetu.
 *
 * @param dao DAO objekt pre pristup k nastaveniam v Room databaze
 */
class OfflineSettingsRepository(private val dao: SettingsDao) : SettingsRepository {
    /**
     * Ziska aktualne nastavenia aplikacie ako reaktivny Flow.
     * Deleguje operaciu na DAO.
     *
     * @return Flow s nastaveniami
     */
    override fun getSettingsStream(): Flow<SettingsEntity?> =
        dao.getSettings()

    /**
     * Ulozi alebo aktualizuje nastavenia aplikacie v lokalnej databaze.
     * Deleguje operaciu na DAO.
     *
     * @param settings Objekt s nastaveniami na ulozenie
     */
    override suspend fun insertSettings(settings: SettingsEntity) {
        dao.insertSettings(settings)
    }
}
