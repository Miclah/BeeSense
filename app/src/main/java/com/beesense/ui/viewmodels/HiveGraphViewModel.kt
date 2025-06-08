package com.beesense.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beesense.data.api.ApiService
import com.beesense.data.model.HiveData
import com.beesense.data.db.repository.HiveRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

/**
 * ViewModel pre obrazovku s grafmi udajov z ulov.
 *
 * Tato trieda zabezpecuje nacitanie a spracovanie dat pre grafy,
 * umoznuje vyber ulov, typu dat a casoveho obdobia, a poskytuje
 * tieto data UI komponentom v kompozicnej forme.
 *
 * @property context Kontext potrebny pre pristup k API a databaze
 * @property hiveRepository Repozitar pre ziskanie informacii o uloch
 */
class HiveGraphViewModel(
    context: Context,
    private val hiveRepository: HiveRepository
) : ViewModel() {
    // Instancia API sluzby pre komunikaciu so serverom
    private val apiService = ApiService(context)

    // Tag pre logovanie - urychluje filtrovanie logov
    private val TAG = "HiveGraphViewModel"

    // MutableStateFlow drziaci cely UI stav - pouziva sa pattern jedineho zdroja pravdy
    private val _uiState = MutableStateFlow(UiState())

    // Verejny, len na citanie StateFlow, ktory UI komponenty pozoruju
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Mechanizmus pre debouncing - zabranuje prilis castym volania API pri rychlych zmenach
    private var fetchDataJob: Job? = null

    // Predvoleny ul pouzivany ak nie su dostupne ziadne ine uly
    private val defaultHive = HiveDisplay("no_data", "Žiadne dostupné úle")

    /**
     * Inicializacia ViewModelu - nacita uly z repozitara.
     */
    init {
        // Nacitame ule z HiveRepository pre pripojenie ke sprave ulov
        loadHivesFromRepository()
    }

    /**
     * Nacita uly z repozitara a aktualizuje UI stav.
     * Ak repozitar nema ziadne uly, pokusi sa ich nacitat z API.
     */
    private fun loadHivesFromRepository() {
        // Spustenie korutiny v scope ViewModelu - automaticky sa zrusi pri zruseni ViewModelu
        viewModelScope.launch {
            // Najprv nastavime priznak nacitavania ulov
            _uiState.update { it.copy(isLoadingHives = true) }
            try {
                // Nacitanie zoznamu ulov z repozitara
                val managedHives = hiveRepository.getAllHivesStream().firstOrNull() ?: emptyList()

                // Logovanie poctu nacitanych ulov
                Log.d(TAG, "Loaded ${managedHives.size} hives from repository")

                // Ak sme nasli nejake uly v repozitari
                if (managedHives.isNotEmpty()) {
                    // Konverzia entit z databazy na zobrazitelne objekty
                    val hiveDisplays = managedHives.map { hive ->
                        HiveDisplay(tableName = hive.tableName, displayName = hive.displayName)
                    }

                    // Aktualizacia UI stavu s nacitanymi ulmi
                    _uiState.update { currentState ->
                        currentState.copy(
                            availableHives = hiveDisplays,
                            selectedHive = hiveDisplays.firstOrNull() ?: defaultHive,
                            isLoadingHives = false
                        )
                    }

                    // Nacitanie dat pre vybrany ul
                    loadDataWithDebounce()
                } else {
                    // Ak v repozitari nie su ziadne uly, skusime ich nacitat cez API
                    loadHivesFromAPI()
                }
            } catch (e: Exception) {
                // Logovanie chyby pri nacitavani z repozitara
                Log.e(TAG, "Error loading hives from repository: ${e.message}", e)

                // Zaloha - pokusime sa nacitat uly priamo z API
                loadHivesFromAPI()
            }
        }
    }

    /**
     * Zalozna metoda pre nacitanie ulov priamo z API.
     * Pouziva sa ak repozitar nema ziadne uly alebo pri chybe repozitara.
     */
    private fun loadHivesFromAPI() {
        // Spustenie korutiny v scope ViewModelu
        viewModelScope.launch {
            try {
                // Ziskanie zoznamu dostupnych ulov/tabuliek z API
                val allTablesData = apiService.getAllTablesLastRow()
                val tables = allTablesData.keys.toList()

                // Logovanie nacitanych tabuliek
                Log.d(TAG, "Available tables from API: $tables")

                // Vytvorenie zobrazitelnych objektov pre UI
                val hiveDisplays = mutableListOf<HiveDisplay>()
                tables.forEach { table ->
                    // Pre uly z API pouzijeme rovnaky nazov ako technicky nazov tabulky
                    hiveDisplays.add(HiveDisplay(table, table))
                }

                // Ak sme nenasli ziadne uly, pridame predvoleny
                if (hiveDisplays.isEmpty()) {
                    Log.w(TAG, "No hives found from API, adding default hive")
                    hiveDisplays.add(defaultHive)
                }

                // Aktualizacia UI stavu s nacitanymi ulmi
                _uiState.update { currentState ->
                    currentState.copy(
                        availableHives = hiveDisplays,
                        selectedHive = hiveDisplays.firstOrNull() ?: defaultHive,
                        isLoadingHives = false
                    )
                }

                // Nacitanie dat pre vybrany ul
                loadDataWithDebounce()
            } catch (e: Exception) {
                // Logovanie chyby pri nacitavani z API
                Log.e(TAG, "Error loading hives from API: ${e.message}", e)

                // Aktualizacia UI stavu s chybou
                _uiState.update {
                    it.copy(
                        availableHives = listOf(defaultHive),
                        selectedHive = defaultHive,
                        isLoadingHives = false,
                        error = "Chyba pri načítavaní úľov: ${e.message}"
                    )
                }
                loadDataWithDebounce()
            }
        }
    }

    /**
     * Nacita data pre aktualne vybrany ul a casove obdobie z API.
     * Data su potom agregovane podla potreby a ulozene do UI stavu.
     */
    private fun loadData() {
        // Spustenie korutiny v scope ViewModelu
        viewModelScope.launch {
            // Nastavenie priznaku nacitavania a vymazanie pripadnej chyby
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Ziskanie nazvu tabulky z aktualneho stavu
                val tableName = uiState.value.selectedHive.tableName

                // Ziskanie dat z API podla vybraneho casoveho obdobia
                val data = when(uiState.value.selectedTimePeriod) {
                    TimePeriod.DAY -> {
                        apiService.getLastXDays(1, tableName)  // Data za 1 den
                    }
                    TimePeriod.WEEK -> {
                        apiService.getLastXDays(7, tableName)  // Data za 7 dni
                    }
                    TimePeriod.TWO_WEEKS -> {
                        apiService.getLastXDays(14, tableName)  // Data za 14 dni
                    }
                    TimePeriod.MONTH -> {
                        apiService.getLastXDays(30, tableName)  // Data za 30 dni
                    }
                    TimePeriod.THREE_MONTHS -> {
                        apiService.getLastXDays(90, tableName)  // Data za 90 dni
                    }
                    TimePeriod.SIX_MONTHS -> {
                        apiService.getLastXDays(180, tableName)  // Data za 180 dni
                    }
                    TimePeriod.YEAR -> {
                        apiService.getLastXDays(365, tableName)  // Data za 365 dni
                    }
                    TimePeriod.CUSTOM -> {
                        // Tu by sa volalo API s vlastnym casovym rozsahom
                        // Zatial pouzivame nahradne riesenie - 30 dni
                        apiService.getLastXDays(30, tableName)
                    }
                }

                // Konverzia DTO objektov na domenovy model
                var hiveData = data.map { it.toHiveData() }

                // Agregacia dat podla potreby - zabranuje prilis velkemu mnozstvu bodov v grafe
                hiveData = aggregateDataIfNeeded(hiveData, uiState.value.selectedTimePeriod)

                // Aktualizacia UI stavu s nacitanymi datami
                _uiState.update {
                    it.copy(
                        hiveData = hiveData,
                        isLoading = false,
                        error = null,
                        selectedDataPoint = null  // Reset vybraneho bodu
                    )
                }
            } catch (e: Exception) {
                // Logovanie chyby pri nacitavani dat
                Log.e(TAG, "Error loading data", e)

                // Aktualizacia UI stavu s chybovou spravou
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Chyba pri načítaní údajov: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Agreguje data ak je ich prilis mnoho pre dany casovy rozsah.
     * Tato funkcia kombinuje blizke datove body na zredukovanie ich poctu,
     * co zrychli vykreslovanie grafu a zvysi prehladnost.
     *
     * @param data Povodny zoznam dat
     * @param period Vybrane casove obdobie
     * @return Agregovane data - rovnaky zoznam alebo mensi pocet bodov
     */
    private fun aggregateDataIfNeeded(data: List<HiveData>, period: TimePeriod): List<HiveData> {
        // Pre male mnozstvo dat alebo kratke obdobie nevykonavame agregaciu
        if (data.size <= 100 || period == TimePeriod.DAY) {
            return data
        }

        // Urcenie faktoru agregacie podla vybraneho obdobia - viac dni = vacsia agregacia
        val aggregationFactor = when (period) {
            TimePeriod.WEEK -> 2      // Kombinuje 2 body (kazdu hodinu)
            TimePeriod.TWO_WEEKS -> 4 // Kombinuje 4 body (kazde 2 hodiny)
            TimePeriod.MONTH -> 8     // Kombinuje 8 bodov (kazde 4 hodiny)
            TimePeriod.THREE_MONTHS -> 24 // Kombinuje 24 bodov (kazdy polden)
            TimePeriod.SIX_MONTHS -> 48 // Kombinuje 48 bodov (kazdy den)
            TimePeriod.YEAR -> 96     // Kombinuje 96 bodov (kazde 2 dni)
            else -> 1                 // Ziadna agregacia
        }

        // Ak nie je potrebna agregacia, vratime povodne data
        if (aggregationFactor == 1) {
            return data
        }

        // Vytvorime prazdny zoznam pre agregovane vysledky
        val result = mutableListOf<HiveData>()
        var i = 0

        // Prechadzame vstupne data po skupinach podla agregacneho faktoru
        while (i < data.size) {
            // Vyberieme skupinu bodov pre agregovanie
            val chunk = data.subList(i, min(i + aggregationFactor, data.size))
            if (chunk.isEmpty()) break

            // Pouzijeme prvy casovy udaj z tejto skupiny
            val timestamp = chunk.first().timestamp

            // Inicializacia premennych pre vypocet priemerov
            var totalWeight = 0f
            var temperatureSensor = 0f
            var temperatureOutside = 0f
            var weightLeft = 0f
            var weightRight = 0f
            var pressure = 0f
            var humidity = 0f
            var countTotalWeight = 0
            var countTempSensor = 0
            var countTempOutside = 0
            var countWeightLeft = 0
            var countWeightRight = 0
            var countPressure = 0
            var countHumidity = 0

            // Spracovanie vsetkych bodov v skupine a vypocet sum
            chunk.forEach { item ->
                // Pre nenulove hodnoty pripocitame k sume a zvysime pocitadlo
                totalWeight += item.totalWeight; countTotalWeight++
                temperatureSensor += item.temperatureSensor; countTempSensor++

                // Pre nullable hodnoty kontrolujeme null a az potom pripocitavame
                // Pre kazdu hodnotu kontrolujeme, ci nie je null - ak existuje, pripocitame ju k sume a zvysime pocitadlo
                item.temperatureOutside?.let { temperatureOutside += it; countTempOutside++ }
                item.weightLeft?.let { weightLeft += it; countWeightLeft++ }
                item.weightRight?.let { weightRight += it; countWeightRight++ }
                item.pressure?.let { pressure += it; countPressure++ }
                item.humidity?.let { humidity += it; countHumidity++ }
            }

            // Vytvorime novy agregovany zaznam s priemernymi hodnotami
            result.add(
                HiveData(
                    id = chunk.first().id,  // ID zostavame z prveho zaznamu
                    timestamp = timestamp,   // Pouzijeme casovu znacku z prveho zaznamu
                    // Vypocet priemerov - ak nie su data, vratime 0 alebo null
                    // Pre povinnne hodnoty vraciame 0, pre volitelne hodnoty vraciame null ak nie su data
                    totalWeight = if (countTotalWeight > 0) totalWeight / countTotalWeight else 0f,
                    temperatureSensor = if (countTempSensor > 0) temperatureSensor / countTempSensor else 0f,
                    temperatureOutside = if (countTempOutside > 0) temperatureOutside / countTempOutside else null,
                    weightLeft = if (countWeightLeft > 0) weightLeft / countWeightLeft else null,
                    weightRight = if (countWeightRight > 0) weightRight / countWeightRight else null,
                    pressure = if (countPressure > 0) pressure / countPressure else null,
                    humidity = if (countHumidity > 0) humidity / countHumidity else null
                )
            )

            // Posunieme sa o velkost agregacneho faktoru - preskakujeme zaznamy ktore sme uz spracovali
            i += aggregationFactor
        }

        return result
    }

    /**
     * Nacita data s oneskorenim na zabranenie prilis castym volania API.
     * Zrusi predchadzajuce volanie ak bolo vykonane prilis skoro.
     *
     * @param delayMs Oneskorenie v milisekundach
     */
    private fun loadDataWithDebounce(delayMs: Long = 300) {
        // Zrusenie predchadzajuceho nacitavania ak este neskoncilo
        fetchDataJob?.cancel()

        // Spustenie noveho nacitavania s oneskorenim
        fetchDataJob = viewModelScope.launch {
            delay(delayMs) // Pockame delayMs milisekund
            loadData()     // Nacitame data
        }
    }

    // Verejne metody pre interakcie z UI

    /**
     * Nastavi vybrany ul a obnovi data.
     *
     * @param hive Novo vybrany ul
     */
    fun onHiveSelected(hive: HiveDisplay) {
        _uiState.update { it.copy(selectedHive = hive) }
        loadDataWithDebounce()
    }

    /**
     * Nastavi vybrany typ dat (napr. teplota, vaha).
     *
     * @param dataType Novo vybrany typ dat
     */
    fun onDataTypeSelected(dataType: DataType) {
        _uiState.update { it.copy(selectedDataType = dataType) }
        // Tu nie je potrebne znova nacitavat data, len sa zmeni zobrazenie
    }

    /**
     * Nastavi vybrane casove obdobie a obnovi data.
     *
     * @param timePeriod Nove casove obdobie
     */
    fun onTimePeriodSelected(timePeriod: TimePeriod) {
        _uiState.update {
            val calendar = Calendar.getInstance()
            val now = calendar.time

            // Vypocet zaciatku casoveho obdobia podla vybranej periody
            val startDate = when (timePeriod) {
                TimePeriod.DAY -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)  // 1 den dozadu
                    calendar.time
                }
                TimePeriod.WEEK -> {
                    calendar.add(Calendar.WEEK_OF_YEAR, -1)  // 1 tyzden dozadu
                    calendar.time
                }
                TimePeriod.TWO_WEEKS -> {
                    calendar.add(Calendar.WEEK_OF_YEAR, -2)  // 2 tyzdne dozadu
                    calendar.time
                }
                TimePeriod.MONTH -> {
                    calendar.add(Calendar.MONTH, -1)  // 1 mesiac dozadu
                    calendar.time
                }
                TimePeriod.THREE_MONTHS -> {
                    calendar.add(Calendar.MONTH, -3)  // 3 mesiace dozadu
                    calendar.time
                }
                TimePeriod.SIX_MONTHS -> {
                    calendar.add(Calendar.MONTH, -6)  // 6 mesiacov dozadu
                    calendar.time
                }
                TimePeriod.YEAR -> {
                    calendar.add(Calendar.YEAR, -1)  // 1 rok dozadu
                    calendar.time
                }
                TimePeriod.CUSTOM -> it.customStartDate ?: {
                    calendar.add(Calendar.WEEK_OF_YEAR, -1)  // Predvolene 1 tyzden dozadu
                    calendar.time
                }()
            }

            // Aktualizacia UI stavu s novym casovym obdobim
            it.copy(
                selectedTimePeriod = timePeriod,
                customStartDate = if (timePeriod == TimePeriod.CUSTOM) it.customStartDate else startDate,
                customEndDate = if (timePeriod == TimePeriod.CUSTOM) it.customEndDate ?: now else now
            )
        }

        // Nacitame nove data pre vybrane obdobie
        loadDataWithDebounce()
    }

    /**
     * Nastavi pociatocny datum pre vlastne casove obdobie.
     *
     * @param date Novy pociatocny datum
     */
    fun onStartDateSelected(date: Date) {
        _uiState.update {
            it.copy(
                customStartDate = date,
                // Zabezpecime, ze koncovy datum nie je pred zaciatocnym
                customEndDate = if (it.customEndDate?.before(date) == true) date else it.customEndDate
            )
        }
        if (uiState.value.selectedTimePeriod == TimePeriod.CUSTOM) {
            loadDataWithDebounce()
        }
    }

    /**
     * Nastavi koncovy datum pre vlastne casove obdobie.
     *
     * @param date Novy koncovy datum
     */
    fun onEndDateSelected(date: Date) {
        _uiState.update { it.copy(customEndDate = date) }
        if (uiState.value.selectedTimePeriod == TimePeriod.CUSTOM) {
            loadDataWithDebounce()
        }
    }

    /**
     * Oznaci vybrany datovy bod v grafe.
     *
     * @param timestamp Casova znacka vybraneho bodu
     * @param value Hodnota vybraneho bodu
     */
    fun onValueSelected(timestamp: String, value: Float) {
        _uiState.update {
            it.copy(selectedDataPoint = timestamp to value)
        }
    }

    // Datove triedy a enumy pre stav UI

    /**
     * Reprezentuje ul v UI s technickym a zobrazenym nazvom.
     *
     * @property tableName Technicky nazov tabulky v API/databaze
     * @property displayName Zobrazovane meno pre uzivatela
     */
    data class HiveDisplay(
        val tableName: String,  // Nazov tabulky pouzivany v API
        val displayName: String // Nazov zobrazeny v UI
    ) {
        override fun toString(): String = displayName  // Pre zobrazenie v dropdownoch
    }

    /**
     * Typy dat, ktore mozno zobrazit v grafe.
     * Kazdy typ ma kluc, zobrazovane meno a jednotku.
     */
    enum class DataType(val key: String, val displayName: String, val unit: String) {
        TOTAL_WEIGHT("totalWeight", "Celková váha", "kg"),
        TEMPERATURE_SENSOR("temperatureSensor", "Teplota v úli", "°C"),
        TEMPERATURE_OUTSIDE("temperatureOutside", "Vonkajšia teplota", "°C"),
        WEIGHT_LEFT("weightLeft", "Váha ľavá", "kg"),
        WEIGHT_RIGHT("weightRight", "Váha pravá", "kg"),
        PRESSURE("pressure", "Tlak", "hPa"),
        HUMIDITY("humidity", "Vlhkosť", "%")
    }

    /**
     * Casove obdobia pre graf.
     * Kazde obdobie ma zobrazovane meno a pocet dni.
     */
    enum class TimePeriod(val displayName: String, val days: Int) {
        DAY("Deň", 1),
        WEEK("Týždeň", 7),
        TWO_WEEKS("2 týždne", 14),
        MONTH("Mesiac", 30),
        THREE_MONTHS("3 mesiace", 90),
        SIX_MONTHS("6 mesiacov", 180),
        YEAR("Rok", 365),
        CUSTOM("Vlastné obdobie", -1)  // -1 znamena, ze pocet dni je definovany datumami
    }

    /**
     * Trieda reprezentujuca cely stav UI pre obrazovku s grafmi.
     * Pouziva sa pattern "jediny zdroj pravdy" pre vsetky UI komponenty.
     */
    data class UiState(
        val isLoadingHives: Boolean = false,         // Ci sa prave nacitavaju uly
        val isLoading: Boolean = false,              // Ci sa prave nacitavaju data
        val error: String? = null,                   // Pripadna chybova sprava
        val hiveData: List<HiveData> = emptyList(),  // Nacitane data pre graf
        val availableHives: List<HiveDisplay> = emptyList(),  // Dostupne uly na vyber
        val selectedHive: HiveDisplay = HiveDisplay("no_data", "Načítavam úle..."),  // Aktualne vybrany ul
        val availableDataTypes: List<DataType> = DataType.entries,  // Dostupne typy dat
        val selectedDataType: DataType = DataType.TOTAL_WEIGHT,     // Aktualne vybrany typ dat
        val selectedTimePeriod: TimePeriod = TimePeriod.WEEK,       // Aktualne vybrane casove obdobie
        val customStartDate: Date? = {                              // Pociatocny datum pre vlastne obdobie
            val cal = Calendar.getInstance()
            cal.add(Calendar.WEEK_OF_YEAR, -1)  // Predvolene 1 tyzden dozadu
            cal.time
        }(),
        val customEndDate: Date? = Calendar.getInstance().time,  // Koncovy datum pre vlastne obdobie (teraz)
        val selectedDataPoint: Pair<String, Float>? = null       // Vybrany datovy bod v grafe
    ) {
        /**
         * Pomocna funkcia na ziskanie hodnoty z HiveData pre konkretny typ dat.
         *
         * @param data Objekt s datami ula
         * @param dataType Typ dat, ktoreho hodnotu chceme ziskat
         * @return Hodnota pre dany typ dat, alebo null ak nie je dostupna
         */
        fun getValueForDataType(data: HiveData, dataType: DataType): Float? {
            return when (dataType) {
                DataType.TOTAL_WEIGHT -> data.totalWeight
                DataType.TEMPERATURE_SENSOR -> data.temperatureSensor
                DataType.TEMPERATURE_OUTSIDE -> data.temperatureOutside
                DataType.WEIGHT_LEFT -> data.weightLeft
                DataType.WEIGHT_RIGHT -> data.weightRight
                DataType.PRESSURE -> data.pressure
                DataType.HUMIDITY -> data.humidity
            }
        }
    }

    /**
     * Factory trieda pre vytvaranie instancii HiveGraphViewModel.
     * Pouziva sa na poskytnutie zavislosti ako context a repozitar do ViewModelu.
     */
    class Factory(
        private val context: Context,
        private val hiveRepository: HiveRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HiveGraphViewModel::class.java)) {
                return HiveGraphViewModel(context, hiveRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
