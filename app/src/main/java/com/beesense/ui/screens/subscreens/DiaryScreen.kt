package com.beesense.ui.screens.subscreens

/**
 * Obrazovka dennika zaznamov o uloch.
 *
 * Obsahuje funkcionalitu pre zobrazenie, vyhladavanie, pridavanie, upravu
 * a mazanie zaznamov o uloch. Umoznuje vcelarom zaznamenavat si
 * poznamky a aktivity tykajuce sa jednotlivych ulov.
 */

// Importy pre layouty a komponenty UI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

// Importy pre Lazy komponenty - efektivne zobrazenie zoznamov
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Importy pre ikony pouzivane v UI
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search

// Importy Material Design 3 komponentov
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

// Importy pre state management v Compose
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

// Importy pre umiestnenie a styling
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import pre ViewModel a navigaciu
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Import pre datovy model a komponenty aplikacie
import com.beesense.data.model.DiaryEntry
import com.beesense.ui.components.DiaryEntryCard
import com.beesense.ui.components.DiaryEntryDialog
import com.beesense.ui.navigation.Screen
import com.beesense.viewmodel.DiaryViewModel

/**
 * Hlavna composable funkcia pre obrazovku dennika.
 *
 * @param viewModel ViewModel obsahujuci biznis logiku a data pre tuto obrazovku
 * @param navController Kontroler pre navigaciu medzi obrazovkami
 */
@OptIn(ExperimentalMaterial3Api::class)  // Oznacuje pouzitie API, ktore este nie su stabilne
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = viewModel(),
    navController: NavController? = null
) {
    // Ziskanie dat z ViewModelu pomocou Flow a ich uchovanie v state
    val diaryEntries by viewModel.allDiaryEntries.collectAsState(initial = emptyList())

    // State pre ukladanie a uchovanie hodnot pri rekompozicii
    var searchQuery by rememberSaveable { mutableStateOf("") }  // Text vyhladavania
    var showDialog by rememberSaveable { mutableStateOf(false) }  // Flag zobrazenia dialogu
    var editingEntry by rememberSaveable { mutableStateOf<DiaryEntry?>(null) }  // Upravovany zaznam

    // Konverzia entit z databazy na objekty pouzivane v UI
    val mappedEntries = diaryEntries.filterNotNull().map { entity ->
        DiaryEntry(
            id = entity.id,
            type = entity.type,
            timestamp = entity.timestamp,
            note = entity.notes
        )
    }

    // Filtrovanie zaznamov podla vyhladavacieho dotazu
    val filteredEntries = mappedEntries.filter { entry ->
        val query = searchQuery.lowercase()
        // Vyhladavanie vo vsetkych relevantnych poliach
        entry.type.lowercase().contains(query)
                || entry.timestamp.lowercase().contains(query)
                || entry.note.lowercase().contains(query)
    }

    // Hlavny layout obrazovky
    Scaffold(
        // Horny panel s titulkom a vyhladavanim
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp) // Pravy padding pre lepsie zarovnanie
                    ) {
                        // Nazov sekcie
                        Text(
                            "Dennik",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis  // Orezanie textu ak je prilis dlhy
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Vylepsene vyhladavacie pole s lepsou viditelnostou textu
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Hľadať...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp
                                )
                            },
                            // Ikona lupy na zaciatku vyhladavacieho pola
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Hľadať",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            // Ikona vycistenia vyhladavania ak je nieco zadane
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
                                .weight(1f)  // Vyplni zvysny priestor v riadku
                                .heightIn(min = 46.dp, max = 50.dp),  // Fixna vyska pre konzistentny vzhlad
                            shape = MaterialTheme.shapes.medium,
                            singleLine = true,  // Vyhladavanie je jednoriadkove
                            // Nastavenie stylu textu
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            // Nastavenie farieb pre rozne stavy textoveho pola
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
                    }
                },
                // Navigacne tlacidlo spat, ak je k dispozicii NavController
                navigationIcon = {
                    if (navController != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Späť",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                // Farba pozadia horneho panela
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        // Plávajúce tlačítko pre pridanie nového záznamu
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Pri kliknuti vynulujeme upravovany zaznam a zobrazime dialog
                    editingEntry = null
                    showDialog = true
                },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,  // Tien pre lepsi vizualny efekt
                        shape = MaterialTheme.shapes.medium,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    .size(60.dp)  // Velkost tlacidla
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pridať záznam",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        // Zoznam zaznamov s plynulym skrolovanim
        LazyColumn(
            contentPadding = innerPadding,  // Aplikacia paddingu zo Scaffold
            verticalArrangement = Arrangement.spacedBy(8.dp),  // Medzery medzi polozkami
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Vykreslenie vsetkych filtrovanych zaznamov
            items(filteredEntries) { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { selectedEntry ->
                        // Nastavenie vybraneho zaznamu pre upravu a zobrazenie dialogu
                        editingEntry = selectedEntry
                        showDialog = true
                    }
                )
            }
        }
    }

    // Zobrazenie dialogu pre pridanie/upravu zaznamu ak je showDialog true
    if (showDialog) {
        DiaryEntryDialog(
            initialEntry = editingEntry,  // Ak je null, ide o novy zaznam
            onDismiss = { showDialog = false },  // Zatvorenie dialogu
            onDelete = { entryToDelete ->
                // Zmazanie zaznamu a zatvorenie dialogu
                viewModel.deleteDiaryEntry(entryToDelete)
                showDialog = false
            },
            onSave = { newEntry ->
                // Podmienene pridanie noveho alebo aktualizacia existujuceho zaznamu
                if (newEntry.id == 0) {
                    viewModel.addDiaryEntry(newEntry)
                } else {
                    viewModel.updateDiaryEntry(newEntry)
                }
                showDialog = false  // Zatvorenie dialogu po ulozeni
            }
        )
    }
}
