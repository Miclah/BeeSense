package com.beesense.ui.components.charts

import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.beesense.data.model.HiveData
import com.beesense.ui.viewmodels.HiveGraphViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.*

// Pomoc s AI

/**
 * Komponent pre zobrazenie grafu udajov z ula.
 *
 * Tento komponent pouziva MPAndroidChart kniznici integrovanej s Jetpack Compose
 * pomocou AndroidView. Zobrazuje carovy graf rôznych senzorovych dat z ula
 * a podporuje interakcie ako zoom, posun a vyber konkretnych bodov.
 *
 * @param hiveData Zoznam dat z ula, ktore sa maju zobrazit
 * @param dataType Typ udajov, ktory sa ma zobrazit (teplota, vaha, atd.)
 * @param onValueSelected Callback funkcia volana pri vybere datoveho bodu
 * @param modifier Volitelny Compose modifier pre nastavenie velkosti a pozicie
 */
@Composable
fun HiveDataChart(
    hiveData: List<HiveData>,
    dataType: HiveGraphViewModel.DataType,
    onValueSelected: (timestamp: String, value: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Ziskanie aktualneho kontextu a konfiguracie obrazovky
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Ulozenie konfiguracie grafu do stavu, ktory prezije rekompoziciu
    // Toto je dolezite, aby sa pri aktualizacii dat nestratila pozicia v grafe
    val chartConfig = remember { ChartConfigState() }

    // Extrahovanie farieb z aktualnej temy pre zjednoteny dizajn
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()      // Hlavna farba pre carty
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()  // Farba pre zvyraznenie
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()      // Farba pozadia
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()  // Farba textu a osi
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f).toArgb()  // Svetlejsia farba mriezky

    // Vytvorenie a konfiguracia Android grafu v Compose UI pomocou AndroidView
    AndroidView(
        modifier = modifier.fillMaxSize(), // Graf vyplni cely dostupny priestor
        factory = { context ->
            // Vytvorenie instancie LineChart (MPAndroidChart)
            LineChart(context).apply {
                // Konfiguracia zakladnych vlastnosti grafu
                description.isEnabled = false // Vypnutie popisku v rohu grafu
                legend.apply {
                    isEnabled = true          // Povolenie legendy
                    textColor = onSurfaceColor // Farba textu legendy podla temy
                }

                setBackgroundColor(surfaceColor)  // Nastavenie farby pozadia
                setDrawGridBackground(false)      // Vypnutie vykreslovania pozadia mriezky

                // Konfiguracia horizontalnej osi (X)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM  // Os X bude na spodku grafu
                    granularity = 1f                      // Minimalna vzdialenost medzi hodnotami
                    setDrawGridLines(true)                // Povolenie ciar mriezky
                    gridColor = gridLineColor             // Farba mriezky
                    textColor = onSurfaceColor            // Farba popiskov
                    axisLineColor = onSurfaceColor         // Farba osi
                    setAvoidFirstLastClipping(true)        // Zabránenie odrezaniu krajnych popiskov
                    labelRotationAngle = 0f                // Horizontalne popisky
                }

                // Konfiguracia lavej vertikalnej osi (Y)
                axisLeft.apply {
                    setDrawGridLines(true)                // Povolenie horizontalnych ciar mriezky
                    gridColor = gridLineColor             // Farba mriezky
                    textColor = onSurfaceColor            // Farba popiskov
                    axisLineColor = onSurfaceColor         // Farba osi
                    setDrawZeroLine(true)                  // Vykreslenie nuloveho bodu
                    zeroLineColor = onSurfaceColor         // Farba ciare na nule
                    setDrawLimitLinesBehindData(true)      // Limity vykreslit za datami
                }

                axisRight.isEnabled = false  // Vypnutie pravej osi Y (nebudeme ju pouzivat)

                // Nastavenie moznosti interakcie
                isDragEnabled = true         // Povolenie posuvania grafu
                isScaleXEnabled = true       // Povolenie horizontalneho zoomu
                isScaleYEnabled = false      // Vypnutie vertikalneho zoomu pre lepsie UX
                setPinchZoom(true)           // Povolenie pinch-zoom gesta
                isDoubleTapToZoomEnabled = true // Povolenie dvojkliku pre zoom
                setVisibleXRangeMinimum(5f)  // Minimalny pocet viditelnych bodov
                setVisibleXRangeMaximum(50f) // Maximalny pocet viditelnych bodov

                // DÔLEŽITÉ: Vypnúť všetky animácie
                setDragDecelerationEnabled(false) // Vypnúť animáciu po pustení posúvania

                // Nastavenie zvyraznovania hodnot
                isHighlightPerTapEnabled = true    // Povolenie zvyraznenia pri kliknuti
                isHighlightPerDragEnabled = true   // Povolenie zvyraznenia pri posuvani

                // Nastavenie posluchaca pre vyber hodnot - toto je volane, ked uzivatel klikne na bod v grafe
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { entry ->
                            // DÔLEŽITÉ: Najprv uložíme aktuálny zobrazený rozsah
                            // Tymto predideme posunu grafu pri vybere bodu
                            chartConfig.visibleXMin = lowestVisibleX  // Ulozime lavy okraj
                            chartConfig.visibleXMax = highestVisibleX // Ulozime pravy okraj
                            chartConfig.selectedEntryIndex = entry.x.toInt() // Ulozime index vybrateho bodu

                            val index = entry.x.toInt()
                            if (index >= 0 && index < hiveData.size) {
                                val timestamp = hiveData[index].timestamp // Casova znacka vybrateho bodu
                                val value = entry.y                      // Hodnota vybrateho bodu

                                // Zavoláme callback funkciu s vybranymi udajmi
                                onValueSelected(timestamp, value)

                                // FIX: Zrusime automatické centrovanie výberom bodov
                                // nastavením rovnakého rozsahu naspäť
                                val visibleRange = chartConfig.visibleXMax - chartConfig.visibleXMin
                                setVisibleXRangeMaximum(visibleRange)
                                moveViewToX(chartConfig.visibleXMin)

                                // Vynutime prekreslenie grafu bez animacie
                                invalidate()
                            }
                        }
                    }

                    override fun onNothingSelected() {
                        // Reset stavu vybrateho bodu, ked uzivatel klikne mimo bodov
                        chartConfig.selectedEntryIndex = -1
                    }
                })

                // Nastavenie posluchaca gesticulacie pre sledovanie zmien v grafe
                onChartGestureListener = object : OnChartGestureListener {
                    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                        // Ulozime poziciu na zaciatku gesta
                        chartConfig.visibleXMin = lowestVisibleX
                        chartConfig.visibleXMax = highestVisibleX
                    }

                    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                        // Aktualizujeme poziciu na konci gesta
                        chartConfig.visibleXMin = lowestVisibleX
                        chartConfig.visibleXMax = highestVisibleX
                    }

                    override fun onChartLongPressed(me: MotionEvent?) {}

                    override fun onChartDoubleTapped(me: MotionEvent?) {
                        // Pri dvojitom kliknuti resetujeme pohled na celý graf
                        fitScreen()
                        chartConfig.reset()
                    }

                    override fun onChartSingleTapped(me: MotionEvent?) {}

                    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}

                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                        // Aktualizujeme poziciu pri zmene mierky
                        chartConfig.visibleXMin = lowestVisibleX
                        chartConfig.visibleXMax = highestVisibleX
                    }

                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                        // Aktualizujeme poziciu pri posunuti grafu
                        chartConfig.visibleXMin = lowestVisibleX
                        chartConfig.visibleXMax = highestVisibleX
                    }
                }

                // Uplne vypnutie animacii pre stabilne spravanie
                animateX(0)
                animateY(0)
            }
        },
        update = { lineChart ->
            // Tato cast sa zavola pri aktualizacii dat alebo typu dat
            // Docasne vypneme interakcie aby nedoslo k neocakavanym pohybom
            lineChart.setTouchEnabled(false)

            // Extrahujeme data pre vybrany typ merania (teplota, vaha, atd.)
            val entries = hiveData.mapIndexed { index, data ->
                val value = when (dataType) {
                    HiveGraphViewModel.DataType.TOTAL_WEIGHT -> data.totalWeight
                    HiveGraphViewModel.DataType.TEMPERATURE_SENSOR -> data.temperatureSensor
                    HiveGraphViewModel.DataType.TEMPERATURE_OUTSIDE -> data.temperatureOutside
                    HiveGraphViewModel.DataType.WEIGHT_LEFT -> data.weightLeft
                    HiveGraphViewModel.DataType.WEIGHT_RIGHT -> data.weightRight
                    HiveGraphViewModel.DataType.PRESSURE -> data.pressure
                    HiveGraphViewModel.DataType.HUMIDITY -> data.humidity
                }

                // Pre kazdu neluovu hodnotu vytvorime Entry bod s indexom a hodnotou
                value?.let { Entry(index.toFloat(), it) }
            }.filterNotNull() // Odstranime vsetky null hodnoty

            // Urcime, ci data boli agregovane - ak je pocet bodov mensi nez povodny pocet dat a je ich vela
            val isDataAggregated = entries.size < hiveData.size && hiveData.size > 100

            // Vytvorime dataset s nastaveniami pre carovy graf
            val dataSet = LineDataSet(entries, dataType.displayName).apply {
                color = primaryColor  // Farba ciary podla temy
                setCircleColor(primaryColor)  // Farba bodov podla temy

                // Pre agregovane data nezobrazujeme body, len ciary
                setDrawCircles(!isDataAggregated)
                setDrawCircleHole(!isDataAggregated)

                circleRadius = 3f        // Velkost bodov
                circleHoleRadius = 1.5f  // Velkost otvoru v bodoch
                lineWidth = 2.5f        // Hrubka ciary
                valueTextColor = onSurfaceColor // Farba textu hodnot
                setDrawValues(false)     // Nezobrazujeme hodnoty priamo v grafe

                // Rozlisne nastavenie pre agregovane vs. neagregovane data
                if (isDataAggregated) {
                    // Pre agregovane data pouzijeme zahladeny graf s vyplnou
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = primaryColor
                    fillAlpha = 50 // polopriehladne
                } else {
                    // Pre neagregovane data pouzijeme linearny graf bez vyplne
                    mode = LineDataSet.Mode.LINEAR
                    setDrawFilled(false)
                }

                // Nastavenie zvyraznenia pri vybere bodu
                highLightColor = secondaryColor
                setDrawHighlightIndicators(true)
                highlightLineWidth = 1.5f

                valueTextSize = 10f
            }

            // Vytvorime objekt LineData s nasim datasetom
            val lineData = LineData(dataSet)
            lineChart.data = lineData

            // Konfiguracia popiskov na X osi - casove znacky
            val xLabels = if (hiveData.size > 50) {
                // Pre velke mnozstvo dat zobrazime len niekolko popiskov
                val labelIndices = mutableListOf<Int>()
                val step = hiveData.size / 10 // Priblizne 10 popiskov

                // Vypocitame indexy, kde budu popisky
                (0 until hiveData.size step step).forEach { labelIndices.add(it) }

                // Vytvorime pole popiskov - prazdny retazcec pre nepopiskovane body
                hiveData.mapIndexed { index, data ->
                    if (index in labelIndices) formatTimestampForLabel(data.timestamp) else ""
                }
            } else {
                // Pre male mnozstvo bodov zobrazime vsetky popisky
                hiveData.map { formatTimestampForLabel(it.timestamp) }
            }

            // Nastavime vytvorene popisky do formatovaca X osi
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

            // DOLEZITE: Zachovat poziciu pohladu bez animacii
            if (chartConfig.hasValidRange() && chartConfig.visibleXMax <= entries.size) {
                // Vypocitame aktualnu viditelnu sirku
                val visibleRange = chartConfig.visibleXMax - chartConfig.visibleXMin

                // Nastavime rozsah bez animacie
                lineChart.setVisibleXRangeMaximum(visibleRange)
                lineChart.moveViewToX(chartConfig.visibleXMin)

                // Zvyraznime predtym vybrany bod ak existuje
                if (chartConfig.selectedEntryIndex >= 0 && chartConfig.selectedEntryIndex < entries.size) {
                    lineChart.highlightValue(chartConfig.selectedEntryIndex.toFloat(), 0, false)
                }
            } else {
                // Pociatocne zobrazenie - ukazeme max 50 bodov, fokus na najnovsie data
                chartConfig.reset() // Inicializujeme s predvolenymi hodnotami
                if (entries.size > 50) {
                    lineChart.setVisibleXRangeMaximum(50f)
                    // Presunieme sa na najnovsie data
                    lineChart.moveViewToX(entries.size.toFloat() - 1)
                    // Ulozime pociatocny rozsah
                    chartConfig.visibleXMin = lineChart.lowestVisibleX
                    chartConfig.visibleXMax = lineChart.highestVisibleX
                }
            }

            // Pridame popis ak su data agregovane
            if (isDataAggregated) {
                lineChart.description.apply {
                    isEnabled = true
                    text = "Údaje sú agregované pre lepšiu prehľadnosť"
                    textColor = onSurfaceColor
                }
            } else {
                lineChart.description.isEnabled = false
            }

            // Znovu povolime dotyky na konci aktualizacie
            lineChart.setTouchEnabled(true)

            // Aktualizujeme graf bez animacie
            lineChart.invalidate()
        }
    )
}

/**
 * Pomocna funkcia na nastavenie presneho viditelneho rozsahu X osi.
 *
 * @param min Minimum viditelneho rozsahu (prva zobrazena hodnota)
 * @param max Maximum viditelneho rozsahu (posledna zobrazena hodnota)
 */
private fun LineChart.setVisibleXRange(min: Float, max: Float) {
    val range = max - min
    if (range > 0) {
        setVisibleXRangeMaximum(range)  // Nastavime presne mnozstvo viditelnych bodov
        moveViewToX(min)                // Presunieme view na pociatocny bod
    }
}

/**
 * Trieda uchovavajuca stav grafu pocas rekompoznicii.
 *
 * Tato pomocna trieda sleduje poziciu a vybrany bod v grafe,
 * co umoznuje bezproblemove aktualizacie dat bez straty pozicie.
 */
class ChartConfigState {
    var visibleXMin: Float = 0f      // Lavy okraj viditelnej oblasti
    var visibleXMax: Float = 0f      // Pravy okraj viditelnej oblasti
    var selectedEntryIndex: Int = -1 // Index vybrateho datoveho bodu (-1 = nic)

    /**
     * Reset stavu na predvolene hodnoty.
     */
    fun reset() {
        visibleXMin = 0f
        visibleXMax = 0f
        selectedEntryIndex = -1
    }

    /**
     * Kontroluje, ci stav obsahuje platny rozsah.
     *
     * @return True ak ulozeny rozsah je platny, inak false
     */
    fun hasValidRange(): Boolean {
        return visibleXMin >= 0f && visibleXMax > visibleXMin
    }
}

/**
 * Pomocna funkcia na formatovanie casovej znacky pre popisky X osi.
 *
 * Adaptivne formatuje datumove znacky, aby zobrazovali len relevantne informacie:
 * - Ak je hodnota z dnesneho dna, zobrazi sa len cas
 * - Ak je hodnota z tohto roku, zobrazi sa datum bez roku
 * - Ak je hodnota z ineho roku, zobrazi sa cely datum s rokom
 *
 * @param timestamp Casova znacka vo formate "dd-MM-yyyy HH:mm:ss"
 * @return Formatovana casova znacka pre popis v grafe
 */
private fun formatTimestampForLabel(timestamp: String): String {
    try {
        // Parsovanie vstupneho formatu casovej znacky
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(timestamp) ?: return timestamp

        // Ziskanie aktualneho datumu pre porovnanie
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val thisYear = calendar.get(Calendar.YEAR)

        // Nastavenie kalendara na datum zo znacky
        calendar.time = date
        val dataDay = calendar.get(Calendar.DAY_OF_YEAR)
        val dataYear = calendar.get(Calendar.YEAR)

        // Vyber formatu podla toho, ci je znacka z dnesneho dna, tohto roku alebo starsieho
        return when {
            dataYear != thisYear -> {
                // Iny rok - zobrazit datum s rokom
                SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(date)
            }
            dataDay != today -> {
                // Tento rok, ale iny den - zobrazit datum bez roku
                SimpleDateFormat("dd.MM", Locale.getDefault()).format(date)
            }
            else -> {
                // Dnesny den - staci zobrazit cas
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
        }
    } catch (e: Exception) {
        // V pripade chyby vratime povodny retazec
        return timestamp
    }
}
