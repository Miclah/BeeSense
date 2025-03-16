package com.beesense.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.ui.components.HiveCard
import com.beesense.viewmodel.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(viewModel: HiveViewModel = viewModel()) {
    val hiveDataState = viewModel.hiveData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material3.Text("Prehľad úľov")
                },
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(hiveDataState.value) { hiveData ->
                HiveCard(hiveData = hiveData)
            }
        }
    }
}
