package com.beesense.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.repository.HiveConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * ViewModel pre obrazovku editora ulej, umoznuje vytvorenie alebo upravu konfiguracii ula
 * Spravuje vsetky senzory a nastavenia pre kazdy ul v systeme
 */
class HiveEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val hiveManagementViewModel: HiveManagementViewModel? = null
) : ViewModel() {
    companion object {
        private const val TAG = "HiveEditorViewModel" // Tag pre logovanie
        private const val API_URL = "https://jamika.sk/api/api.php" // URL API pre komunikaciu so serverom
        private const val API_KEY = "1e1d59410d20104fc30a83b0be7aa44f" // API kluc pre autentifikaciu
    }

    // Sleduje ci sme v rezime upravy alebo vytvarania noveho ula
    var isEditMode by mutableStateOf(false)
        private set

    // Jedinecny identifikator ula
    var hiveId by mutableStateOf(savedStateHandle.get<Int>("hiveId") ?: 0)
        private set

    // Zobrazovane meno ula pre uzivatela
    var displayName by mutableStateOf(savedStateHandle.get<String>("displayName") ?: "")
        private set

    // Nazov tabulky v databaze pre dany ul
    var tableName by mutableStateOf(savedStateHandle.get<String>("tableName") ?: "")
        private set

    // Stav roznych senzorov v uli - ci su pritomne alebo nie
    var hasTemperatureSensor by mutableStateOf(savedStateHandle.get<Boolean>("hasTemperatureSensor") ?: false)
        private set

    var hasTemperatureOutside by mutableStateOf(savedStateHandle.get<Boolean>("hasTemperatureOutside") ?: false)
        private set

    var hasWeightLeft by mutableStateOf(savedStateHandle.get<Boolean>("hasWeightLeft") ?: false)
        private set

    var hasWeightRight by mutableStateOf(savedStateHandle.get<Boolean>("hasWeightRight") ?: false)
        private set

    var hasPressure by mutableStateOf(savedStateHandle.get<Boolean>("hasPressure") ?: false)
        private set

    var hasHumidity by mutableStateOf(savedStateHandle.get<Boolean>("hasHumidity") ?: false)
        private set

    // Indikator nacitavania pre zobrazenie progress baru
    var isLoading by mutableStateOf(false)
        private set

    // Chybova hlaska pri spracovani alebo validacii
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // HTTP klient pre komunikaciu s API, nastaveny s timeoutmi pre stabilne pripojenie
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        // Ak je tableName poskytnute v savedStateHandle, sme v rezime upravy
        isEditMode = !tableName.isBlank()

        // Ak sme v rezime upravy ale ostatne parametre nie su nastavene,
        // mozu byt nacitane cez loadExistingHiveData() z management ViewModelu
    }

    /**
     * Nacitanie udajov existujuceho ula z konfiguracie
     * @param hive Konfiguracia ula, ktoru chceme nacitat
     */
    fun loadExistingHiveData(hive: HiveConfig) {
        hiveId = hive.id
        displayName = hive.displayName
        tableName = hive.tableName
        hasTemperatureSensor = hive.hasTemperatureSensor
        hasTemperatureOutside = hive.hasTemperatureOutside
        hasWeightLeft = hive.hasWeightLeft
        hasWeightRight = hive.hasWeightRight
        hasPressure = hive.hasPressure
        hasHumidity = hive.hasHumidity
        isEditMode = true

        // Aktualizacia savedStateHandle pre ulozenie hodnot v pripade ukoncenia procesu
        savedStateHandle["hiveId"] = hiveId
        savedStateHandle["displayName"] = displayName
        savedStateHandle["tableName"] = tableName
        savedStateHandle["hasTemperatureSensor"] = hasTemperatureSensor
        savedStateHandle["hasTemperatureOutside"] = hasTemperatureOutside
        savedStateHandle["hasWeightLeft"] = hasWeightLeft
        savedStateHandle["hasWeightRight"] = hasWeightRight
        savedStateHandle["hasPressure"] = hasPressure
        savedStateHandle["hasHumidity"] = hasHumidity
    }

    /**
     * Aktualizacia zobrazovaneho mena ula
     * @param name Nove meno ula
     */
    fun updateDisplayName(name: String) {
        displayName = name
        savedStateHandle["displayName"] = name
    }

    /**
     * Aktualizacia nazvu tabulky v databaze
     * Poznamka: Tabulku mozno nastavit len v rezime vytvarania, nie pri uprave
     * @param name Novy nazov tabulky
     */
    fun updateTableName(name: String) {
        // Nazov tabulky mozno menit len v rezime vytvarania
        if (!isEditMode) {
            tableName = name
            savedStateHandle["tableName"] = name
        }
    }

    // Skupina funkcii na aktualizaciu stavu senzorov

    /**
     * Nastavenie pritomnosti senzora teploty v uli
     */
    fun updateHasTemperatureSensor(has: Boolean) {
        hasTemperatureSensor = has
        savedStateHandle["hasTemperatureSensor"] = has
    }

    /**
     * Nastavenie pritomnosti senzora vonkajsej teploty
     */
    fun updateHasTemperatureOutside(has: Boolean) {
        hasTemperatureOutside = has
        savedStateHandle["hasTemperatureOutside"] = has
    }

    /**
     * Nastavenie pritomnosti laveho vahoveho senzora
     */
    fun updateHasWeightLeft(has: Boolean) {
        hasWeightLeft = has
        savedStateHandle["hasWeightLeft"] = has
    }

    /**
     * Nastavenie pritomnosti praveho vahoveho senzora
     */
    fun updateHasWeightRight(has: Boolean) {
        hasWeightRight = has
        savedStateHandle["hasWeightRight"] = has
    }

    /**
     * Nastavenie pritomnosti senzora tlaku
     */
    fun updateHasPressure(has: Boolean) {
        hasPressure = has
        savedStateHandle["hasPressure"] = has
    }

    /**
     * Nastavenie pritomnosti senzora vlhkosti
     */
    fun updateHasHumidity(has: Boolean) {
        hasHumidity = has
        savedStateHandle["hasHumidity"] = has
    }

    /**
     * Validacia a ulozenie konfiguracie ula
     * @param onSuccess Callback funkcia, ktora sa vykona po uspesnom ulozeni
     */
    fun validateAndSaveHive(onSuccess: () -> Unit) {
        // Validacia vstupov pred ulozenim
        if (displayName.isBlank()) {
            errorMessage = "Zadajte meno úľa"
            return
        }

        if (tableName.isBlank()) {
            errorMessage = "Zadajte meno tabuľky"
            return
        }

        // Kontrola ci je zvoleny aspon jeden senzor
        if (!hasTemperatureSensor && !hasTemperatureOutside && !hasWeightLeft &&
            !hasWeightRight && !hasPressure && !hasHumidity) {
            errorMessage = "Vyberte aspoň jeden senzor"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val isValid = verifyTableAndSensors()
                if (isValid) {
                    saveHive(onSuccess)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error validating hive: ${e.message}", e)
                errorMessage = "Chyba: ${e.message}"
                isLoading = false
            }
        }
    }

    /**
     * Overenie existencie tabulky a pritomnosti vsetkych vybranych senzorov v danej tabulke
     * @return true ak vsetko existuje, false ak nieco chyba
     */
    private suspend fun verifyTableAndSensors(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // API volanie pre ziskanie vsetkych tabuliek s ich poslednym riadkom
                val request = Request.Builder()
                    .url("$API_URL?cmd=all_tables_last_row")
                    .addHeader("X-API-Key", API_KEY)
                    .build()

                Log.d(TAG, "Making API call: $API_URL?cmd=all_tables_last_row")
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Kontrola ci API vratilo uspesnu odpoved
                if (!response.isSuccessful || responseBody == null) {
                    errorMessage = "API request failed: ${response.code} - ${response.message}"
                    Log.e(TAG, "API request failed with code ${response.code}: $responseBody")
                    return@withContext false
                }

                Log.d(TAG, "API Response: $responseBody")

                // Parsovanie odpovede
                val jsonResponse = JSONObject(responseBody)
                if (!jsonResponse.getBoolean("success")) {
                    errorMessage = "API returned error: ${jsonResponse.optString("message", "Unknown error")}"
                    return@withContext false
                }

                // Ziskanie data objektu so vsetkymi tabulkami
                val dataObj = jsonResponse.optJSONObject("data")
                if (dataObj == null) {
                    errorMessage = "API response didn't contain data"
                    return@withContext false
                }

                // Kontrola existencie tabulky (v oboch rezimoch - vytvaranie aj uprava)
                if (!dataObj.has(tableName)) {
                    val availableTables = dataObj.keys().asSequence().joinToString(", ")
                    Log.d(TAG, "Table validation failed: Table '$tableName' not found. Available tables: $availableTables")
                    errorMessage = "Tabuľka '$tableName' neexistuje v databáze. Dostupné tabuľky: $availableTables"
                    return@withContext false
                }

                // Ziskanie dat tabulky
                val tableData = dataObj.getJSONObject(tableName)
                val dataJsonString = tableData.getString("data")
                val sensorData = JSONObject(dataJsonString)

                Log.d(TAG, "Sensor data for table '$tableName': $dataJsonString")

                // Overenie senzorov - kontrola ci su vsetky vybrane senzory pritomne v datach
                val missingFields = mutableListOf<String>()

                // Kontrola pritomnosti jednotlivych senzorov podla konfiguracie
                if (hasTemperatureSensor && !sensorData.has("temperature_sensor")) {
                    missingFields.add("Teplota senzoru")
                }
                if (hasTemperatureOutside && !sensorData.has("temperature_outside")) {
                    missingFields.add("Vonkajšia teplota")
                }
                if (hasWeightLeft && !sensorData.has("weight_left")) {
                    missingFields.add("Váha (ľavá)")
                }
                if (hasWeightRight && !sensorData.has("weight_right")) {
                    missingFields.add("Váha (pravá)")
                }
                if (hasPressure && !sensorData.has("pressure")) {
                    missingFields.add("Tlak")
                }
                if (hasHumidity && !sensorData.has("humidity")) {
                    missingFields.add("Vlhkosť")
                }

                // Ak chybaju nejake senzory, nastavi sa chybova hlaska
                if (missingFields.isNotEmpty()) {
                    errorMessage = "V tabuľke chýbajú nasledujúce senzory: ${missingFields.joinToString(", ")}"
                    return@withContext false
                }

                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error verifying table and sensors: ${e.message}", e)
                errorMessage = "Chyba pri overovaní: ${e.message}"
                return@withContext false
            }
        }
    }

    /**
     * Ulozenie konfiguracie ula
     * @param onSuccess Callback funkcia, ktora sa vykona po uspesnom ulozeni
     */
    private suspend fun saveHive(onSuccess: () -> Unit) {
        try {
            // Kontrola, ci je k dispozicii HiveManagementViewModel pre ulozenie
            if (hiveManagementViewModel == null) {
                Log.e(TAG, "Cannot save hive: HiveManagementViewModel is null")
                errorMessage = "Interná chyba aplikácie: Nemôžem uložiť úľ"
                return
            }

            // Vytvorenie objektu HiveConfig z aktualneho stavu
            val hiveConfig = HiveConfig(
                id = hiveId,
                displayName = displayName,
                tableName = tableName,
                hasTemperatureSensor = hasTemperatureSensor,
                hasTemperatureOutside = hasTemperatureOutside,
                hasWeightLeft = hasWeightLeft,
                hasWeightRight = hasWeightRight,
                hasPressure = hasPressure,
                hasHumidity = hasHumidity
            )

            Log.d(TAG, "Saving hive: $hiveConfig")

            // Ulozenie ula pomocou management view modelu
            if (isEditMode) {
                // Aktualizacia existujuceho ula
                hiveManagementViewModel.updateHive(hiveConfig) {
                    Log.d(TAG, "Hive updated successfully")
                    onSuccess()
                }
            } else {
                // Pridanie noveho ula
                hiveManagementViewModel.addHive(hiveConfig) {
                    Log.d(TAG, "Hive added successfully")
                    onSuccess()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving hive: ${e.message}", e)
            errorMessage = "Chyba pri ukladaní úľa: ${e.message}"
        } finally {
            isLoading = false
        }
    }
}
