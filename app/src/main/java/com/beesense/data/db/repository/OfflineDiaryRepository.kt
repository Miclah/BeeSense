package com.beesense.data.db.repository

import com.beesense.data.db.dao.DiaryEntryDao
import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

class OfflineDiaryRepository(private val dao: DiaryEntryDao) : DiaryRepository {
    override fun getDiaryStream(): Flow<List<DiaryEntryEntity?>> =
        dao.getAllDiaryEntries()


    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.insertDiaryEntry(diaryEntry)
    }

    override suspend fun updateDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.updateDiaryEntry(diaryEntry)
    }

    override suspend fun deleteDiaryEntry(diaryEntry: DiaryEntryEntity) {
        dao.deleteDiaryEntry(diaryEntry)
    }
}
