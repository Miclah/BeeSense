package com.beesense.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.beesense.data.db.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO rozhranie pre pristup k nastaveniam aplikacie v databaze.
 *
 * Toto rozhranie pracuje s jedinou instanciou nastaveni, ktora ma fixne ID = 1.
 * Definuje metody pre ziskavanie, vytvaranie a aktualizaciu nastaveni aplikacie.
 */
@Dao
interface SettingsDao {
    /**
     * Ziska aktualne nastavenia aplikacie.
     *
     * @return Flow s objektom nastaveni
     */
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>

    /**
     * Vlozi nove nastavenia do databazy alebo nahradi existujuce.
     * Vzdy sa pouziva fixne ID = 1.
     *
     * @param settings Objekt s nastaveniami na vlozenie/nahradenie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    /**
     * Aktualizuje existujuce nastavenia v databaze.
     *
     * @param settings Objekt s aktualizovanymi nastaveniami
     */
    @Update
    suspend fun updateSettings(settings: SettingsEntity)
}
