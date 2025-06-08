package com.beesense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.api.ApiService
import com.beesense.data.db.AppContainer
import com.beesense.data.model.HiveData
import com.beesense.data.model.HiveDataWithTrend
import com.beesense.data.model.TrendAnalyzer
import com.beesense.data.repository.HiveConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.util.Log
import com.beesense.data.db.entities.toHiveConfig

/**
 * ViewModel pre obrazovku prehladu vcelich ulov.
 *
 * Tento ViewModel zodpoveda za nacitavanie, spracovanie a poskytovanie dat
 * pre hlavnu obrazovku aplikacie (OverviewScreen). Sprava konfiguracie ulov
 * nacitane z lokalnej databazy a aktualne data zo senzorov ziskane z API.
 * Udaje su analyzovane na identifikaciu trendov (rast, pokles) a poskytovane
 * UI prostrednictvom StateFlow.
 */
class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "OverviewViewModel"

    // API service pre ziskavanie dat zo serveru
    private val apiService = ApiService(application.applicationContext)

    // AppContainer a repository pre ziskavanie ulov z lokalnej databazy
    private val appContainer = AppContainer(application.applicationContext)
    private val hiveRepository = appContainer.hiveRepository

    // State flows pre komunikaciu s UI
    // List vsetkych ulov s ich aktualnymi hodnotami a trendmi
    private val _hiveDataWithTrends = MutableStateFlow<List<HiveDataWithTrend>>(emptyList())
    val hiveDataWithTrends: StateFlow<List<HiveDataWithTrend>> = _hiveDataWithTrends.asStateFlow()

    // Indikator nacitavania dat
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Chybova hlasenie ak nastane problem pri aktualizacii dat
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Zoznam konfigurací ulov nacitany z Room databazy
    private val _hiveConfigs = MutableStateFlow<List<HiveConfig>>(emptyList())

    /**
     * Inicializacia ViewModelu - automaticky nacita konfiguracne data
     */
    init {
        loadHiveConfigs()
    }

    /**
     * Nacita konfiguracne data o uloch z lokalnej databazy.
     * Nasledne spusti nacitanie aktualnych senzorových dat pre kazdy ul.
     */
    private fun loadHiveConfigs() {
        viewModelScope.launch {
            try {
                // Sledujeme data o uloch z databazy pomocou Flow
                hiveRepository.getAllHivesStream().collect { hiveEntities ->
                    // Konvertujeme entity z databazy na objekty HiveConfig
                    val configs = hiveEntities.map { it.toHiveConfig() }
                    _hiveConfigs.value = configs

                    // Po nacitani konfiguraci nacitame data pre kazdy ul
                    refreshData()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading hive configs: ${e.message}", e)
                _error.value = "Chyba pri nacitani konfiguracii ulov: ${e.message}"
            }
        }
    }

    /**
     * Obnovi data pre vsetky uly z API serveru.
     * Tuto funkciu moze vyvolat uzivatel pomocou tlacidla obnovit.
     */
    fun refreshData() {
        if (_hiveConfigs.value.isEmpty()) {
            // Ak nemame ziadne uly na spracovanie, vratime prazdny zoznam
            _hiveDataWithTrends.value = emptyList()
            return
        }

        viewModelScope.launch {
            // Nastavenie stavu nacitavania a resetovanie chyboveho stavu
            _isLoading.value = true
            _error.value = null

            try {
                val allHiveData = mutableListOf<HiveDataWithTrend>()

                // Pre kazdy ul ziskame posledne dva zaznamy (aktualny a predchadzajuci)
                for (hiveConfig in _hiveConfigs.value) {
                    try {
                        // Ziskanie poslednych dvoch merani z API
                        val lastTwoMeasurements = apiService.getLastTwoMeasurements(hiveConfig.tableName)

                        if (lastTwoMeasurements.isEmpty()) {
                            // Preskocime ul, pre ktory nemame ziadne data
                            continue
                        }

                        // Ziskanie posledneho a predposledneho zaznamu a vytvorenie HiveData
                        val currentDto = lastTwoMeasurements.firstOrNull()
                        val previousDto = if (lastTwoMeasurements.size >= 2) lastTwoMeasurements[1] else null

                        if (currentDto != null) {
                            // Konvertovanie DTO objektov na objekty HiveData
                            val currentHiveData = currentDto.toHiveData().copy(id = hiveConfig.id)
                            val previousHiveData = previousDto?.toHiveData()?.copy(id = hiveConfig.id)

                            // Analyza trendu udajov s pouzitim mena ula
                            val hiveDataWithTrend = TrendAnalyzer.analyzeDataTrend(
                                currentHiveData,
                                previousHiveData,
                                displayName = hiveConfig.displayName
                            )
                            allHiveData.add(hiveDataWithTrend)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading data for hive ${hiveConfig.displayName}: ${e.message}", e)
                        // Pokracujeme s dalsim ulom aj v pripade chyby
                    }
                }

                // Aktualizacia UI s novymi datami
                _hiveDataWithTrends.value = allHiveData

                // Nastavenie chybovej spravy, ak nemame ziadne data
                if (allHiveData.isEmpty() && _hiveConfigs.value.isNotEmpty()) {
                    _error.value = "Ziadne uly nemaju dostupne udaje."
                } else {
                    _error.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing data: ${e.message}", e)
                _error.value = "Chyba pri aktualizacii dat: ${e.message}"
            } finally {
                // Ukoncenie stavu nacitavania bez ohladu na vysledok
                _isLoading.value = false
            }
        }
    }
}
