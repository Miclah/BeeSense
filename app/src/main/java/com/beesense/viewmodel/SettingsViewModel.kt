package com.beesense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.db.AppContainer
import com.beesense.data.db.entities.SettingsEntity
import com.beesense.data.db.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SettingsRepository = AppContainer(application).settingsRepository

    val settings: StateFlow<SettingsEntity> =
        repository.getSettingsStream()
            .map { it ?: SettingsEntity(
                isDarkMode = false,
                areNotificationsEnabled = false,
                weightThresholdKg = 3f,
                inactivityThresholdHours = 5
            ) }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                SettingsEntity(
                    isDarkMode = false,
                    areNotificationsEnabled = false,
                    weightThresholdKg = 3f,
                    inactivityThresholdHours = 5
                )
            )

    fun save(settings: SettingsEntity) {
        viewModelScope.launch {
            repository.insertSettings(settings)
        }
    }
}
