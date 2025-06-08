package com.beesense.data.api

import com.beesense.data.model.HiveData
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

// Pomoc s AI
/**
 * Reprezentuje odpoved z API servera.
 *
 * Tato trieda je pouzita pri deserializacii JSON odpovede zo servera.
 * Obsahuje informacie o uspesnosti volania, pripadnu spravu a samotne data.
 *
 * @property success Ci bolo API volanie uspesne
 * @property message Volitelna textova sprava, typicky popis chyby pri neuspechu
 * @property data Zoznam udajov z ulov, pritomny len pri uspesnom volani
 */
data class ApiResponse(
    val success: Boolean,                // True ak API volanie bolo uspesne, false ak nastala chyba
    val message: String? = null,         // Obsahuje chybovu spravu ak success = false, inak null
    val data: List<HiveDataDto>? = null  // Obsahuje zoznam dat z ulov ak success = true, inak null
)

/**
 * Data Transfer Object (DTO) pre udaje z ula ziskane z API.
 *
 * Tato trieda reprezentuje data z API predtym, nez su skonvertovane
 * na domenovy model HiveData. Obsahuje surove udaje a logiku
 * pre ich prevod na model pouzivany v aplikacii.
 *
 * @property id Jedinecny identifikator zaznamu
 * @property timestamp Casova znacka vo formate "yyyy-MM-dd HH:mm:ss"
 * @property data JSON retazec obsahujuci vsetky namerane hodnoty
 */
data class HiveDataDto(
    val id: Int,                         // Jednoznacny identifikator zaznamu v databaze
    val timestamp: String,               // Cas merania vo formate "2025-05-30 16:00:00" (zo servera)
    val data: String                     // JSON retazec obsahujuci vsetky namerane hodnoty
) {
    /**
     * Konvertuje DTO objekt na domenovy model HiveData.
     *
     * Tato metoda extrahuje hodnoty z JSON retazca, prepocitava
     * celkovu vahu a preformatuje casovu znacku.
     *
     * @return Objekt HiveData s udajmi pripravenymi pre pouzitie v aplikacii
     */
    fun toHiveData(): HiveData {
        // Parsovanie JSON retazca na objekt pre jednoduchsi pristup k hodnotam
        val jsonObject = JSONObject(data)

        // Extrahovanie hodnot z JSON - pouzitie optDouble zabezpeci, ze ak hodnota neexistuje, pouzije sa predvolena (0.0)
        val humidity = jsonObject.optDouble("humidity", 0.0).toFloat()            // Hodnota vlhkosti v %
        val pressure = jsonObject.optDouble("pressure", 0.0).toFloat()            // Atmosfericky tlak v hPa
        val weightLeft = jsonObject.optDouble("weight_left", 0.0).toFloat()       // Vaha na lavej strane ula v kg
        val weightRight = jsonObject.optDouble("weight_right", 0.0).toFloat()     // Vaha na pravej strane ula v kg
        val temperatureSensor = jsonObject.optDouble("temperature_sensor", 0.0).toFloat()  // Teplota vo vnutri ula v °C
        val temperatureOutside = jsonObject.optDouble("temperature_outside", 0.0).toFloat()  // Vonkajsia teplota v °C

        // Vypocet celkovej vahy ula suctom lavej a pravej strany
        val totalWeight = weightLeft + weightRight

        // Konverzia formatu casovej znacky z API formatu na format pouzivany v aplikacii
        // Z "YYYY-MM-DD HH:MM:SS" na "DD-MM-YYYY HH:MM:SS"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())  // Definicia vstupneho formatu
        val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())  // Definicia vystupneho formatu
        val formattedDate = try {
            val date = inputFormat.parse(timestamp)  // Parsovanie stringu na Date objekt
            outputFormat.format(date!!)              // Formatovanie Date objektu na string v pozadovanom formate
        } catch (e: Exception) {
            timestamp                                // V pripade chyby (zly format) vrati povodny retazec
        }

        // Vytvorenie a vratenie noveho HiveData objektu s konvertovanymi hodnotami
        return HiveData(
            id = id,                            // ID zostava rovnake
            timestamp = formattedDate,          // Pouzije preformatovanu casovu znacku
            totalWeight = totalWeight,          // Pouzije vypocitanu celkovu vahu
            temperatureSensor = temperatureSensor,
            temperatureOutside = temperatureOutside,
            weightLeft = weightLeft,
            weightRight = weightRight,
            pressure = pressure,
            humidity = humidity
        )
    }
}

/**
 * Custom deserializer pre spracovanie vnoreneho JSON retazca v poli 'data'.
 *
 * Tento deserializer je pouzity v Gson pri spracovani API odpovedi na konverziu
 * JSON dat na objekty typu HiveDataDto.
 */
class HiveDataDtoDeserializer : JsonDeserializer<HiveDataDto> {
    /**
     * Metoda volana Gson kniznicou pre kazdy objekt typu HiveDataDto.
     *
     * @param json Element obsahujuci JSON data
     * @param typeOfT Typ objektu, ktory sa deserializuje (HiveDataDto)
     * @param context Kontext deserializacie pre pouzitie pri vnorenych objektoch
     * @return Vytvoreny objekt HiveDataDto
     */
    override fun deserialize(
        json: JsonElement,                      // JSON element obsahujuci data
        typeOfT: Type,                          // Typ objektu (vzdy HiveDataDto v tomto pripade)
        context: JsonDeserializationContext     // Kontext pre deserializaciu vnorenych objektov
    ): HiveDataDto {
        // Konverzia JSON elementu na objekt pre pristup k jeho vlastnostiam
        val jsonObject = json.asJsonObject

        // Ziskanie jednotlivych hodnot z JSON objektu
        val id = jsonObject.get("id").asInt              // Ziskanie ID ako Int
        val timestamp = jsonObject.get("timestamp").asString  // Ziskanie timestampu ako String
        val data = jsonObject.get("data").asString      // Ziskanie dat ako String (vnoreny JSON)

        // Vytvorenie a vratenie noveho HiveDataDto objektu
        return HiveDataDto(id, timestamp, data)
    }
}
