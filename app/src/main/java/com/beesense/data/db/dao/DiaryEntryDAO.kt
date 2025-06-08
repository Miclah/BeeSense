package com.beesense.data.db.dao
import androidx.room.*
import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO rozhranie pre pristup k dennikovym zaznamom v databaze.
 *
 * Toto rozhranie poskytuje metody pre manipulaciu s dennikmi vcielkara,
 * umoznuje ziskavanie, pridavanie, aktualizaciu a mazanie zaznamov.
 */
@Dao
interface DiaryEntryDao {
    /**
     * Ziska vsetky dennikove zaznamy zoradene od najnovsieho po najstarsi.
     *
     * @return Flow so zoznamom vsetkych dennikov
     */
    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntryEntity>>

    /**
     * Vlozi novy dennikovy zaznam alebo nahradi existujuci s rovnakym ID.
     *
     * @param entry Dennikovy zaznam na vlozenie/nahradenie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntryEntity)

    /**
     * Aktualizuje existujuci dennikovy zaznam.
     *
     * @param entry Dennikovy zaznam s aktualizovanymi udajmi
     */
    @Update
    suspend fun updateDiaryEntry(entry: DiaryEntryEntity)

    /**
     * Odstrani dennikovy zaznam z databazy.
     *
     * @param entry Dennikovy zaznam na odstranenie
     */
    @Delete
    suspend fun deleteDiaryEntry(entry: DiaryEntryEntity)
}