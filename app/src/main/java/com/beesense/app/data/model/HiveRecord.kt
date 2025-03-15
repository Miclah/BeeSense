package com.beesense.app.data.model

data class HiveRecord(
    val id: Int,
    val name: String,
    val hiveDataList: List<HiveData>
)
