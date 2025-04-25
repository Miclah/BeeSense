package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.data.model.DiaryEntry
import com.beesense.ui.components.DiaryEntryCard
import com.beesense.ui.components.DiaryEntryDialog
import com.beesense.viewmodel.DiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(viewModel: DiaryViewModel = viewModel()) {
    val diaryEntries by viewModel.allDiaryEntries.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    val mappedEntries = diaryEntries.filterNotNull().map { entity ->
        DiaryEntry(
            id = entity.id,
            type = entity.type,
            timestamp = entity.timestamp,
            note = entity.notes
        )
    }

    val filteredEntries = mappedEntries.filter { entry ->
        val query = searchQuery.lowercase()
        entry.type.lowercase().contains(query)
                || entry.timestamp.lowercase().contains(query)
                || entry.note.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Dennik", fontSize = 26.sp)
                },
                actions = {
                    SearchBar(
                        inputField = {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Hladat...") },
                                singleLine = true
                            )
                        },
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEntry = null
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pridat zaznam"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(filteredEntries) { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { selectedEntry ->
                        editingEntry = selectedEntry
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog) {
        DiaryEntryDialog(
            initialEntry = editingEntry,
            onDismiss = { showDialog = false },
            onDelete = { entryToDelete ->
                viewModel.deleteDiaryEntry(entryToDelete)
                showDialog = false
            },
            onSave = { newEntry ->
                if (newEntry.id == 0) {
                    viewModel.addDiaryEntry(newEntry)
                } else {
                    viewModel.updateDiaryEntry(newEntry)
                }
                showDialog = false
            }
        )
    }
}
