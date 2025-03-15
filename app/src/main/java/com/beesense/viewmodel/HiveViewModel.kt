package com.beesense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.model.HiveData
import com.beesense.data.repository.HiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HiveViewModel(application: Application) : AndroidViewModel(application) {
    private val hiveRepository = HiveRepository()

    private val _hiveData = MutableStateFlow<List<HiveData>>(emptyList())
    val hiveData: StateFlow<List<HiveData>> = _hiveData

    init {
        loadHives()
    }

    private fun loadHives() {
        viewModelScope.launch {
            _hiveData.value = hiveRepository.getHiveData()
        }
    }
}
