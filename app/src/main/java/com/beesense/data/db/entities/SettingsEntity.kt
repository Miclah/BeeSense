package com.beesense.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean,
    val areNotificationsEnabled: Boolean,
    val weightThresholdKg: Float,
    val inactivityThresholdHours: Int
)
