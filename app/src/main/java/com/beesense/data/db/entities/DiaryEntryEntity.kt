package com.beesense.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.beesense.data.model.DiaryEntry

/**
 * Entitna trieda reprezentujuca zaznam v denniku v lokalnej databaze.
 *
 * Tato trieda sluzi na ukladanie zaznamov vcielkarskych cinnosti,
 * ako su kontroly, liecenia, rozsirovania ulov a podobne.
 */
@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    /** Jedinecny identifikator zaznamu, automaticky generovany Room databazou */
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    /** Typ zaznamu (napr. "kontrola", "liecenie", "rozsirenie") */
    val type: String,

    /** Casova znacka vytvorenia zaznamu vo formate "dd-MM-yyyy HH:mm:ss" */
    val timestamp: String,

    /** Poznamka s detailmi o vykonanej aktivite */
    val notes: String
)

/**
 * Konverzia z entitnej triedy DiaryEntryEntity na domenovy model DiaryEntry.
 *
 * @return Domenovy model DiaryEntry s rovnakymi hodnotami
 */
fun DiaryEntryEntity.toDiaryEntry(): DiaryEntry {
    return DiaryEntry(
        id = this.id,
        type = this.type,
        timestamp = this.timestamp,
        note = this.notes
    )
}

/**
 * Konverzia z domenoveho modelu DiaryEntry na entitnu triedu DiaryEntryEntity.
 *
 * @return Entitna trieda DiaryEntryEntity s rovnakymi hodnotami
 */
fun DiaryEntry.toDiaryEntryEntity(): DiaryEntryEntity {
    return DiaryEntryEntity(
        id = this.id,
        type = this.type,
        timestamp = this.timestamp,
        notes = this.note
    )
}