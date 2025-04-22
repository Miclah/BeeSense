package com.beesense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.ui.components.HiveCard
import com.beesense.viewmodel.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(viewModel: HiveViewModel = viewModel()) {
    val hiveDataState = viewModel.hiveData.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var favoriteHives by rememberSaveable { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Prehlad", fontSize = 26.sp)
                },
                actions = {
                    SearchBar(
                        inputField = {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Hladat ID ula...") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                            )
                        },
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },
                        modifier = Modifier.padding(end = 8.dp),
                    ) {}
                }
            )
        }
    ) { padding ->
        val filteredHives = hiveDataState.value
            .filter { hive -> searchQuery.isBlank() || hive.id.toString().contains(searchQuery) }
            .sortedByDescending { it.id in favoriteHives }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            items(filteredHives) { hiveData ->
                HiveCard(
                    hiveData = hiveData,
                    isFavorite = hiveData.id in favoriteHives,
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
}

