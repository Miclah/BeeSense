package com.beesense.data.db.repository

import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Rozhranie definujuce operacie s dennikmi vcelara.
 *
 * Toto rozhranie abstrahuje operacie s dennikmi a umoznuje
 * pouzivat rozne implementacie - lokalnu perzistenciu
 * v Room databaze, sietove volania, alebo ich kombinaciu.
 */
interface DiaryRepository {
    /**
     * Ziska vsetky dennikove zaznamy ako reaktivny stream.
     *
     * @return Flow so zoznamom vsetkych dennikov
     */
    fun getDiaryStream(): Flow<List<DiaryEntryEntity?>>

    /**
     * Prida novy dennikovy zaznam.
     *
     * @param diaryEntry Dennikovy zaznam na pridanie
     */
    suspend fun insertDiaryEntry(diaryEntry: DiaryEntryEntity)

    /**
     * Aktualizuje existujuci dennikovy zaznam.
     *
     * @param diaryEntry Dennikovy zaznam s aktualizovanymi udajmi
     */
    suspend fun updateDiaryEntry(diaryEntry: DiaryEntryEntity)

    /**
     * Odstrani dennikovy zaznam.
     *
     * @param diaryEntry Dennikovy zaznam na odstranenie
     */
    suspend fun deleteDiaryEntry(diaryEntry: DiaryEntryEntity)
}
