package com.beesense.data.repository

/**
 * Domenovy model pre konfiguraciu ula.
 *
 * Tato data trieda drzi vsetky informacie o konfigurácii ula, ako su
 * jeho nazvy a dostupne senzory. Pouziva sa na zobrazenie informacii
 * o uloch v UI a pre komunikaciu s API a databazou.
 *
 * @property id Jedinecny identifikator ula
 * @property displayName Uzivatelsky priatelske meno ula zobrazene v aplikacii
 * @property tableName Technicky nazov tabulky v API/databaze pre tento ul
 * @property hasTemperatureSensor Ci ul ma senzor vnutornej teploty
 * @property hasTemperatureOutside Ci ul ma senzor vonkajsej teploty
 * @property hasWeightLeft Ci ul ma vahovej senzor na lavej strane
 * @property hasWeightRight Ci ul ma vahovej senzor na pravej strane
 * @property hasPressure Ci ul ma senzor na meranie atmosferickeho tlaku
 * @property hasHumidity Ci ul ma senzor na meranie vlhkosti
 */
data class HiveConfig(
    val id : Int,                        // Jedinecne ID pre kazdy ul v systeme
    val displayName: String,             // Nazov, ktory sa zobrazi uzivatelovi v aplikacii
    val tableName: String,               // Nazov tabulky v databáze, kde su ulozene udaje z tohto ula
    val hasTemperatureSensor: Boolean,   // True ak ul ma teplomer vo vnutri
    val hasTemperatureOutside: Boolean,  // True ak ul ma teplomer na meranie vonkajsej teploty
    val hasWeightLeft: Boolean,          // True ak ul ma vahovej senzor na lavej strane
    val hasWeightRight: Boolean,         // True ak ul ma vahovej senzor na pravej strane
    val hasPressure: Boolean,            // True ak ul ma barometer na meranie tlaku
    val hasHumidity: Boolean             // True ak ul ma senzor vlhkosti
)
