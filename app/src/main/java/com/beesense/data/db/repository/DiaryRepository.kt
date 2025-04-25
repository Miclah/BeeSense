package com.beesense.data.db.repository

import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getDiaryStream(): Flow<List<DiaryEntryEntity?>>

    suspend fun insertDiaryEntry(diaryEntry: DiaryEntryEntity)

    suspend fun updateDiaryEntry(diaryEntry: DiaryEntryEntity)

    suspend fun deleteDiaryEntry(diaryEntry: DiaryEntryEntity)
}
