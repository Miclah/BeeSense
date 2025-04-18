package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.ui.theme.BeeSenseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(false) }

    var weightThreshold by remember { mutableStateOf("3") }
    var inactivityThresholdHours by remember { mutableStateOf("5") }

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
            androidx.compose.material3.OutlinedTextField(
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
            androidx.compose.material3.OutlinedTextField(
                value = inactivityThresholdHours,
                onValueChange = { inactivityThresholdHours = it.filter { ch -> ch.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Napr. 5") },
                singleLine = true
            )

        }
    }
}
