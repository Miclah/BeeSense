package com.beesense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beesense.data.model.DiaryEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryDialog(
    initialEntry: DiaryEntry?,
    onDismiss: () -> Unit,
    onSave: (DiaryEntry) -> Unit
) {
    var note by remember { mutableStateOf(initialEntry?.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Záznam denníka") },
        text = {
            Column {
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Poznámka") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newEntry = initialEntry?.copy(note = note) ?: DiaryEntry(0, "Čas", "Typ", note)
                onSave(newEntry)
            }) {
                Text("Uložiť")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Zrušiť")
            }
        }
    )
}
