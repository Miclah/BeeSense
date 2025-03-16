package com.beesense.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun FavoriteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Odstrániť z obľúbených?") },
        text = { Text("Naozaj chcete odstrániť tento úľ z obľúbených?") },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Áno")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Nie")
            }
        }
    )
}
