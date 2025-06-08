package com.beesense.data.api

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Pomoc s AI

/**
 * Sluzi na komunikaciu s BeeSense API serverom.
 *
 * Tato trieda zabezpecuje vsetky sietove volania smerom k backendu,
 * nacitava API kluc, vytvara HTTP poziadavky a spracovava odpovede.
 * Pouziva OkHttp klienta pre volania a Gson na deserializaciu JSON.
 *
 * @property context Kontext aplikacie potrebny na pristup k lokalnym suborom
 */
class ApiService(private val context: Context) {
    // Tag pre logovanie - pomaha pri filtrovaní logov
    private val TAG = "ApiService"

    // Vytvorenie HTTP klienta s nastavenymi casovymi limitmi - ochrana proti zamrznutiu aplikacie
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Maximalne 30 sekund na vytvorenie spojenia
        .readTimeout(30, TimeUnit.SECONDS)     // Maximalne 30 sekund na citanie dat
        .build()                               // Vytvorenie samotnej instancie klienta

    // Vytvorenie deserializera JSON dat s vlastnym typovym adapterom pre HiveDataDto
    private val gson = GsonBuilder()
        .registerTypeAdapter(HiveDataDto::class.java, HiveDataDtoDeserializer()) // Adapter pre specialne spracovanie HiveDataDto
        .create()                                                                // Vytvorenie instancie Gson

    // Základná URL API sluzby
    private val baseUrl = "https://jamika.sk/api/api.php"

    // Nazov suboru, kde je ulozeny API kluc
    private val apiKeyEnvFile = "api_key.env"

    /**
     * Ziskava API kluc zo suboru ulozeného v assetoch.
     *
     * Metoda nacita obsah suboru api_key.env, ktory obsahuje riadok
     * s API klucom vo formate "X-API-Key: 123456abcdef". Ak sa kluc
     * nepodari nacitat (subor neexistuje alebo ma nespravny format),
     * pouzije sa predvoleny kluc.
     *
     * @return API kluc potrebny na autentifikaciu voci serveru
     */
    private suspend fun getApiKey(): String {
        // Spusta kod na IO vlakne, aby sa neblokoval hlavny thread UI
        return withContext(Dispatchers.IO) {
            try {
                // Otvorenie suboru z assetov
                val inputStream = context.assets.open(apiKeyEnvFile)

                // Vytvorenie citaca pre efektívne citanie textu
                val reader = BufferedReader(InputStreamReader(inputStream))

                // Nacitanie celeho obsahu suboru do retazca
                val content = reader.readText()

                // Zatvorenie citaca, aby nedochadzalo k uniku pamate
                reader.close()

                // Extrahovanie API kluca z obsahu suboru
                val apiKeyLine = content.lines().firstOrNull { it.contains("X-API-Key") } // Najde riadok obsahujuci "X-API-Key"
                apiKeyLine?.split(":")?.lastOrNull()?.trim() ?: DEFAULT_API_KEY           // Rozdeli riadok podla ":" a vrati cast za tym znakom, alebo DEFAULT_API_KEY
            } catch (e: Exception) {
                // Logovanie chyby pre diagnosticke ucely
                Log.e(TAG, "Failed to read API key from file: ${e.message}")

                // Pouzitie predvoleneho kluca v pripade chyby
                DEFAULT_API_KEY
            }
        }
    }

    /**
     * Ziskava posledne dve merania pre dany ul.
     * Tieto merania sa pouzivaju na zobrazenie aktualneho stavu a trendu.
     *
     * @param tableName Nazov tabulky reprezentujucej ul v databaze (napr. "test", "hive1")
     * @return Zoznam obsahujuci dva posledne zaznamy, alebo prazdny zoznam v pripade chyby
     */
    suspend fun getLastTwoMeasurements(tableName: String = "test"): List<HiveDataDto> {
        // Spusta kod na IO vlakne, aby sa neblokoval hlavny thread UI
        return withContext(Dispatchers.IO) {
            try {
                // Ziskanie API kluca pre autorizaciu
                val apiKey = getApiKey()

                // Vytvorenie URL pre API volanie s potrebnymi parametrami
                val request = Request.Builder()
                    .url("$baseUrl?table=$tableName&cmd=last_two")  // URL s parametrami pre ziskanie poslednych 2 merani
                    .addHeader("X-API-Key", apiKey)                 // Pridanie API kluca do hlavicky pre autentifikaciu
                    .build()                                        // Vytvorenie objektu poziadavky

                // Vykonanie HTTP volania a ziskanie odpovede
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()  // Ziskanie tela odpovede ako retazec

                // Spracovanie odpovede len ak bola uspesna a obsahuje data
                if (response.isSuccessful && responseBody != null) {
                    // Logovanie odpovede pre diagnostiku
                    Log.d(TAG, "API Response: $responseBody")

                    // Deserializacia JSON odpovede na ApiResponse objekt
                    val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java)

                    // Vratenie dat z odpovede alebo prazdneho zoznamu, ak su data null
                    apiResponse.data ?: emptyList()
                } else {
                    // Logovanie chyby v pripade neuspesnej odpovede
                    Log.e(TAG, "API request failed: ${response.code}")

                    // Vratenie prazdneho zoznamu v pripade chyby
                    emptyList()
                }
            } catch (e: Exception) {
                // Logovanie chyby v pripade vynimky
                Log.e(TAG, "Error fetching data: ${e.message}", e)

                // Vratenie prazdneho zoznamu v pripade vynimky
                emptyList()
            }
        }
    }

    /**
     * Ziskava posledny zaznam pre vsetky dostupne uly.
     * Tato metoda je pouzivana na zobrazenie prehladovej obrazovky so vsetkymi ulmi.
     *
     * @return Mapa, kde kluc je nazov tabulky (ula) a hodnota je posledny zaznam
     */
    suspend fun getAllTablesLastRow(): Map<String, HiveDataDto> {
        // Spusta kod na IO vlakne, aby sa neblokoval hlavny thread UI
        return withContext(Dispatchers.IO) {
            try {
                // Ziskanie API kluca pre autorizaciu
                val apiKey = getApiKey()

                // Vytvorenie URL pre API volanie s potrebnymi parametrami
                val request = Request.Builder()
                    .url("$baseUrl?cmd=all_tables_last_row")  // URL s parametrom pre ziskanie poslednych zaznamov vsetkych tabuliek
                    .addHeader("X-API-Key", apiKey)          // Pridanie API kluca do hlavicky pre autentifikaciu
                    .build()                                 // Vytvorenie objektu poziadavky

                // Vykonanie HTTP volania a ziskanie odpovede
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()  // Ziskanie tela odpovede ako retazec

                // Spracovanie odpovede len ak bola uspesna a obsahuje data
                if (response.isSuccessful && responseBody != null) {
                    // Logovanie odpovede pre diagnostiku
                    Log.d(TAG, "API Response (all_tables_last_row): $responseBody")

                    // Deserializacia JSON odpovede na JsonObject pre manualne spracovanie
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

                    // Kontrola, ci response obsahuje 'success' a ci je true
                    if (jsonObject.has("success") && jsonObject.get("success").asBoolean) {
                        // Vytvorenie prazdnej mapy pre vysledne data
                        val tableData = mutableMapOf<String, HiveDataDto>()

                        // Ziskanie objektu 'data', ktory obsahuje data vsetkych tabuliek
                        val dataObject = jsonObject.getAsJsonObject("data")

                        // Iteracia cez vsetky kluce (nazvy tabuliek) v datovom objekte
                        dataObject.keySet().forEach { tableName ->
                            // Ziskanie JSON objektu pre konkretnu tabulku
                            val tableEntry = dataObject.getAsJsonObject(tableName)

                            // Manualne vytvorenie HiveDataDto objektu z JSON dat
                            val dto = HiveDataDto(
                                id = tableEntry.get("id").asInt,                  // Konverzia ID na Int
                                timestamp = tableEntry.get("timestamp").asString, // Konverzia timestampu na String
                                data = tableEntry.get("data").asString           // Konverzia dat na String (JSON)
                            )

                            // Pridanie tabulky a jej dat do vyslednej mapy
                            tableData[tableName] = dto
                        }

                        // Vratenie naplnenej mapy s datami
                        tableData
                    } else {
                        // V pripade chyby vrati prazdnu mapu
                        emptyMap()
                    }
                } else {
                    // Logovanie chyby v pripade neuspesnej odpovede
                    Log.e(TAG, "API request failed: ${response.code}")

                    // Vratenie prazdnej mapy v pripade chyby
                    emptyMap()
                }
            } catch (e: Exception) {
                // Logovanie chyby v pripade vynimky
                Log.e(TAG, "Error fetching all tables data: ${e.message}", e)

                // Vratenie prazdnej mapy v pripade vynimky
                emptyMap()
            }
        }
    }

    /**
     * Ziskava data pre poslednych X dni pre dany ul.
     * Tato metoda je pouzivana na zobrazenie grafov a historickych dat.
     *
     * @param days Pocet dni, za ktore sa maju ziskat data
     * @param tableName Nazov tabulky reprezentujucej ul v databaze
     * @return Zoznam zaznamov za zadane obdobie, alebo prazdny zoznam v pripade chyby
     */
    suspend fun getLastXDays(days: Int, tableName: String = "test"): List<HiveDataDto> {
        // Spusta kod na IO vlakne, aby sa neblokoval hlavny thread UI
        return withContext(Dispatchers.IO) {
            try {
                // Ziskanie API kluca pre autorizaciu
                val apiKey = getApiKey()

                // Vytvorenie URL pre API volanie s potrebnymi parametrami
                val request = Request.Builder()
                    .url("$baseUrl?table=$tableName&cmd=last_x_days&days=$days")  // URL s parametrami pre zadany pocet dni a tabulku
                    .addHeader("X-API-Key", apiKey)                              // Pridanie API kluca do hlavicky pre autentifikaciu
                    .build()                                                     // Vytvorenie objektu poziadavky

                // Vykonanie HTTP volania a ziskanie odpovede
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()  // Ziskanie tela odpovede ako retazec

                // Spracovanie odpovede len ak bola uspesna a obsahuje data
                if (response.isSuccessful && responseBody != null) {
                    // Logovanie odpovede pre diagnostiku
                    Log.d(TAG, "API Response: $responseBody")

                    // Deserializacia JSON odpovede na ApiResponse objekt
                    val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java)

                    // Vratenie dat z odpovede alebo prazdneho zoznamu, ak su data null
                    apiResponse.data ?: emptyList()
                } else {
                    // Logovanie chyby v pripade neuspesnej odpovede
                    Log.e(TAG, "API request failed: ${response.code}")

                    // Vratenie prazdneho zoznamu v pripade chyby
                    emptyList()
                }
            } catch (e: Exception) {
                // Logovanie chyby v pripade vynimky
                Log.e(TAG, "Error fetching data: ${e.message}", e)

                // Vratenie prazdneho zoznamu v pripade vynimky
                emptyList()
            }
        }
    }

    companion object {
        // Predvoleny API kluc, ktory sa pouzije ak sa nepodarilo nacitat kluc zo suboru
        private const val DEFAULT_API_KEY = "1e1d59410d20104fc30a83b0be7aa44f"
    }
}
