package com.beesense.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.app.data.model.HiveData
import com.beesense.app.ui.components.HiveCard
import com.beesense.app.viewmodel.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(viewModel: HiveViewModel = viewModel()) {
    val hivesState = viewModel.hives.collectAsState()
    var selectedHive by remember { mutableStateOf<HiveData?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { androidx.compose.material3.Text("Prehľad úľov") }) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(hivesState.value) { hive ->
                HiveCard()
            }
        }
    }
}