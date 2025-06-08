package com.beesense.data.model

/**
 * Model reprezentujuci data o uloch.
 *
 * Tato trieda obsahuje vsetky udaje ziskane zo senzorov ula, vratane celkovej
 * vahy, teploty, tlaku a vlhkosti. Niektor hodnoty mozu byt aj null, ak dany
 * senzor pre konkretny ul nie je dostupny alebo data neboli namerane.
 */
data class HiveData(
    /** Jedinecny identifikator zaznamu */
    val id: Int,

    /** Casova znacka vo formate "dd-MM-yyyy HH:mm:ss" */
    val timestamp: String,

    /** Celkova vaha ula v kilogramoch */
    val totalWeight: Float,

    /** Teplota namerana senzorom vo vnutri ula v stupnoch Celzia */
    val temperatureSensor: Float,

    /** Vonkajsia teplota v stupnoch Celzia, moze byt null ak senzor nie je dostupny */
    val temperatureOutside: Float? = null,

    /** Vaha laveho senzora v kilogramoch, moze byt null ak senzor nie je dostupny */
    val weightLeft: Float? = null,

    /** Vaha praveho senzora v kilogramoch, moze byt null ak senzor nie je dostupny */
    val weightRight: Float? = null,

    /** Atmosfericky tlak v hPa, moze byt null ak senzor nie je dostupny */
    val pressure: Float? = null,

    /** Relativna vlhkost v percentach, moze byt null ak senzor nie je dostupny */
    val humidity: Float? = null
)
