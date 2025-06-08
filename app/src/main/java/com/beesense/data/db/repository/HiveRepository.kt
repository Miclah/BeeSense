package com.beesense.data.db.repository

import com.beesense.data.db.entities.HiveEntity
import kotlinx.coroutines.flow.Flow

/**
 * Rozhranie definujuce operacie s ulmi.
 *
 * Toto rozhranie abstrahuje operacie nad ulmi a umoznuje
 * pouzivat rozne implementacie - lokalnu perzistenciu
 * v Room databaze, sietove volania, alebo ich kombinaciu.
 */
interface HiveRepository {
    /**
     * Ziska vsetky uly ako reaktivny stream.
     *
     * @return Flow so zoznamom vsetkych ulov
     */
    fun getAllHivesStream(): Flow<List<HiveEntity>>

    /**
     * Ziska konkretny ul podla jeho ID ako reaktivny stream.
     *
     * @param hiveId Identifikator hladaneho ula
     * @return Flow s ulom alebo null ak sa nenasiel
     */
    fun getHiveStream(hiveId: Int): Flow<HiveEntity?>

    /**
     * Prida novy ul do databazy.
     *
     * @param hive Ul na pridanie
     * @return ID pridaneho ula
     */
    suspend fun insertHive(hive: HiveEntity): Long

    /**
     * Aktualizuje existujuci ul.
     *
     * @param hive Ul s aktualizovanymi informaciami
     */
    suspend fun updateHive(hive: HiveEntity)

    /**
     * Odstrani ul z databazy.
     *
     * @param hive Ul na odstranenie
     */
    suspend fun deleteHive(hive: HiveEntity)
}
