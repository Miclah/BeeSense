package com.beesense.data.db.repository

import com.beesense.data.db.dao.DiaryEntryDao
import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementacia DiaryRepository pracujuca s lokalnou Room databazou.
 *
 * Tato implementacia zabezpecuje vsetky operacie s dennikmi prostrednictvom
 * lokalneho DAO (Data Access Object) a Room databazy. Umoznuje ulozit,
 * aktualizovat a nacitat dennikove zaznamy aj bez pripojenia k internetu.
 *
 * @param dao DAO objekt pre pristup k dennikm v Room databaze
 */
class OfflineDiaryRepository(private val dao: DiaryEntryDao) : DiaryRepository {
    /**
     * Ziska vsetky dennikove zaznamy ako reaktivny Flow.
     * Deleguje operaciu na DAO.
     *
     * @return Flow so zoznamom vsetkych dennikov
     */
    override fun getDiaryStream(): Flow<List<DiaryEntryEntity?>> =
        dao.getAllDiaryEntries()

    /**
     * Prida novy dennikovy zaznam do lokalnej databazy.
     * Deleguje operaciu na DAO.
     *
     * @param diaryEntry Dennikovy zaznam na pridanie
     */
    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.insertDiaryEntry(diaryEntry)
    }

    /**
     * Aktualizuje existujuci dennikovy zaznam v lokalnej databaze.
     * Deleguje operaciu na DAO.
     *
     * @param diaryEntry Dennikovy zaznam s aktualizovanymi udajmi
     */
    override suspend fun updateDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.updateDiaryEntry(diaryEntry)
    }

    /**
     * Odstrani dennikovy zaznam z lokalnej databazy.
     * Deleguje operaciu na DAO.
     *
     * @param diaryEntry Dennikovy zaznam na odstranenie
     */
    override suspend fun deleteDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.deleteDiaryEntry(diaryEntry)
    }
}
