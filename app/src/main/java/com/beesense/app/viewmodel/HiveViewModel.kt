package com.beesense.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.app.data.model.HiveData
import com.beesense.app.data.repository.HiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HiveViewModel : ViewModel() {
    private val repository = HiveRepository()
    private val _hives = MutableStateFlow<List<HiveData>>(emptyList())
    val hives: StateFlow<List<HiveData>> = _hives

    init {
        loadHives()
    }

    private fun loadHives() {
        viewModelScope.launch {
            _hives.value = repository.getHiveData()
        }
    }
}
