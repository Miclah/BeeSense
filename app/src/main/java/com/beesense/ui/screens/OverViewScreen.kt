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
    var favoriteHives by rememberSaveable { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prehľad úľov",
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("ID úľa", fontSize = 14.sp) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .width(180.dp)
                                .height(40.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            )
        }
    ) { padding ->
        val filteredHives = hiveDataState.value
            .filter { hive -> searchQuery.isBlank() || hive.id.toString().contains(searchQuery) }
            .sortedByDescending { it.id in favoriteHives }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            modifier = Modifier.padding(padding)
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

