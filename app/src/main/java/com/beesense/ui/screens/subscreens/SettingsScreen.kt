package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beesense.ui.theme.ThemeManager
import com.beesense.viewmodel.SettingsViewModel

/**
 * Obrazovka nastaveni aplikacie, kde uzivatel moze nastavit temu, notifikacie a dalsie parametre
 *
 * @param navController Controller pre navigaciu medzi obrazovkami
 * @param viewModel ViewModel pre spravu nastaveni aplikacie
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    // Ziskavanie nastaveni z databazy pomocou ViewModel
    val dbSettings by viewModel.settings.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    // Ziskanie aktualnej temy z ThemeManager
    val currentTheme by ThemeManager.isDarkTheme.collectAsState()

    // Inicializacia lokalnych stavov pre UI komponenty
    var isDarkMode by rememberSaveable { mutableStateOf(formState.isDarkMode) }
    var areNotificationsEnabled by rememberSaveable { mutableStateOf(formState.areNotificationsEnabled) }
    var weightThreshold by rememberSaveable { mutableStateOf(formState.weightThresholdKg.toString()) }
    var inactivityThresholdHours by rememberSaveable { mutableStateOf(formState.inactivityThresholdHours.toString()) }
    var notificationIntervalHours by rememberSaveable { mutableStateOf(formState.notificationIntervalHours.toString()) }

    // Synchronizacia temy pri prvom spusteni komponenty
    LaunchedEffect(Unit) {
        if (currentTheme != isDarkMode) {
            ThemeManager.setDarkTheme(isDarkMode)
            viewModel.updateDarkMode(isDarkMode)
        }
    }

    // Aktualizacia UI stavov pri zmene formState (napr. po nacitani z databazy)
    LaunchedEffect(formState) {
        isDarkMode = formState.isDarkMode
        areNotificationsEnabled = formState.areNotificationsEnabled
        weightThreshold = formState.weightThresholdKg.toString()
        inactivityThresholdHours = formState.inactivityThresholdHours.toString()
        notificationIntervalHours = formState.notificationIntervalHours.toString()

        // Zabezpecenie synchronizacie temy so stavom
        if (currentTheme != isDarkMode) {
            ThemeManager.setDarkTheme(isDarkMode)
        }
    }

    Scaffold(
        topBar = {
            // Horny panel aplikacie s nazvom a tlacidlom spat
            TopAppBar(
                title = { Text("Nastavenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Späť"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // Hlavny obsah obrazovky s moznostou scrollovania
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sekcia nastavenia temy
            Text(
                text = "Režim témy",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDarkMode) "Tmavý režim" else "Svetlý režim",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                // Prepinac tmavy/svetly rezim
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { newValue ->
                        isDarkMode = newValue
                        // Okamzita aktualizacia temy
                        ThemeManager.setDarkTheme(newValue)
                        // Aktualizacia vo ViewModeli pre zachovanie pri rotacii
                        viewModel.updateDarkMode(newValue)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Oddelovac medzi sekciami
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Sekcia nastavenia notifikacii
            Text(
                text = "Nastavenia notifikácií",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Povoliť notifikácie", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                // Prepinac povolenia notifikacii
                Switch(
                    checked = areNotificationsEnabled,
                    onCheckedChange = { newValue ->
                        areNotificationsEnabled = newValue
                        // Aktualizacia vo ViewModeli
                        viewModel.updateNotificationsEnabled(newValue)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Vstupne pole pre nastavenie limitu zmeny hmotnosti
            Text(
                text = "Limit zmeny hmotnosti (kg)",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = weightThreshold,
                onValueChange = { newValue ->
                    // Filtrovanie vstupu len na cisla a bodku pre desatinne cisla
                    val filteredValue = newValue.filter { ch -> ch.isDigit() || ch == '.' }
                    weightThreshold = filteredValue
                    // Aktualizacia vo ViewModeli
                    filteredValue.toFloatOrNull()?.let { viewModel.updateWeightThreshold(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 3.0") },
                singleLine = true
            )

            // Vstupne pole pre nastavenie limitu neaktivity
            Text(
                text = "Limit neaktivity (hodiny)",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = inactivityThresholdHours,
                onValueChange = { newValue ->
                    // Filtrovanie vstupu len na cisla
                    val filteredValue = newValue.filter { ch -> ch.isDigit() }
                    inactivityThresholdHours = filteredValue
                    // Aktualizacia vo ViewModeli
                    filteredValue.toIntOrNull()?.let { viewModel.updateInactivityThreshold(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 5") },
                singleLine = true
            )

            // Vstupne pole pre nastavenie intervalu notifikacii
            Text(
                text = "Interval notifikácií (hodiny)",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = notificationIntervalHours,
                onValueChange = { newValue ->
                    // Filtrovanie vstupu len na cisla
                    val filteredValue = newValue.filter { ch -> ch.isDigit() }
                    notificationIntervalHours = filteredValue
                    // Aktualizacia vo ViewModeli
                    filteredValue.toIntOrNull()?.let { viewModel.updateNotificationInterval(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 4") },
                singleLine = true
            )

            // Tlacidlo na ulozenie nastaveni a navrat spat
            Button(
                onClick = {
                    viewModel.save() // Ulozenie nastaveni
                    navController.popBackStack() // Navrat na predchadzajucu obrazovku
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("Uložiť nastavenia", fontSize = 18.sp)
            }
        }
    }
}
