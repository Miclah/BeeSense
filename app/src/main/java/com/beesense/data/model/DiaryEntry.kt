package com.beesense.data.model

data class DiaryEntry(
    val id: Int,
    val type: String,
    val timestamp: String,
    val note: String
)