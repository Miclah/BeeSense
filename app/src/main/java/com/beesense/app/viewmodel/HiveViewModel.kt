package com.beesense.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.app.data.model.HiveData
import com.beesense.app.data.model.HiveRecord
import com.beesense.app.data.repository.HiveInfoRepository
import com.beesense.app.data.repository.HiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HiveViewModel(application: Application) : AndroidViewModel(application) {
    private val hiveRepository = HiveRepository()
    private val hiveInfoRepository = HiveInfoRepository(application)

    private val _hiveData = MutableStateFlow<List<HiveData>>(emptyList())
    private val _hiveNames = hiveInfoRepository.getAllHives() 
    val hivesWithNames: StateFlow<List<HiveRecord>> = MutableStateFlow(emptyList())

    init {
        loadHives()
    }

    private fun loadHives() {
        viewModelScope.launch {
            _hiveData.value = hiveRepository.getHiveData()
        }

        viewModelScope.launch {
            combine(_hiveData, _hiveNames) { data, names ->
                data.groupBy { it.id }.map { (hiveId, hiveDataList) ->
                    val hiveName = names.find { it.id == hiveId }?.name ?: "Úľ #$hiveId"
                    HiveRecord(hiveId, hiveName, hiveDataList)
                }
            }.collect {
                (hivesWithNames as MutableStateFlow).value = it
            }
        }
    }

    fun saveHiveName(id: Int, name: String) {
        viewModelScope.launch {
            hiveInfoRepository.saveHive(id, name)
        }
    }
}
