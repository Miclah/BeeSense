package com.beesense.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beesense.data.db.repository.HiveRepository
import com.beesense.data.repository.HiveConfig
import com.beesense.data.db.entities.toHiveEntity
import com.beesense.data.db.entities.toHiveConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pre spravu ulov v aplikacii
 * Umoznuje ziskavanie, pridavanie, aktualizaciu a mazanie konfiguracie ulov
 * Komunikuje s lokalnou databazou pomocou HiveRepository
 */
class HiveManagementViewModel(
    private val hiveRepository: HiveRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HiveManagementViewModel" // Tag pre logovanie
    }

    /**
     * StateFlow obsahujuci zoznam vsetkych konfiguracii ulov
     * Automaticky sa aktualizuje pri zmenach v databaze
     */
    val hivesState: StateFlow<List<HiveConfig>> = hiveRepository.getAllHivesStream()
        .map { hives -> hives.map { it.toHiveConfig() } } // Konverzia entit na datovy model
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Automaticky zastavi po 5 sekundach neaktivity
            initialValue = emptyList()
        )

    // Indikator stavu nacitavania
    var isLoading by mutableStateOf(false)
        private set

    // Chybova hlaska pri operaciach
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Prida novy ul do databazy
     *
     * @param hiveConfig Konfiguracia ula na pridanie
     * @param onSuccess Callback funkcia, ktora sa vyvola po uspesnom pridani
     */
    fun addHive(hiveConfig: HiveConfig, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // Konverzia z datoveho modelu na entitu pre databazu
                val hiveEntity = hiveConfig.toHiveEntity()
                val newId = hiveRepository.insertHive(hiveEntity)

                Log.d(TAG, "Added new hive with ID: $newId")
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding hive: ${e.message}", e)
                errorMessage = "Chyba pri pridaní úľa: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Aktualizuje existujuci ul v databaze
     *
     * @param hiveConfig Konfiguracia ula na aktualizaciu
     * @param onSuccess Callback funkcia, ktora sa vyvola po uspesnej aktualizacii
     */
    fun updateHive(hiveConfig: HiveConfig, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val hiveEntity = hiveConfig.toHiveEntity()
                hiveRepository.updateHive(hiveEntity)

                Log.d(TAG, "Updated hive with ID: ${hiveConfig.id}")
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating hive: ${e.message}", e)
                errorMessage = "Chyba pri aktualizácii úľa: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Vymaze existujuci ul z databazy
     *
     * @param hiveConfig Konfiguracia ula na vymazanie
     * @param onSuccess Callback funkcia, ktora sa vyvola po uspesnom vymazani
     */
    fun deleteHive(hiveConfig: HiveConfig, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val hiveEntity = hiveConfig.toHiveEntity()
                hiveRepository.deleteHive(hiveEntity)

                Log.d(TAG, "Deleted hive with ID: ${hiveConfig.id}")
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting hive: ${e.message}", e)
                errorMessage = "Chyba pri mazaní úľa: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Factory trieda pre vytvorenie instancie HiveManagementViewModel
     * Pouziva sa na poskytnutie zavislosti (HiveRepository) do ViewModelu
     */
    class Factory(private val hiveRepository: HiveRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HiveManagementViewModel::class.java)) {
                return HiveManagementViewModel(hiveRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
