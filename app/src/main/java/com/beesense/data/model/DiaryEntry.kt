package com.beesense.data.model

/**
 * Model reprezentujuci zaznam v denniku vcielok.
 *
 * Tato trieda uchovava informacie o jednotlivych zaznamoch, ktore vcelar robi
 * pocas starania sa o ul, napriklad kontroly, liecenia, rosirenia ula a podobne.
 */
data class DiaryEntry(
    /** Jedinecny identifikator zaznamu v denniku */
    val id: Int,

    /** Typ zaznamu (napr. "kontrola", "liecenie", "rozsirenie") */
    val type: String,

    /** Casova znacka vytvorenia zaznamu vo formate "dd-MM-yyyy HH:mm:ss" */
    val timestamp: String,

    /** Poznamka s detailmi o vykonanej aktivite */
    val note: String
)