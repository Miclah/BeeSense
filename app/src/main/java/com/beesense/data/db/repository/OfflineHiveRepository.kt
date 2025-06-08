package com.beesense.data.db.repository

import com.beesense.data.db.dao.HiveDao
import com.beesense.data.db.entities.HiveEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementacia HiveRepository pracujuca s lokalnou Room databazou.
 *
 * Tato implementacia zabezpecuje vsetky operacie s ulmi prostrednictvom
 * lokalneho DAO (Data Access Object) a Room databazy. Umoznuje ulozit,
 * aktualizovat a nacitat data ulov aj bez pripojenia k internetu.
 *
 * @param hiveDao DAO objekt pre pristup k ulom v Room databaze
 */
class OfflineHiveRepository(private val hiveDao: HiveDao) : HiveRepository {
    /**
     * Ziska vsetky uly ako reaktivny Flow.
     * Deleguje operaciu na DAO.
     *
     * @return Flow so zoznamom vsetkych ulov
     */
    override fun getAllHivesStream(): Flow<List<HiveEntity>> =
        hiveDao.getAllHives()

    /**
     * Ziska konkretny ul podla ID ako reaktivny Flow.
     * Deleguje operaciu na DAO.
     *
     * @param hiveId Identifikator hladaneho ula
     * @return Flow s ulom alebo null ak sa nenasiel
     */
    override fun getHiveStream(hiveId: Int): Flow<HiveEntity?> =
        hiveDao.getHive(hiveId)

    /**
     * Prida novy ul do lokalnej databazy.
     * Deleguje operaciu na DAO.
     *
     * @param hive Ul na pridanie
     * @return ID pridaneho ula
     */
    override suspend fun insertHive(hive: HiveEntity): Long =
        hiveDao.insertHive(hive)

    /**
     * Aktualizuje existujuci ul v lokalnej databaze.
     * Deleguje operaciu na DAO.
     *
     * @param hive Ul s aktualizovanymi informaciami
     */
    override suspend fun updateHive(hive: HiveEntity) =
        hiveDao.updateHive(hive)

    /**
     * Odstrani ul z lokalnej databazy.
     * Deleguje operaciu na DAO.
     *
     * @param hive Ul na odstranenie
     */
    override suspend fun deleteHive(hive: HiveEntity) =
        hiveDao.deleteHive(hive)
}
