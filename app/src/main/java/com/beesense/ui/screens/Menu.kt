package com.beesense.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.beesense.ui.navigation.Screen

/**
 * Obrazovka obsahujuca hlavne menu aplikacie.
 *
 * Tato obrazovka sluzi ako vstupny bod do roznych casti aplikacie.
 * Obsahuje karty pre kazdu hlavnu funkcionalitu, ktore po kliknuti
 * naviguju uzivatela na prislusnu obrazovku.
 *
 * @param navController Controller pouzivany na navigaciu medzi obrazovkami
 */
@OptIn(ExperimentalMaterial3Api::class) // Oznacenie, ze pouzivame experimentalne API z Material3
@Composable
fun MenuScreen(navController: NavController) {
    // Hlavny layout obrazovky s top app bar
    Scaffold(
        // Horny panel s nadpisom "Menu"
        topBar = {
            TopAppBar(
                title = {
                    // Nadpis "Menu" v hornom paneli
                    Text(
                        "Menu",
                        style = MaterialTheme.typography.headlineMedium,  // Vacsi text pre nadpis
                        color = MaterialTheme.colorScheme.onSurface,      // Farba textu kompatibilna s pozadim
                        maxLines = 1,                                     // Maximalne 1 riadok
                        overflow = TextOverflow.Ellipsis                  // Zobrazenie "..." ak je text pridlhy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,  // Farba pozadia panela
                )
            )
        }
    ) { paddingValues ->
        // Hlavny obsah obrazovky s paddingom podla Scaffoldu
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)  // Aplikuje padding z layoutu Scaffold
                .padding(16.dp)          // Dodatocny padding pre obsah
                .verticalScroll(rememberScrollState()),  // Umoznuje rolovanie ak je obsah prilis velky
            verticalArrangement = Arrangement.spacedBy(16.dp)  // Medzera 16dp medzi prvkami
        ) {

            // Karta pre denniky
            MenuCard(
                title = "Denník",
                description = "Záznam aktivít a poznámok",
                onClick = {
                    // Navigacia na obrazovku dennikov
                    navController.navigate(Screen.Diary.route)
                }
            )

            // Karta pre správu úľov
            MenuCard(
                title = "Správa úľov",
                description = "Pridávanie a úprava úľov",
                onClick = {
                    // Navigacia na obrazovku spravy ulov
                    navController.navigate(Screen.HiveManagement.route)
                }
            )

            // Karta pre nastavenia
            MenuCard(
                title = "Nastavenia",
                description = "Konfigurácia aplikácie",
                onClick = {
                    // Navigacia na obrazovku nastaveni
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
    }
}

/**
 * Karta predstavujuca jednu polozku v menu.
 *
 * @param title Nadpis karty
 * @param description Kratky popis funkcionality
 * @param onClick Lambda, ktora sa vykona pri kliknuti na kartu
 */
@Composable
private fun MenuCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    // Karta s efektom stlacenia
    Card(
        modifier = Modifier
            .fillMaxWidth()  // Sirka cez celu obrazovku
            .clickable { onClick() },  // Pri kliknuti vyvola onClick lambda
        shape = RoundedCornerShape(8.dp),  // Zaoblene rohy
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp  // Tien karty pre 3D efekt
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface  // Farba pozadia karty
        )
    ) {
        // Obsah karty
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)  // Vnútorný padding obsahu
        ) {
            // Stlpec s textami
            Column {
                // Nadpis karty
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,  // Velky tucny text
                    color = MaterialTheme.colorScheme.primary  // Zdoraznena farba pre nadpis
                )

                // Popisok karty
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,  // Stredne velky text
                    color = MaterialTheme.colorScheme.onSurfaceVariant,  // Menej vyrazna farba pre popis
                    modifier = Modifier.padding(top = 4.dp)  // Odsadenie od nadpisu
                )
            }
        }
    }
}
