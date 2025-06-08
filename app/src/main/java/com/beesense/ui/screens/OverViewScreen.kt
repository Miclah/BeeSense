package com.beesense.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.ui.components.HiveCard
import com.beesense.viewmodel.OverviewViewModel
import com.beesense.data.model.HiveDataWithTrend

/**
 * Hlavna obrazovka aplikacie zobrazujuca prehlad vsetkych ulov.
 *
 * Tato obrazovka prezentuje vsetky dostupne uly vo forme karticiek v mriezke.
 * Kazda karticka obsahuje zakladne informacie o ule (teplota, vaha, atd.)
 * a indikuje trendy hodnot pomocou farebneho zvyraznenia a ikon. Uzivatel moze
 * aktualizovat data pomocou tlacidla obnovit a filtrovat uly pomocou vyhladavania.
 *
 * @param viewModel ViewModel poskytujuci data a biznis logiku pre tuto obrazovku
 */
@OptIn(ExperimentalMaterial3Api::class) // Oznacenie, ze pouzivame experimentalne API z Material3
@Composable
fun OverviewScreen(viewModel: OverviewViewModel = viewModel()) {
    // Ziskame stavy z viewModelu a sledujeme ich zmeny s respektovanim lifecycle
    val hiveDataList by viewModel.hiveDataWithTrends.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Lokalny stav pre vyhladavaci vyraz, ktory prezije aj rekompoziciu obrazovky
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Lokalny stav pre filtrovane uly
    var filteredHives by remember { mutableStateOf<List<HiveDataWithTrend>>(emptyList()) }

    // Lokalna premenna pre ulozenie oblubenych ulov - zmenene na Int, kedze HiveData.id je typu Int
    var favoriteHives by remember { mutableStateOf(setOf<Int>()) }

    // Pri zmene zoznamu ulov aktualizujeme filtrovany zoznam
    filteredHives = if (searchQuery.isEmpty()) {
        hiveDataList
    } else {
        hiveDataList.filter { hive ->
            hive.current.id.toString().contains(searchQuery, ignoreCase = true) ||
            (hive.displayName.isNotEmpty() && hive.displayName.contains(searchQuery, ignoreCase = true))
        }
    }

    // Hlavny layout obrazovky s podporou pre floating action button a top app bar
    Scaffold(
        // Horny panel s nazvom aplikacie, vyhladavanim a refresh tlacidlom
        topBar = {
            TopAppBar(
                title = {
                    // Vylepšený layout horného panelu - podobne ako v DiaryScreen
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp) // Padding na pravej strane
                    ) {
                        // Názov aplikácie
                        Text(
                            "BeeSense",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Vylepšené vyhľadávacie pole s lepšou viditeľnosťou textu
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Hľadať úľ...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Hľadať",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Vymazať",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp, max = 50.dp),
                            shape = MaterialTheme.shapes.medium,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            )
                        )

                        // Tlačidlo na obnovenie dát
                        IconButton(
                            onClick = { viewModel.refreshData() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Obnoviť dáta",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { paddingValues ->
        // Hlavny obsah obrazovky s paddingom podla Scaffoldu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Zobrazenie indikátora načítavania, ak sa načítavajú dáta
            if (isLoading) {
                // Centrovany spinner pre nacitavanie
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                // Zobrazenie mriezky ulov, ak mame data
                // Použitie 2 stĺpcov namiesto adaptívnej veľkosti pre lepšie zobrazenie kariet
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),  // Presne dva stĺpce pre štvorcové karty
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Vytvorenie karticiek pre kazdy ul
                    items(filteredHives) { hiveDataWithTrend ->
                        HiveCard(
                            hiveData = hiveDataWithTrend,
                            isFavorite = hiveDataWithTrend.current.id in favoriteHives,
                            onFavoriteToggle = { hiveId ->
                                favoriteHives = if (hiveId in favoriteHives) {
                                    favoriteHives - hiveId
                                } else {
                                    favoriteHives + hiveId
                                }
                            }
                        )
                    }
                }
            }

            // Zobrazenie chybovej hlasky, ak nastala chyba
            error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ) {
                    Text(text = errorMsg)
                }
            }
        }
    }
}
