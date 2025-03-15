package com.beesense.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hive_info")
data class HiveInfo(
    @PrimaryKey val id: Int,
    val name: String
)
