package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beesense.data.repository.HiveConfig
import com.beesense.ui.components.DeleteConfirmationDialog
import com.beesense.ui.components.HiveCheckboxItem
import com.beesense.ui.viewmodels.HiveEditorViewModel
import com.beesense.ui.viewmodels.HiveManagementViewModel
import kotlinx.coroutines.launch

/**
 * Obrazovka pre vytvaranie a upravu konfiguracii vcelich ulov
 *
 * @param navController Controller pre navigaciu medzi obrazovkami
 * @param hiveManagementViewModel ViewModel pre spravu ulov poskytujuci data pre editaciu
 * @param editHiveId Volitelny parameter ID upravovaneho ula, null v pripade vytvarania noveho ula
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveEditorScreen(
    navController: NavController,
    hiveManagementViewModel: HiveManagementViewModel,
    editHiveId: Int? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Vytvorenie HiveEditorViewModel s dependencies pomocou factory
    val viewModel: HiveEditorViewModel = viewModel(
        factory = HiveEditorViewModelFactory(
            hiveManagementViewModel = hiveManagementViewModel,
            hiveId = editHiveId
        )
    )

    // Nacitanie existujuceho ula ak sme v rezime editacie
    LaunchedEffect(editHiveId) {
        editHiveId?.let { id ->
            // Nacitanie dat ula zo stavu hivesState
            hiveManagementViewModel.hivesState.value.find { it.id == id }?.let { hive ->
                viewModel.loadExistingHiveData(hive)
            }
        }
    }

    // Zobrazenie chybovych hlaseni v snackbare
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    // Zobrazi potvrdenie vymazania ula ak je to potrebne
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            title = "Odstrániť úľ",
            message = "Naozaj chcete odstrániť úľ '${viewModel.displayName}'?",
            onConfirm = {
                val hiveToDelete = HiveConfig(
                    id = viewModel.hiveId,
                    displayName = viewModel.displayName,
                    tableName = viewModel.tableName,
                    hasTemperatureSensor = viewModel.hasTemperatureSensor,
                    hasTemperatureOutside = viewModel.hasTemperatureOutside,
                    hasWeightLeft = viewModel.hasWeightLeft,
                    hasWeightRight = viewModel.hasWeightRight,
                    hasPressure = viewModel.hasPressure,
                    hasHumidity = viewModel.hasHumidity
                )
                hiveManagementViewModel.deleteHive(hiveToDelete) {
                    navController.popBackStack()
                }
                showDeleteConfirmation = false
            },
            onDismiss = {
                showDeleteConfirmation = false
            }
        )
    }

    Scaffold(
        topBar = {
            // Horny panel s nazvom a navigacnymi tlacidlami
            TopAppBar(
                title = { Text(text = if (viewModel.isEditMode) "Upraviť Úľ" else "Pridať Úľ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Späť"
                        )
                    }
                },
                // Tlacidlo na vymazanie ula v rezime upravy
                actions = {
                    if (viewModel.isEditMode) {
                        IconButton(
                            onClick = { showDeleteConfirmation = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Odstrániť úľ",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Hlavny obsah obrazovky s formulárom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vstupne pole pre nazov ula
            OutlinedTextField(
                value = viewModel.displayName,
                onValueChange = { viewModel.updateDisplayName(it) },
                label = { Text("Názov úľa") },
                modifier = Modifier.fillMaxWidth()
            )

            // Vstupne pole pre nazov databazovej tabulky (zakazane v rezime upravy)
            OutlinedTextField(
                value = viewModel.tableName,
                onValueChange = { viewModel.updateTableName(it) },
                label = { Text("Názov tabuľky") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isEditMode // Zakázanie úpravy názvu tabuľky v režime úprav
            )

            // Nadpis sekcie výberu senzorov
            Text(
                text = "Výber senzorov",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Oddelovač pre vizuálne oddelenie sekcie
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            // Checkboxy pre jednotlivé senzory
            HiveCheckboxItem(
                label = "Teplota senzoru",
                checked = viewModel.hasTemperatureSensor,
                onCheckedChange = { viewModel.updateHasTemperatureSensor(it) }
            )

            HiveCheckboxItem(
                label = "Vonkajšia teplota",
                checked = viewModel.hasTemperatureOutside,
                onCheckedChange = { viewModel.updateHasTemperatureOutside(it) }
            )

            HiveCheckboxItem(
                label = "Hmotnosť (ľavá)",
                checked = viewModel.hasWeightLeft,
                onCheckedChange = { viewModel.updateHasWeightLeft(it) }
            )

            HiveCheckboxItem(
                label = "Hmotnosť (pravá)",
                checked = viewModel.hasWeightRight,
                onCheckedChange = { viewModel.updateHasWeightRight(it) }
            )

            HiveCheckboxItem(
                label = "Tlak vzduchu",
                checked = viewModel.hasPressure,
                onCheckedChange = { viewModel.updateHasPressure(it) }
            )

            HiveCheckboxItem(
                label = "Vlhkosť vzduchu",
                checked = viewModel.hasHumidity,
                onCheckedChange = { viewModel.updateHasHumidity(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tlačidlo pre uloženie alebo aktualizáciu úľa
            Button(
                onClick = {
                    viewModel.validateAndSaveHive {
                        // Po úspešnom uložení - návrat na predchádzajúcu obrazovku
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                // Zobrazenie indikátora načítavania počas spracovania
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                }
                Text(if (viewModel.isEditMode) "AKTUALIZOVAŤ" else "ULOŽIŤ")
            }
        }
    }
}

/**
 * Factory trieda pre vytvorenie HiveEditorViewModel s potrebnymi zavislostami
 *
 * @param hiveManagementViewModel ViewModel obsahujuci data ulov
 * @param hiveId Volitelny parameter ID upravovaneho ula
 */
class HiveEditorViewModelFactory(
    private val hiveManagementViewModel: HiveManagementViewModel,
    private val hiveId: Int? = null
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HiveEditorViewModel::class.java)) {
            return HiveEditorViewModel(
                savedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
                    hiveId?.let { set("hiveId", it) }
                },
                hiveManagementViewModel = hiveManagementViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
