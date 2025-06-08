package com.beesense.data.db.dao

import androidx.room.*
import com.beesense.data.db.entities.HiveEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO rozhranie pre pristup k ulom v databaze.
 *
 * Toto rozhranie definuje metody pre CRUD operacie (Create, Read, Update, Delete)
 * nad ulmi v lokalnej Room databaze.
 */
@Dao
interface HiveDao {
    /**
     * Ziska vsetky uly z databazy zoradene podla zobrazovaneho mena.
     *
     * @return Flow so zoznamom vsetkych ulov
     */
    @Query("SELECT * FROM hives ORDER BY displayName ASC")
    fun getAllHives(): Flow<List<HiveEntity>>

    /**
     * Ziska ul podla jeho identifikatora.
     *
     * @param hiveId Identifikator ula
     * @return Flow s ulom alebo null, ak ul s danym ID neexistuje
     */
    @Query("SELECT * FROM hives WHERE id = :hiveId")
    fun getHive(hiveId: Int): Flow<HiveEntity?>

    /**
     * Vlozi novy ul do databazy alebo nahradi existujuci, ak ma rovnake ID.
     *
     * @param hive Ul na vlozenie/nahradenie
     * @return ID vlozeneho ula (long hodnota)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHive(hive: HiveEntity): Long

    /**
     * Aktualizuje existujuci ul v databaze.
     *
     * @param hive Ul s aktualizovanymi udajmi
     */
    @Update
    suspend fun updateHive(hive: HiveEntity)

    /**
     * Odstrani ul z databazy.
     *
     * @param hive Ul na odstranenie
     */
    @Delete
    suspend fun deleteHive(hive: HiveEntity)
}
