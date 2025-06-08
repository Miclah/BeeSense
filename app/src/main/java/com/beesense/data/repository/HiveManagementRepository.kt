package com.beesense.data.repository

// Pomoc s AI
/**
 * Repozitar pre spravu konfiguracii ulov.
 *
 * Tato trieda zabezpecuje ulozenie, nacitanie a mazanie konfiguracie ulov
 * v lokalnom ulozisti pomocou SharedPreferences. Konfiguracie sa ukladaju
 * v JSON formate.
 */

// Import pre pristup ku kontextu aplikacie
import android.content.Context

// Import pre lokalnu pamat aplikacie
import android.content.SharedPreferences

// Import pre logovanie
import android.util.Log

// Importy pre pracu s JSON formatom
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Repozitar pre spravu konfiguracii ulov.
 *
 * @property context Kontext aplikacie potrebny pre pristup k SharedPreferences
 */
class HiveManagementRepository(private val context: Context) {
    // Tag pre logovanie
    private val TAG = "HiveManagementRepository"

    // Inicializacia SharedPreferences pre ulozenie konfiguracie ulov
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "hive_management", Context.MODE_PRIVATE
    )

    // Objekt pre konverziu medzi Kotlin objektami a JSON formatom
    private val gson = Gson()

    /**
     * Ulozi konfiguraciu ula do lokalnej pamate.
     *
     * Ak konfiguracua s rovnakym nazvom tabulky uz existuje, je prepisana.
     *
     * @param hive Konfiguracia ula na ulozenie
     * @return True ak sa ulozenie podarilo, false v pripade chyby
     */
    fun saveHive(hive: HiveConfig): Boolean {
        try {
            // Ziskame vsetky aktualne ulozene uly
            val hives = getAllHives().toMutableList()
            // Nahradime existujuci ul alebo pridame novy
            val existingIndex = hives.indexOfFirst { it.tableName == hive.tableName }
            if (existingIndex != -1) {
                // Nahradenie existujuceho zaznamu
                hives[existingIndex] = hive
            } else {
                // Pridanie noveho zaznamu
                hives.add(hive)
            }

            // Konverzia zoznamu do JSON formatu
            val hivesJson = gson.toJson(hives)
            // Ulozenie JSON retazca do SharedPreferences
            sharedPreferences.edit().putString(HIVES_KEY, hivesJson).apply()
            return true
        } catch (e: Exception) {
            // Logovanie chyby pri ulozeni
            Log.e(TAG, "Error saving hive: ${e.message}", e)
            return false
        }
    }

    /**
     * Ziska vsetky ulozene konfigurace ulov.
     *
     * @return Zoznam vsetkych konfiguracii ulov alebo prazdny zoznam v pripade chyby
     */
    fun getAllHives(): List<HiveConfig> {
        // Nacitanie JSON retazca zo SharedPreferences
        val hivesJson = sharedPreferences.getString(HIVES_KEY, "[]")
        // Definicia typu pre deserializaciu
        val type = object : TypeToken<List<HiveConfig>>() {}.type
        return try {
            // Konverzia z JSON na zoznam objektov
            gson.fromJson(hivesJson, type) ?: emptyList()
        } catch (e: Exception) {
            // Logovanie chyby pri nacitani a vratenie prazdneho zoznamu
            Log.e(TAG, "Error getting hives: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Vymaze konfiguraciu ula podla nazvu tabulky.
     *
     * @param tableName Nazov tabulky ula, ktory sa ma vymazat
     * @return True ak bol ul najdeny a vymazany, false inak
     */
    fun deleteHive(tableName: String): Boolean {
        try {
            // Ziskame vsetky aktualne ulozene uly
            val hives = getAllHives().toMutableList()
            // Odstranime ul s danym nazvom tabulky
            val removed = hives.removeIf { it.tableName == tableName }
            if (removed) {
                // Ak bol ul najdeny a odstraneny, ulozime aktualizovany zoznam
                val hivesJson = gson.toJson(hives)
                sharedPreferences.edit().putString(HIVES_KEY, hivesJson).apply()
            }
            return removed
        } catch (e: Exception) {
            // Logovanie chyby pri mazani
            Log.e(TAG, "Error deleting hive: ${e.message}", e)
            return false
        }
    }

    companion object {
        // Kluc pre ulozenie zoznamu ulov v SharedPreferences
        private const val HIVES_KEY = "saved_hives"
    }
}
