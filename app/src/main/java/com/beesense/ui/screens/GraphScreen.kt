package com.beesense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.ui.viewmodels.HiveGraphViewModel
import com.beesense.ui.components.charts.HiveDataChart
import com.beesense.ui.components.DateRangePicker
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext
import com.beesense.data.db.AppContainer

// Pomoc s AI

/**
 * Obrazovka pre zobrazenie grafov s udajmi z ulov.
 *
 * Tato obrazovka umoznuje uzivatelovi zobrazit rozne typy dat (teplota, vaha, atd.)
 * z vybrateho ula vo forme grafu. Uzivatel moze vybrat konkretny ul, typ dat
 * a casove obdobie, za ktore chce data zobrazit.
 */
@Composable
fun GraphScreen() {
    // Ziskame kontext a reference na AppContainer pre pristup k repozitarom
    val context = LocalContext.current
    // Vytvorime instanciu AppContainer, ktora poskytuje pristup k repozitarom
    val appContainer = remember { AppContainer(context) }

    // Vytvorime viewModel s repozitarom, ktory bude dodavat data pre tuto obrazovku
    val viewModel: HiveGraphViewModel = viewModel(
        factory = HiveGraphViewModel.Factory(
            context = context,
            hiveRepository = appContainer.hiveRepository // Dodame repozitar pre pracu s ulmi
        )
    )

    // Ziskanie aktualneho stavu UI z ViewModelu a jeho sledovanie
    val uiState by viewModel.uiState.collectAsState()

    // Hlavny layout obrazovky - vertikalny stlpec s moznostou rolovania
    Column(
        modifier = Modifier
            .fillMaxSize()           // Vyplni celu obrazovku
            .padding(16.dp)          // Pridanie vonkajsieho paddingu
            .verticalScroll(rememberScrollState()) // Umozni rolovanie, ak je obsah vacsi nez obrazovka
    ) {
        // Nadpis obrazovky
        Text(
            text = "Grafy údajov",
            style = MaterialTheme.typography.headlineMedium, // Velky nadpis
            fontWeight = FontWeight.Bold,                  // Tucny text
            modifier = Modifier.padding(bottom = 16.dp)    // Odsadenie pod nadpisom
        )

        // Sekcia pre vyber ula
        HiveSelectionSection(
            selectedHive = uiState.selectedHive,         // Aktualne vybrany ul
            hives = uiState.availableHives,              // Zoznam dostupnych ulov
            onHiveSelected = viewModel::onHiveSelected,  // Callback pri zmene vyberu
            isLoading = uiState.isLoadingHives           // Ci sa prave nacitavaju uly
        )

        Spacer(modifier = Modifier.height(16.dp)) // Medzera medzi sekciami

        // Sekcia pre vyber typu dat
        DataTypeSelectionSection(
            selectedDataType = uiState.selectedDataType,            // Aktualne vybrany typ dat
            dataTypes = uiState.availableDataTypes,                // Zoznam dostupnych typov dat
            onDataTypeSelected = viewModel::onDataTypeSelected     // Callback pri zmene vyberu
        )

        Spacer(modifier = Modifier.height(16.dp)) // Medzera medzi sekciami

        // Sekcia pre vyber casoveho obdobia
        TimePeriodSelectionSection(
            selectedTimePeriod = uiState.selectedTimePeriod,         // Aktualne vybrane obdobie
            onTimePeriodSelected = viewModel::onTimePeriodSelected,  // Callback pri zmene obdobia
            startDate = uiState.customStartDate,                     // Pociatocny datum pre vlastne obdobie
            endDate = uiState.customEndDate,                         // Koncovy datum pre vlastne obdobie
            onStartDateSelected = viewModel::onStartDateSelected,    // Callback pri zmene pociatocneho datumu
            onEndDateSelected = viewModel::onEndDateSelected,        // Callback pri zmene koncoveho datumu
            showCustomDatePickers = uiState.selectedTimePeriod == HiveGraphViewModel.TimePeriod.CUSTOM // Zobrazit vyber datumov len pri vlastnom obdobi
        )

        Spacer(modifier = Modifier.height(24.dp)) // Vacsia medzera pred grafom

        // Sekcia pre zobrazenie grafu a hodnot
        DataDisplaySection(
            uiState = uiState,                            // Stav UI s datami
            onValueSelected = viewModel::onValueSelected  // Callback pri vybere bodu v grafe
        )
    }
}

/**
 * Sekcia pre vyber aktivneho ula.
 *
 * @param selectedHive Aktualne vybrany ul
 * @param hives Zoznam dostupnych ulov
 * @param onHiveSelected Callback volany pri vybere noveho ula
 * @param isLoading Ci sa prave nacitavaju uly
 */
@Composable
fun HiveSelectionSection(
    selectedHive: HiveGraphViewModel.HiveDisplay,
    hives: List<HiveGraphViewModel.HiveDisplay>,
    onHiveSelected: (HiveGraphViewModel.HiveDisplay) -> Unit,
    isLoading: Boolean
) {
    // Popisok sekcie
    Text(
        text = "Výber úľa",
        style = MaterialTheme.typography.titleMedium // Stredne velky nadpis
    )

    // Stav pre otvorenie/zatvorenie rozbaloveho menu
    var expanded by remember { mutableStateOf(false) }

    // Kontajner pre rozbalove menu
    Box {
        // Tlacidlo pre otvorenie menu - karta s vybranym ulom
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()                 // Sirka cez celu obrazovku
                .clickable { expanded = true }  // Pri kliknuti sa otvori menu
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Obsah na zaciatku a konci riadku
                verticalAlignment = Alignment.CenterVertically    // Vertikalne zarovnanie na stred
            ) {
                // Text s nazvom vybraneho ula alebo nacitavanie
                Text(
                    text = if (isLoading) "Načítavam úle..." else selectedHive.displayName
                )
                // Sipka dole indikujuca rozbalove menu
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            }
        }

        // Rozbalove menu so zoznamom ulov
        DropdownMenu(
            expanded = expanded && !isLoading,        // Menu je otvorene len ak nie je nacitavanie
            onDismissRequest = { expanded = false },  // Zatvorenie pri kliknuti mimo menu
            modifier = Modifier.fillMaxWidth(0.9f)    // Sirka menu (90% obrazovky)
        ) {
            // Vytvorenie polozky menu pre kazdy ul
            hives.forEach { hive ->
                DropdownMenuItem(
                    text = { Text(text = hive.displayName) }, // Text polozky
                    onClick = {
                        onHiveSelected(hive)  // Volanie callbacku pri vybere ula
                        expanded = false       // Zatvorenie menu po vybere
                    }
                )
            }
        }
    }
}

/**
 * Sekcia pre vyber typu dat zobrazenych v grafe.
 *
 * @param selectedDataType Aktualne vybrany typ dat
 * @param dataTypes Zoznam dostupnych typov dat
 * @param onDataTypeSelected Callback volany pri vybere noveho typu dat
 */
@Composable
fun DataTypeSelectionSection(
    selectedDataType: HiveGraphViewModel.DataType,
    dataTypes: List<HiveGraphViewModel.DataType>,
    onDataTypeSelected: (HiveGraphViewModel.DataType) -> Unit
) {
    // Popisok sekcie
    Text(
        text = "Typ údajov",
        style = MaterialTheme.typography.titleMedium // Stredne velky nadpis
    )

    // Stav pre otvorenie/zatvorenie rozbaloveho menu
    var expanded by remember { mutableStateOf(false) }

    // Kontajner pre rozbalove menu
    Box {
        // Tlacidlo pre otvorenie menu - karta s vybranym typom dat
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()                // Sirka cez celu obrazovku
                .clickable { expanded = true } // Pri kliknuti sa otvori menu
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Obsah na zaciatku a konci riadku
                verticalAlignment = Alignment.CenterVertically    // Vertikalne zarovnanie na stred
            ) {
                // Text s nazvom vybraneho typu dat
                Text(text = selectedDataType.displayName)
                // Sipka dole indikujuca rozbalove menu
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            }
        }

        // Rozbalove menu so zoznamom typov dat
        DropdownMenu(
            expanded = expanded,                     // Stav otvorenia menu
            onDismissRequest = { expanded = false }, // Zatvorenie pri kliknuti mimo menu
            modifier = Modifier.fillMaxWidth(0.9f)   // Sirka menu (90% obrazovky)
        ) {
            // Vytvorenie polozky menu pre kazdy typ dat
            dataTypes.forEach { dataType ->
                DropdownMenuItem(
                    text = { Text(text = dataType.displayName) }, // Text polozky
                    onClick = {
                        onDataTypeSelected(dataType) // Volanie callbacku pri vybere typu
                        expanded = false              // Zatvorenie menu po vybere
                    }
                )
            }
        }
    }
}

/**
 * Sekcia pre vyber casoveho obdobia dat zobrazenych v grafe.
 *
 * @param selectedTimePeriod Aktualne vybrany casovy period
 * @param onTimePeriodSelected Callback volany pri vybere noveho obdobia
 * @param startDate Pociatocny datum pre vlastne obdobie
 * @param endDate Koncovy datum pre vlastne obdobie
 * @param onStartDateSelected Callback volany pri zmene pociatocneho datumu
 * @param onEndDateSelected Callback volany pri zmene koncoveho datumu
 * @param showCustomDatePickers Ci sa maju zobrazit komponenty na vyber datumov
 */
@Composable
fun TimePeriodSelectionSection(
    selectedTimePeriod: HiveGraphViewModel.TimePeriod,
    onTimePeriodSelected: (HiveGraphViewModel.TimePeriod) -> Unit,
    startDate: Date?,
    endDate: Date?,
    onStartDateSelected: (Date) -> Unit,
    onEndDateSelected: (Date) -> Unit,
    showCustomDatePickers: Boolean
) {
    // Popisok sekcie
    Text(
        text = "Časové obdobie",
        style = MaterialTheme.typography.titleMedium // Stredne velky nadpis
    )

    // Stav pre otvorenie/zatvorenie rozbaloveho menu
    var expanded by remember { mutableStateOf(false) }

    // Kontajner pre rozbalove menu
    Box {
        // Tlacidlo pre otvorenie menu - karta s vybranym casovym obdobim
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()                // Sirka cez celu obrazovku
                .clickable { expanded = true } // Pri kliknuti sa otvori menu
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Obsah na zaciatku a konci riadku
                verticalAlignment = Alignment.CenterVertically    // Vertikalne zarovnanie na stred
            ) {
                // Text s nazvom vybraneho casoveho obdobia
                Text(text = selectedTimePeriod.displayName)
                // Sipka dole indikujuca rozbalove menu
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            }
        }

        // Rozbalove menu so zoznamom casovych obdobi
        DropdownMenu(
            expanded = expanded,                     // Stav otvorenia menu
            onDismissRequest = { expanded = false }, // Zatvorenie pri kliknuti mimo menu
            modifier = Modifier.fillMaxWidth(0.9f)   // Sirka menu (90% obrazovky)
        ) {
            // Vytvorenie polozky menu pre kazde casove obdobie
            HiveGraphViewModel.TimePeriod.entries.forEach { timePeriod ->
                DropdownMenuItem(
                    text = { Text(text = timePeriod.displayName) }, // Text polozky
                    onClick = {
                        onTimePeriodSelected(timePeriod) // Volanie callbacku pri vybere obdobia
                        expanded = false                  // Zatvorenie menu po vybere
                    }
                )
            }
        }
    }

    // Komponenty na vyber vlastneho datumoveho rozsahu
    if (showCustomDatePickers) {
        Spacer(modifier = Modifier.height(16.dp)) // Medzera nad vyberom datumov

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Komponenty rozlozene rovnomerne
        ) {
            // Vyber pociatocneho datumu
            DateRangePicker(
                label = "Od dátumu",                   // Popisok pre pociatocny datum
                date = startDate,                      // Aktualny pociatocny datum
                onDateSelected = onStartDateSelected,  // Callback pri zmene datumu
                modifier = Modifier.weight(1f)         // Zaberanie polovice sirky
            )

            Spacer(modifier = Modifier.width(8.dp)) // Medzera medzi vybermi datumov

            // Vyber koncoveho datumu
            DateRangePicker(
                label = "Do dátumu",                  // Popisok pre koncovy datum
                date = endDate,                       // Aktualny koncovy datum
                onDateSelected = onEndDateSelected,   // Callback pri zmene datumu
                modifier = Modifier.weight(1f),       // Zaberanie polovice sirky
                minDate = startDate                   // Koncovy datum nesmie byt pred pociatocnym
            )
        }
    }
}

/**
 * Sekcia pre zobrazenie grafu a pripadnych chybovych hlasok alebo prazdnych stavov.
 *
 * @param uiState Aktualny stav UI s datami pre graf
 * @param onValueSelected Callback volany pri vybere bodu v grafe
 */
@Composable
fun DataDisplaySection(
    uiState: HiveGraphViewModel.UiState,
    onValueSelected: (String, Float) -> Unit
) {
    when {
        // Zobrazenie indikatorou nacitavania, ak sa data nacitavaju
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center // Obsah na strede boxu
            ) {
                // Kruhovy indikator nacitavania
                CircularProgressIndicator()
                // Text pod indikatorom
                Text(
                    text = "Načítavam údaje...",
                    modifier = Modifier.padding(top = 64.dp) // Odsadenie od indikatora
                )
            }
        }

        // Zobrazenie chybovej hlasky ak doslo k chybe
        uiState.error != null -> {
            ErrorDisplay(error = uiState.error)
        }

        // Zobrazenie hlasky ak nie su dostupne ziadne data
        uiState.hiveData.isEmpty() -> {
            EmptyDataDisplay()
        }

        // Zobrazenie grafu ak su dostupne data
        else -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Kontajner pre graf s fixnou vyskou
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Graf s datami ula
                    HiveDataChart(
                        hiveData = uiState.hiveData,               // Data pre graf
                        dataType = uiState.selectedDataType,      // Vybrany typ dat
                        onValueSelected = onValueSelected         // Callback pri vybere bodu
                    )
                }

                // Zobrazenie informacii o vybranom bode, ak existuje
                uiState.selectedDataPoint?.let { dataPoint ->
                    Spacer(modifier = Modifier.height(16.dp)) // Medzera nad detailmi
                    SelectedValueDisplay(
                        timestamp = dataPoint.first,              // Casova znacka
                        value = dataPoint.second,                 // Hodnota
                        dataType = uiState.selectedDataType       // Typ dat pre zobrazenie jednotky
                    )
                }
            }
        }
    }
}

/**
 * Komponent pre zobrazenie chybovej hlasky.
 *
 * @param error Text chybovej hlasky
 */
@Composable
fun ErrorDisplay(error: String) {
    // Karta s chybovou hlaskou s vyraznou farbou pozadia
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer // Cervene pozadie pre chyby
        )
    ) {
        // Obsah karty
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Obsah zarovnany na stred
        ) {
            // Nadpis chyby
            Text(
                text = "Chyba pri načítaní údajov",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer // Kontrastna farba textu
            )
            Spacer(modifier = Modifier.height(8.dp)) // Medzera medzi nadpisom a detailmi
            // Detail chyby
            Text(
                text = error, // Obsah chyboveho hlasenia
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer // Kontrastna farba textu
            )
        }
    }
}

/**
 * Komponent pre zobrazenie informacie o prazdnych datach.
 */
@Composable
fun EmptyDataDisplay() {
    // Karta s informacnou hlaskou
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Obsah karty
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Obsah zarovnany na stred
        ) {
            // Nadpis hlasky
            Text(
                text = "Žiadne údaje nie sú dostupné",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp)) // Medzera medzi nadpisom a detailmi
            // Vysvetlujuci text
            Text(
                text = "Pre zvolené obdobie a úľ nie sú k dispozícii žiadne údaje",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Komponent pre zobrazenie detailov o vybranom bode v grafe.
 *
 * @param timestamp Casova znacka vybraneho bodu
 * @param value Hodnota vybraneho bodu
 * @param dataType Typ dat pre urcenie jednotky
 */
@Composable
fun SelectedValueDisplay(
    timestamp: String,
    value: Float,
    dataType: HiveGraphViewModel.DataType
) {
    // Karta so zvolenymi farbami pozadia a textu
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        // Obsah karty
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nadpis
            Text(
                text = "Vybraná hodnota",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp)) // Medzera medzi nadpisom a detailmi
            // Riadok s casom a hodnotou
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Cas vlavo, hodnota vpravo
            ) {
                // Cas merania
                Text(text = "Čas: $timestamp")
                // Hodnota s jednotkou
                Text(
                    text = "${value.toString()} ${dataType.unit}", // Hodnota a jednotka
                    fontWeight = FontWeight.Bold,                 // Tucne zvyraznenie
                    color = MaterialTheme.colorScheme.primary     // Farebne zvyraznenie
                )
            }
        }
    }
}