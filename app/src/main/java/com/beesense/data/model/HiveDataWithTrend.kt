package com.beesense.data.model

/**
 * Reprezentuje typ trendu hodnoty - stupajuci, klesajuci alebo stabilny.
 *
 * Enum obsahuje sest moznych typov trendov:
 * - UP: mierne stupanie hodnoty
 * - DOWN: mierne klesanie hodnoty
 * - STABLE: hodnota zostava priblizne rovnaka
 * - SIGNIFICANT_UP: vyrazne stupanie hodnoty (prekracuje stanovene prahove hodnoty)
 * - SIGNIFICANT_DOWN: vyrazne klesanie hodnoty (prekracuje stanovene prahove hodnoty)
 * - UNAVAILABLE: trend nie je mozne urcit (napriklad pri nedostatku dat)
 */
enum class TrendType {
    UP, DOWN, STABLE, SIGNIFICANT_UP, SIGNIFICANT_DOWN, UNAVAILABLE
}

/**
 * Rozsireny model HiveData, ktory obsahuje aj informacie o trendoch pre jednotlive hodnoty.
 *
 * Tato trieda kombinuje aktualne (current) data s predchadzajucimi (previous) datami,
 * co umoznuje sledovat zmeny a trendy v nameranych hodnotach. Taktiez obsahuje uzivatelsky
 * definovane meno ula pre lepsiu identifikaciu.
 *
 * @property current Aktualne data z ula
 * @property previous Predchadzajuce data z ula, mozu byt null ak nie su k dispozicii
 * @property displayName Uzivatelsky definovane meno ula
 * @property temperatureTrend Trend vnutornej teploty ula
 * @property outsideTemperatureTrend Trend vonkajsej teploty
 * @property weightTrend Trend celkovej vahy ula
 * @property leftWeightTrend Trend vahy laveho senzora
 * @property rightWeightTrend Trend vahy praveho senzora
 * @property pressureTrend Trend atmosferickeho tlaku
 * @property humidityTrend Trend vlhkosti
 */
data class HiveDataWithTrend(
    val current: HiveData,
    val previous: HiveData?,
    val displayName: String = "",  // Uzivatelsky definovane meno ula
    val temperatureTrend: TrendType = TrendType.UNAVAILABLE,
    val outsideTemperatureTrend: TrendType = TrendType.UNAVAILABLE,
    val weightTrend: TrendType = TrendType.UNAVAILABLE,
    val leftWeightTrend: TrendType = TrendType.UNAVAILABLE,
    val rightWeightTrend: TrendType = TrendType.UNAVAILABLE,
    val pressureTrend: TrendType = TrendType.UNAVAILABLE,
    val humidityTrend: TrendType = TrendType.UNAVAILABLE
)

/**
 * Pomocny objekt na vypocet a analyzu trendov pre rozne typy nameranych udajov.
 *
 * Obsahuje metody na porovnanie aktualnych a predchadzajucich hodnot a urcenie trendu
 * pre jednotlive typy merani. Definuje aj prahove hodnoty pre urcenie, kedy je zmena
 * povazovana za vyznamnu.
 */
object TrendAnalyzer {

    // Vyznamna zmena pre rozne typy hodnot - prahove hodnoty
    private const val SIGNIFICANT_WEIGHT_CHANGE = 2.0f // kg
    private const val SIGNIFICANT_TEMP_CHANGE = 5.0f // °C
    private const val SIGNIFICANT_HUMIDITY_CHANGE = 10.0f // %
    private const val SIGNIFICANT_PRESSURE_CHANGE = 5.0f // hPa

    /**
     * Analyzuje trendy vsetkych hodnot z aktualnch a predchadzajucich dat.
     *
     * @param current Aktualne namerane data z ula
     * @param previous Predchadzajuce namerane data z ula
     * @param displayName Uzivatelsky definovane meno ula
     * @return Objekt HiveDataWithTrend obsahujuci vsetky trendy
     */
    fun analyzeDataTrend(current: HiveData, previous: HiveData?, displayName: String = ""): HiveDataWithTrend {
        if (previous == null) {
            return HiveDataWithTrend(current = current, previous = null, displayName = displayName)
        }

        return HiveDataWithTrend(
            current = current,
            previous = previous,
            displayName = displayName,
            temperatureTrend = analyzeTemperatureTrend(current.temperatureSensor, previous.temperatureSensor),
            outsideTemperatureTrend = analyzeOutsideTemperatureTrend(current.temperatureOutside, previous.temperatureOutside),
            weightTrend = analyzeWeightTrend(current.totalWeight, previous.totalWeight),
            leftWeightTrend = analyzeWeightTrend(current.weightLeft, previous.weightLeft),
            rightWeightTrend = analyzeWeightTrend(current.weightRight, previous.weightRight),
            pressureTrend = analyzePressureTrend(current.pressure, previous.pressure),
            humidityTrend = analyzeHumidityTrend(current.humidity, previous.humidity)
        )
    }

    /**
     * Analyzuje trend vnutornej teploty ula.
     *
     * @param current Aktualna teplota
     * @param previous Predchadzajuca teplota
     * @return Typ trendu teploty
     */
    fun analyzeTemperatureTrend(current: Float, previous: Float): TrendType {
        val diff = current - previous
        return when {
            diff > SIGNIFICANT_TEMP_CHANGE -> TrendType.SIGNIFICANT_UP
            diff < -SIGNIFICANT_TEMP_CHANGE -> TrendType.SIGNIFICANT_DOWN
            diff > 0.5f -> TrendType.UP
            diff < -0.5f -> TrendType.DOWN
            else -> TrendType.STABLE
        }
    }

    /**
     * Analyzuje trend vonkajsej teploty.
     *
     * @param current Aktualna vonkajsia teplota
     * @param previous Predchadzajuca vonkajsia teplota
     * @return Typ trendu vonkajsej teploty, alebo UNAVAILABLE ak data nie su dostupne
     */
    fun analyzeOutsideTemperatureTrend(current: Float?, previous: Float?): TrendType {
        if (current == null || previous == null) return TrendType.UNAVAILABLE
        val diff = current - previous
        return when {
            diff > SIGNIFICANT_TEMP_CHANGE -> TrendType.SIGNIFICANT_UP
            diff < -SIGNIFICANT_TEMP_CHANGE -> TrendType.SIGNIFICANT_DOWN
            diff > 0.5f -> TrendType.UP
            diff < -0.5f -> TrendType.DOWN
            else -> TrendType.STABLE
        }
    }

    /**
     * Analyzuje trend vahy ula alebo jednotliveho senzora.
     *
     * @param current Aktualna vaha
     * @param previous Predchadzajuca vaha
     * @return Typ trendu vahy, alebo UNAVAILABLE ak data nie su dostupne
     */
    fun analyzeWeightTrend(current: Float?, previous: Float?): TrendType {
        if (current == null || previous == null) return TrendType.UNAVAILABLE
        val diff = current - previous
        return when {
            diff > SIGNIFICANT_WEIGHT_CHANGE -> TrendType.SIGNIFICANT_UP
            diff < -SIGNIFICANT_WEIGHT_CHANGE -> TrendType.SIGNIFICANT_DOWN
            diff > 0.2f -> TrendType.UP
            diff < -0.2f -> TrendType.DOWN
            else -> TrendType.STABLE
        }
    }

    /**
     * Analyzuje trend atmosferickeho tlaku.
     *
     * @param current Aktualny tlak
     * @param previous Predchadzajuci tlak
     * @return Typ trendu tlaku, alebo UNAVAILABLE ak data nie su dostupne
     */
    fun analyzePressureTrend(current: Float?, previous: Float?): TrendType {
        if (current == null || previous == null) return TrendType.UNAVAILABLE
        val diff = current - previous
        return when {
            diff > SIGNIFICANT_PRESSURE_CHANGE -> TrendType.SIGNIFICANT_UP
            diff < -SIGNIFICANT_PRESSURE_CHANGE -> TrendType.SIGNIFICANT_DOWN
            diff > 1f -> TrendType.UP
            diff < -1f -> TrendType.DOWN
            else -> TrendType.STABLE
        }
    }

    /**
     * Analyzuje trend vlhkosti.
     *
     * @param current Aktualna vlhkost
     * @param previous Predchadzajuca vlhkost
     * @return Typ trendu vlhkosti, alebo UNAVAILABLE ak data nie su dostupne
     */
    fun analyzeHumidityTrend(current: Float?, previous: Float?): TrendType {
        if (current == null || previous == null) return TrendType.UNAVAILABLE
        val diff = current - previous
        return when {
            diff > SIGNIFICANT_HUMIDITY_CHANGE -> TrendType.SIGNIFICANT_UP
            diff < -SIGNIFICANT_HUMIDITY_CHANGE -> TrendType.SIGNIFICANT_DOWN
            diff > 2f -> TrendType.UP
            diff < -2f -> TrendType.DOWN
            else -> TrendType.STABLE
        }
    }
}
