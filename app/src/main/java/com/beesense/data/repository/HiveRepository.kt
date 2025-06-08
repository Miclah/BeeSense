package com.beesense.data.repository

/**
 * Repozitar pre pracu s datami ulov z API a lokalnych zdrojov.
 *
 * Tato trieda zabezpecuje ziskavanie dat o uloch, primarne z API
 * a v pripade zlyhania poskytuje testovacie data ako zalohu.
 * Sluzi ako jediny zdroj pravdy pre data o uloch v aplikacii.
 */

// Import pre pristup ku kontextu aplikacie
import android.content.Context

// Import pre komunikaciu s API
import com.beesense.data.api.ApiService

// Import pre datovy model
import com.beesense.data.model.HiveData

// Importy pre asynchrnonnu pracu a streams
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repozitar pre ziskavanie dat o uloch.
 *
 * Poskytuje metody pre jednorazove a kontinualne nacitavanie
 * dat o uloch z API alebo testovacich zdrojov.
 *
 * @property context Kontext aplikacie potrebny pre API volania
 */
class HiveRepository(private val context: Context) {
    // Inicializacia servisu pre komunikaciu s API
    private val apiService = ApiService(context)

    /**
     * Ziskava aktualne data o uloch.
     *
     * Najskor sa pokusi ziskat data z API, a ak to zlyha, poskytne testovacie data.
     *
     * @return Zoznam objektov s datami o uloch
     */
    suspend fun getHiveData(): List<HiveData> {
        // Najskor skusime ziskat data z API
        val apiData = apiService.getLastTwoMeasurements()
        if (apiData.isNotEmpty()) {
            // Konverzia DTO objektov na domenovy model
            return apiData.map { it.toHiveData() }
        }

        // Zaloha - testovacie data ak API zlyhalo
        delay(1000) // Simulacia omeskania siete
        return getTestData() // Vratime testovacie data
    }

    /**
     * Poskytuje kontinualny stream dat o uloch.
     *
     * Vytvara Flow, ktory periodicky aktualizuje data o uloch
     * v zadanom casovom intervale.
     *
     * @param refreshInterval Interval aktualizacie v milisekundach (predvolene 30 sekund)
     * @return Flow so zoznamom aktualnych dat o uloch
     */
    fun getHiveDataStream(refreshInterval: Long = 30000): Flow<List<HiveData>> = flow {
        while (true) {
            // Ziskanie aktualnych dat
            val hiveData = getHiveData()

            // Emitovanie dat do Flow
            emit(hiveData)

            // Cakanie na dalsi refresh
            delay(refreshInterval) // Obnova kazdych refreshInterval milisekund (predvolene 30 sekund)
        }
    }

    /**
     * Poskytuje testovacie data pre pripad zlyhania API.
     *
     * @return Zoznam testovacich dat o uloch
     */
    private fun getTestData(): List<HiveData> {
        // Testovacie data s rozlicnymi datumami a hodnotami
        return listOf(
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(2, "03-11-2025 22:00:00", 48f, 31.0f, 26.5f, humidity = 0.42f),
            HiveData(3, "03-11-2025 23:15:00", 45f, 29.8f, 25.5f, weightRight = null),
            HiveData(4, "03-11-2025 23:59:59", 50f, 32.0f, 28.0f, 21.0f, 27.0f, 102.0f, 78.5f),
            HiveData(5, "04-11-2025 08:00:00", 52f, 28.0f, 27.0f, 22.0f, 28.0f, 100.5f, null),
            HiveData(6, "04-11-2025 09:30:30", 47.5f, 30.0f, 26.0f, 20.0f, 26.5f, 101.0f, 76.0f),
            HiveData(7, "04-11-2025 10:45:00", 49f, 30.5f, 27.0f, 21.5f, 27.5f, 103.0f, 77.0f),
            HiveData(8, "04-11-2025 12:00:00", 46f, 29.5f, 25.5f, weightLeft = 22.0f, weightRight = 30.0f),
            HiveData(9, "04-11-2025 14:20:00", 47.2f, 30.4f, 26.7f, 20.5f, 26.7f, 101.8f, 75.5f),
            HiveData(10, "04-11-2025 16:45:00", 48.8f, 31.2f, 27.3f, 22.5f, 28.5f, 100.8f, null),
            HiveData(11, "04-11-2025 16:45:00", 48.8f, 31.2f, 27.3f, 22.5f, 28.5f, 100.8f, null)
        )
    }
}
