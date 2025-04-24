package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.beesense.ui.theme.BeeSenseTheme
import com.beesense.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beesense.data.db.entities.SettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val dbSettings by viewModel.settings.collectAsStateWithLifecycle()
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(false) }

    var weightThreshold by remember { mutableStateOf("3") }
    var inactivityThresholdHours by remember { mutableStateOf("5") }

    LaunchedEffect(dbSettings) {
        isDarkMode = dbSettings.isDarkMode
        areNotificationsEnabled = dbSettings.areNotificationsEnabled
        weightThreshold = dbSettings.weightThresholdKg.toString()
        inactivityThresholdHours = dbSettings.inactivityThresholdHours.toString()
    }

    BeeSenseTheme(darkTheme = isDarkMode) {
        Column(modifier = Modifier.padding(16.dp)) {
            TopAppBar(
                title = { Text("Nastavenia") },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Režim témy",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Svetlý/Tmavý režim", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isDarkMode = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Nastavenia notifikácií",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Povoliť notifikácie", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                Switch(
                    checked = areNotificationsEnabled,
                    onCheckedChange = { areNotificationsEnabled = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Text(
                text = "Limit zmeny hmotnosti (kg)",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = weightThreshold,
                onValueChange = { weightThreshold = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 3.0") },
                singleLine = true
            )


            Text(
                text = "Interval opakovania notifikácie (hodiny)",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = inactivityThresholdHours,
                onValueChange = { inactivityThresholdHours = it.filter { ch -> ch.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 5") },
                singleLine = true
            )
            Button(
                onClick = {
                    val wt = weightThreshold.toFloatOrNull() ?: dbSettings.weightThresholdKg
                    val ih = inactivityThresholdHours.toIntOrNull() ?: dbSettings.inactivityThresholdHours
                    viewModel.save(
                        SettingsEntity(
                            isDarkMode = isDarkMode,
                            areNotificationsEnabled = areNotificationsEnabled,
                            weightThresholdKg = wt,
                            inactivityThresholdHours = ih
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Uložiť", fontSize = 18.sp)
            }
        }
    }
}
