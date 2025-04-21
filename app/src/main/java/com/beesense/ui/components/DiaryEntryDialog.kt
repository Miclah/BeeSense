package com.beesense.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beesense.data.model.DiaryEntry
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryDialog(
    initialEntry: DiaryEntry?,
    onDismiss: () -> Unit,
    onSave: (DiaryEntry) -> Unit,
    onDelete: ((DiaryEntry) -> Unit)? = null
) {
    val types = listOf("Kŕmenie", "Liečenie", "Bratie medu", "Prehliadka", "Iné")

    var selectedType by remember { mutableStateOf(initialEntry?.type ?: types.first()) }
    var time by remember { mutableStateOf(initialEntry?.timestamp ?: getCurrentTime()) }
    var note by remember { mutableStateOf(initialEntry?.note ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val hasChanged = initialEntry == null
            || selectedType != initialEntry.type
            || time != initialEntry.timestamp
            || note != initialEntry.note

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialEntry == null) "Nový záznam" else "Úprava záznamu")
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        label = { Text("Typ úkonu") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Čas a dátum") },
                    placeholder = { Text("HH:mm d.M.yyyy") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Poznámka") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (initialEntry != null) {
                    Button(onClick = { onDelete?.invoke(initialEntry) }) {
                        Text("Vymazať")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(onClick = {
                    if (hasChanged) {
                        val entry = initialEntry?.copy(
                            type = selectedType,
                            timestamp = time,
                            note = note
                        ) ?: DiaryEntry(0, selectedType, time, note)
                        onSave(entry)
                    } else {
                        onDismiss()
                    }
                }) {
                    Text("Uložiť")
                }
            }
        },
        dismissButton = {}
    )
}

private fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    return String.format(
        Locale.getDefault(),
        "%02d:%02d %02d.%02d.%04d",
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )
}
