package com.beesense.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.beesense.data.model.DiaryEntry

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val timestamp: String,
    val notes: String
)

fun DiaryEntryEntity.toDiaryEntry(): DiaryEntry {
    return DiaryEntry(
        id = this.id,
        type = this.type,
        timestamp = this.timestamp,
        note = this.notes
    )
}

fun DiaryEntry.toDiaryEntryEntity(): DiaryEntryEntity {
    return DiaryEntryEntity(
        id = this.id,
        type = this.type,
        timestamp = this.timestamp,
        notes = this.note
    )
}