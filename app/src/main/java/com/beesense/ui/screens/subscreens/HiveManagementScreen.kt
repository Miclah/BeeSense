package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.beesense.data.repository.HiveConfig
import com.beesense.ui.components.DeleteConfirmationDialog
import com.beesense.ui.navigation.Screen
import com.beesense.ui.viewmodels.HiveManagementViewModel

/**
 * Obrazovka pre spravu a zoznam vcelich ulov v aplikacii
 *
 * @param navController Controller pre navigaciu medzi obrazovkami
 * @param viewModel ViewModel obsahujuci logiku pre spravu ulov
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveManagementScreen(
    navController: NavController,
    viewModel: HiveManagementViewModel
) {
    // Ziskanie zoznamu ulov z viewModelu
    val hives by viewModel.hivesState.collectAsState()

    // Stavy pre dialog potvrdenia vymazania ula
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var hiveToDelete by remember { mutableStateOf<HiveConfig?>(null) }

    // Zobrazenie dialogu pre potvrdenie vymazania ula
    if (showDeleteConfirmation && hiveToDelete != null) {
        DeleteConfirmationDialog(
            title = "Odstrániť úľ",
            message = "Naozaj chcete odstrániť úľ '${hiveToDelete?.displayName}'?",
            onConfirm = {
                hiveToDelete?.let {
                    viewModel.deleteHive(it) {
                        // Spetne volanie po uspesnom vymazani - UI sa aktualizuje automaticky
                    }
                }
                showDeleteConfirmation = false
                hiveToDelete = null
            },
            onDismiss = {
                showDeleteConfirmation = false
                hiveToDelete = null
            }
        )
    }

    Scaffold(
        topBar = {
            // Horny panel aplikacie s nazvom a tlacidlom spat
            TopAppBar(
                title = { Text("Správa úľov") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Späť"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        // Plávajúce tlačidlo pre pridanie nového úľa
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.HiveEditor.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pridať úľ"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Zobraziť správu, ak nie sú nakonfigurované žiadne úle
            if (hives.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Žiadne úle nie sú nakonfigurované.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Pridajte nový úľ pomocou tlačidla +",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Zobrazenie zoznamu existujúcich úľov pomocou LazyColumn
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hives) { hive ->
                        HiveConfigCard(
                            hiveConfig = hive,
                            onCardClick = {
                                // Navigácia na obrazovku úpravy úľa s ID konkrétneho úľa
                                navController.navigate("${Screen.HiveEditor.route}?hiveId=${hive.id}")
                            },
                            onDeleteClick = {
                                // Nastavenie údajov pre potvrdzovacie dialógové okno
                                hiveToDelete = hive
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Komponenta reprezentujuca kartu s informaciami o ule
 *
 * @param hiveConfig Konfiguracia ula na zobrazenie
 * @param onCardClick Callback volany po kliknuti na kartu
 * @param onDeleteClick Callback volany po kliknuti na tlacidlo vymazania
 */
@Composable
fun HiveConfigCard(
    hiveConfig: HiveConfig,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Zobrazenie ID úľa v kruhu
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${hiveConfig.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            // Názov úľa a názov tabuľky
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = hiveConfig.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Tabuľka: ${hiveConfig.tableName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tlačidlo pre vymazanie úľa
            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Odstrániť",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
