package com.beesense.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitna trieda reprezentujuca nastavenia aplikacie v lokalnej databaze.
 *
 * Trieda uchovava vsetky pouzivatelske nastavenia aplikacie, napriklad temu,
 * notifikacie a prahove hodnoty pre upozornenia. V databaze existuje vzdy len
 * jeden zaznam nastaveni s fixnym ID = 1.
 */
@Entity(tableName = "settings")
data class SettingsEntity(
    /** Fixny identifikator zaznamu nastaveni, aplikacia vzdy pracuje len s ID=1 */
    @PrimaryKey val id: Int = 1,

    /** Ci je aktivna tmava tema rozhrania */
    val isDarkMode: Boolean,

    /** Ci su aktivovane notifikacie v aplikacii */
    val areNotificationsEnabled: Boolean,

    /** Prahova hodnota zmeny vahy v kg, pri ktorej sa aktivuje notifikacia */
    val weightThresholdKg: Float,

    /** Prahova hodnota neaktivity ulov v hodinach, po ktorej sa aktivuje notifikacia */
    val inactivityThresholdHours: Int,

    /** Casovy interval v hodinach medzi opakovanim notifikacii */
    val notificationIntervalHours: Int = 4  // Predvoleny interval medzi urovnami notifikacii (4 hodiny)
)
