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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.data.model.DiaryEntry
import com.beesense.ui.components.DiaryEntryCard
import com.beesense.ui.components.DiaryEntryDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen() {
    var diaryEntries by remember { mutableStateOf(
        listOf(
            DiaryEntry(1, "03-11-2025 21:30", "Krmenie", "Random Random"),
            DiaryEntry(2, "04-11-2025 08:15", "Prehliadka", "Random Random Random Random Random Random"),
            DiaryEntry(3, "04-11-2025 12:00", "Liečenie", "Random Random Random Random")
        )
    ) }

    var showDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Denník", fontSize = 20.sp) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEntry = null // nový záznam
                    showDialog = true
                }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pridať záznam"
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
            items(diaryEntries) { entry ->
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
            onSave = { newEntry ->
                diaryEntries = if (newEntry.id == 0) {
                    val newId = (diaryEntries.maxOfOrNull { it.id } ?: 0) + 1
                    diaryEntries + newEntry.copy(id = newId)
                } else {
                    diaryEntries.map { if (it.id == newEntry.id) newEntry else it }
                }
                showDialog = false
            }
        )
    }
}
