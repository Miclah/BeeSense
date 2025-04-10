package com.beesense.ui.screens.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.ui.components.HiveCheckboxItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveEditorScreen(
    isEditing: Boolean = false
) {
    var hiveId by remember { mutableStateOf("1") }
    var shortDescription by remember { mutableStateOf("") }

    var leftArmWeight by remember { mutableStateOf(false) }
    var rightArmWeight by remember { mutableStateOf(false) }
    var totalWeight by remember { mutableStateOf(false) }
    var sensorTemperature by remember { mutableStateOf(false) }
    var outdoorTemperature by remember { mutableStateOf(false) }
    var humidity by remember { mutableStateOf(false) }
    var airPressure by remember { mutableStateOf(false) }

    var showInApp by remember { mutableStateOf(false) }
    var hiveDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editovať Úľ" else "Pridať Úľ",
                        fontSize = 20.sp
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ID úľu: $hiveId",
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp),
                    fontSize = 25.sp,
                    color = Color.Black
                )
                OutlinedTextField(
                    value = shortDescription,
                    onValueChange = { shortDescription = it },
                    label = { Text("Názov úľu") },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            HiveCheckboxItem(
                label = "Hmotnosť ľavého ramena",
                checked = leftArmWeight,
                onCheckedChange = { leftArmWeight = it }
            )
            HiveCheckboxItem(
                label = "Hmotnosť pravého ramena",
                checked = rightArmWeight,
                onCheckedChange = { rightArmWeight = it }
            )
            HiveCheckboxItem(
                label = "Celkova hmotnosť",
                checked = totalWeight,
                onCheckedChange = { totalWeight = it }
            )
            HiveCheckboxItem(
                label = "Teplota snímača",
                checked = sensorTemperature,
                onCheckedChange = { sensorTemperature = it }
            )
            HiveCheckboxItem(
                label = "Teplota vonkajšia",
                checked = outdoorTemperature,
                onCheckedChange = { outdoorTemperature = it }
            )
            HiveCheckboxItem(
                label = "Vlhkosť",
                checked = humidity,
                onCheckedChange = { humidity = it }
            )
            HiveCheckboxItem(
                label = "Tlak vzduchu",
                checked = airPressure,
                onCheckedChange = { airPressure = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            HiveCheckboxItem(
                label = "Zobraziť v aplikácii",
                checked = airPressure,
                onCheckedChange = { showInApp = it }
            )

            OutlinedTextField(
                value = hiveDescription,
                onValueChange = { hiveDescription = it },
                label = { Text("Popis úľa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // TODO: Save button logic
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(0.5f), 
                ) {
                    Text(
                        "Uložiť",
                        fontSize = 25.sp 
                    )
                }

                if (isEditing) {
                    Button(
                        onClick = {
                            // TODO: Editing logic
                        },
                        modifier = Modifier
                            .height(60.dp) 
                            .fillMaxWidth(0.5f), 
                    ) {
                        Text(
                            "Vymazať",
                            fontSize = 25.sp 
                        )
                    }
                }

            }
        }
    }
}
