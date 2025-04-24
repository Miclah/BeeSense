package com.beesense.data.db


import android.content.Context
import com.beesense.data.db.repository.DiaryRepository
import com.beesense.data.db.repository.OfflineDiaryRepository
import com.beesense.data.db.repository.OfflineSettingsRepository
import com.beesense.data.db.repository.SettingsRepository

class AppContainer(context : Context) {
    private val database = AppDatabase.getDatabase(context)

    val settingsRepository: SettingsRepository by lazy {
        OfflineSettingsRepository(database.settingsDao())
    }

    val diaryRepository: DiaryRepository by lazy {
        OfflineDiaryRepository(database.diaryEntryDao())
    }
}
