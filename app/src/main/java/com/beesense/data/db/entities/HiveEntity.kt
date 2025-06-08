package com.beesense.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beesense.data.repository.HiveConfig

/**
 * Entitna trieda reprezentujuca ul v lokalnej databaze.
 *
 * Tato trieda obsahuje informacie o ule, ako jeho zobrazovane meno,
 * nazov tabulky v API a podporovane senzory. Pouziva sa v Room databaze.
 */
@Entity(tableName = "hives")
data class HiveEntity(
    /** Jedinecny identifikator ula */
    @PrimaryKey val id: Int,

    /** Uzivatelske meno ula zobrazovane v aplikacii */
    val displayName: String,

    /** Nazov tabulky v API databaze, odkial sa nacitavaju data pre tento ul */
    val tableName: String,

    /** Ci ul disponuje senzorom na meranie vnutornej teploty */
    val hasTemperatureSensor: Boolean,

    /** Ci ul disponuje senzorom na meranie vonkajsej teploty */
    val hasTemperatureOutside: Boolean,

    /** Ci ul disponuje senzorom na meranie vahy na lavej strane */
    val hasWeightLeft: Boolean,

    /** Ci ul disponuje senzorom na meranie vahy na pravej strane */
    val hasWeightRight: Boolean,

    /** Ci ul disponuje senzorom na meranie atmosferickeho tlaku */
    val hasPressure: Boolean,

    /** Ci ul disponuje senzorom na meranie vlhkosti */
    val hasHumidity: Boolean
)

/**
 * Konverzia z entitnej triedy HiveEntity na domenovy model HiveConfig.
 *
 * @return Domenovy model HiveConfig s rovnakymi hodnotami
 */
fun HiveEntity.toHiveConfig(): HiveConfig {
    return HiveConfig(
        id = this.id,
        displayName = this.displayName,
        tableName = this.tableName,
        hasTemperatureSensor = this.hasTemperatureSensor,
        hasTemperatureOutside = this.hasTemperatureOutside,
        hasWeightLeft = this.hasWeightLeft,
        hasWeightRight = this.hasWeightRight,
        hasPressure = this.hasPressure,
        hasHumidity = this.hasHumidity
    )
}

/**
 * Konverzia z domenoveho modelu HiveConfig na entitnu triedu HiveEntity.
 *
 * @return Entitna trieda HiveEntity s rovnakymi hodnotami
 */
fun HiveConfig.toHiveEntity(): HiveEntity {
    return HiveEntity(
        id = this.id,
        displayName = this.displayName,
        tableName = this.tableName,
        hasTemperatureSensor = this.hasTemperatureSensor,
        hasTemperatureOutside = this.hasTemperatureOutside,
        hasWeightLeft = this.hasWeightLeft,
        hasWeightRight = this.hasWeightRight,
        hasPressure = this.hasPressure,
        hasHumidity = this.hasHumidity
    )
}
