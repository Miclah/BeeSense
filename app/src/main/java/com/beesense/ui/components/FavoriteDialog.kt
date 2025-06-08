package com.beesense.ui.components

/**
 * Komponenta dialogu pre potvrdenie odstranenia oblubenej polozky.
 *
 * Tento dialog poskytuje stylovy potvrzovaci dialog, ktory sa zobrazi
 * ked chce pouzivatel odstranit ul zo zoznamu oblubenych poloziek.
 */

// Importy pre vizualne efekty a layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

// Import pre zaoblene rohy
import androidx.compose.foundation.shape.RoundedCornerShape

// Importy Material Design komponentov
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

// Importy pre Compose funkcionalitu
import androidx.compose.runtime.Composable

// Importy pre vizualne nastavenia
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import pre dialogove okno
import androidx.compose.ui.window.Dialog

/**
 * Dialog pre potvrdenie odstranenia ula z oblubenych poloziek.
 *
 * Tento dialog sa zobrazi, ked pouzivatel chce odstranit ul z oblubenych.
 * Obsahuje potvrdzovaciu otazku a dve tlacidla - ano (potvrdenie) a nie (zrusenie).
 *
 * @param onConfirm Funkcia volana pri potvrdeni odstranenia
 * @param onDismiss Funkcia volana pri zruseni dialogu alebo odmietnutia odstranenia
 */
@Composable
fun FavoriteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // Zakladne dialogove okno, ktore sa zatvori pri kliknuti mimo neho
    Dialog(onDismissRequest = { onDismiss() }) {
        // Hlavna karta s tieňom a zaoblenými rohmi
        Card(
            // Definicia tvaru karty - vyrazne zaoblene rohy
            shape = RoundedCornerShape(24.dp),
            // Nastavenie farieb karty podla farebnej schemy
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            // Nastavenie tiena pre lepsi vizualny efekt
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            // Modifiery pre sirku a padding
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Vertikalne usporiadanie obsahu karty
            Column {
                // Farebne zahlavie dialogu s otazkou
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    // Nadpis dialogu
                    Text(
                        text = "Odstrániť z obľúbených?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Oddelovacia ciara medzi zahlavim a telom dialogu
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Obsah dialogu s potvrdzovacou otazkou a tlacidlami
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Text s potvrdzovacou otazkou
                    Text(
                        text = "Naozaj chcete odstrániť tento úľ z obľúbených?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center  // Zarovnanie textu na stred
                    )

                    // Medzera pred tlacidlami
                    Spacer(modifier = Modifier.height(24.dp))

                    // Riadok s tlacidlami pre potvrdenie a zrusenie
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Tlacidlo "NIE" pre zrusenie akcie
                        Button(
                            onClick = { onDismiss() },
                            // Farby tlacidla vo vizualne menej vyraznom style
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            // Zaoblene rohy pre konsistentny vzhlad
                            shape = RoundedCornerShape(12.dp),
                            // Zaberanie polovice dostupnej sirky
                            modifier = Modifier.weight(1f)
                        ) {
                            // Text tlacidla
                            Text(
                                "NIE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        // Medzera medzi tlacidlami
                        Spacer(modifier = Modifier.width(16.dp))

                        // Tlacidlo "ANO" pre potvrdenie odstranenia z oblubenych
                        Button(
                            onClick = { onConfirm() },
                            // Vyraznejsie farby pre tlacidlo primarnej akcie
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            // Zaoblene rohy pre konsistentny vzhlad
                            shape = RoundedCornerShape(12.dp),
                            // Zaberanie polovice dostupnej sirky
                            modifier = Modifier.weight(1f)
                        ) {
                            // Text tlacidla
                            Text(
                                "ÁNO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
