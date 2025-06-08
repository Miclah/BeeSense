package com.beesense.ui.components

/**
 * Komponenta karty zaznamu v denniku.
 *
 * Tato komponenta vytvara vizualnu reprezentaciu zaznamu dennika vo forme karty.
 * Zobrazuje typ zaznamu, casovu znacku a text poznamky s moznostou kliknutia
 * pre detailnejsie zobrazenie alebo upravu.
 */

// Importy pre vizualne efekty a interakcie
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

// Import pre zaoblene rohy a ine tvary
import androidx.compose.foundation.shape.RoundedCornerShape

// Importy Material Design komponentov
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import datoveho modelu
import com.beesense.data.model.DiaryEntry

/**
 * Karta zaznamov dennika.
 *
 * @param entry Zaznam dennika, ktory ma byt zobrazeny
 * @param onClick Funkcia volana pri kliknuti na kartu, umoznuje upravu zaznamu
 */
@Composable
fun DiaryEntryCard(entry: DiaryEntry, onClick: (DiaryEntry) -> Unit) {
    // Hlavna karta s tienom a interaktivnym efektom pri kliknuti
    Card(
        modifier = Modifier
            .fillMaxWidth()  // Vyplni celu dostupnu sirku
            .padding(horizontal = 8.dp, vertical = 6.dp)  // Vonkajsi padding karty
            .clickable { onClick(entry) },  // Klikatelna oblast s callbackom
        shape = RoundedCornerShape(16.dp),  // Zaoblene rohy karty
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),  // Tien pre vizualnu hierarchiu
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,  // Farba pozadia karty
        )
    ) {
        Column {
            // Hlavicka s typom zaznamu a farebnym pozadim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))  // Farebne pozadie s priehladnostou
                    .padding(vertical = 10.dp, horizontal = 16.dp)  // Vnutorny padding hlavicky
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,  // Vertikalne zarovnanie na stred
                    horizontalArrangement = Arrangement.SpaceBetween  // Rozlozenie prvkov na opacne strany
                ) {
                    // Typ zaznamu (napr. "Kontrola ula")
                    Text(
                        text = entry.type,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold  // Tucne pismo pre zvyraznenie
                    )

                    // Casovy udaj zaznamu (datum)
                    Text(
                        text = entry.timestamp,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),  // Mierne priehladna farba
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Oddelovacia ciara medzi hlavickou a obsahom
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Obsah poznamky s vhodnym paddingom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)  // Vnutorny padding pre obsah
            ) {
                // Text poznamky s obmedzenim na 3 riadky
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,  // Orezanie textu ak je prilis dlhy
                    maxLines = 3  // Maximalne 3 riadky, potom nasleduje orezanie
                )
            }
        }
    }
}
