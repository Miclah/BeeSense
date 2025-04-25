package com.beesense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.db.AppContainer
import com.beesense.data.db.entities.DiaryEntryEntity
import com.beesense.data.db.entities.toDiaryEntryEntity
import com.beesense.data.model.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val appContainer = AppContainer(application)
    private val diaryEntryRepository = appContainer.diaryRepository

    val allDiaryEntries: Flow<List<DiaryEntryEntity?>> = diaryEntryRepository.getDiaryStream()

    fun addDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch {
            val entity = diaryEntry.toDiaryEntryEntity()
            diaryEntryRepository.insertDiaryEntry(entity)
        }
    }

    fun updateDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch {
            val entity = diaryEntry.toDiaryEntryEntity()
            diaryEntryRepository.updateDiaryEntry(entity)
        }
    }

    fun deleteDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch {
            val entity = diaryEntry.toDiaryEntryEntity()
            diaryEntryRepository.deleteDiaryEntry(entity)
        }
    }
}
