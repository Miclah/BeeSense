package com.beesense.ui.components

/**
 * Komponenta pre dialogove okno potvrdenia zmazania.
 *
 * Toto dialogove okno sa pouziva na potvrdenie operacie zmazania roznych typov dat.
 * Obsahuje nadpis, spravu a dve tlacidla - na potvrdenie a zrusenie akcie.
 */

// Importy pre layout a komponenty
import androidx.compose.foundation.layout.padding

// Importy pre Material Design komponenty
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

// Import pre Compose funkcionalitu
import androidx.compose.runtime.Composable

// Importy pre vizualne nastavenia
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialogove okno pre potvrdenie mazania.
 *
 * @param title Nadpis dialogu s otazkou alebo upozornenim
 * @param message Sprava s detailmi o mazani
 * @param onConfirm Funkcia volana pri potvrdeni zmazania
 * @param onDismiss Funkcia volana pri zruseni dialogu
 */
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // Standardne dialogove okno Material Design
    AlertDialog(
        // Zatvori dialog pri kliknuti mimo dialogu
        onDismissRequest = onDismiss,

        // Nadpis dialogu
        title = { Text(text = title) },

        // Hlavny text dialogu s informaciami
        text = { Text(text = message) },

        // Tlacidlo na potvrdenie akcie
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Potvrdiť")
            }
        },

        // Tlacidlo na zrusenie akcie
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Zrušiť")
            }
        }
    )
}
