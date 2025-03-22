package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.data.model.DiaryEntry
import com.beesense.ui.components.DiaryEntryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen() {
    // test data
    val diaryEntries = listOf(
        DiaryEntry(1, "03-11-2025 21:30", "Krmenie", "Random Random"),
        DiaryEntry(2, "04-11-2025 08:15", "Prehliadka", "Random Random Random Random Random Random"),
        DiaryEntry(3, "04-11-2025 12:00", "Liečenie", "Random Random Random Random")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Denník", fontSize = 20.sp) }
            )
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
                DiaryEntryCard(entry)
            }
        }
    }
}