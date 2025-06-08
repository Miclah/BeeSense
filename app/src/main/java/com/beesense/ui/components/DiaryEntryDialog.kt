package com.beesense.ui.components

/**
 * Dialogove okno pre pridanie alebo upravu zaznamu v denniku.
 *
 * Tato komponenta umoznuje pouzivatelovi pridat novy zaznam do dennika,
 * alebo upravit existujuci zaznam. Obsahuje formulare pre typ zaznamu,
 * cas/datum a poznamku, a tiez tlacidla pre ulozenie, zrusenie a vymazanie.
 */

// Importy pre vizualne efekty a layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults

// Importy pre Compose funkcionalitu a stav
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Importy pre vizualne nastavenia
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Import datoveho modelu a utilit pre pracu s datumom
import com.beesense.data.model.DiaryEntry
import java.util.Calendar
import java.util.Locale

/**
 * Dialog pre vytvorenie alebo upravu zaznamu dennika.
 *
 * @param initialEntry Povodny zaznam na upravu alebo null pre novy zaznam
 * @param onDismiss Callback volany pri zatvoreni dialogu bez ulozenia
 * @param onSave Callback volany pri ulozeni zaznamu
 * @param onDelete Volitelny callback volany pri zmazani zaznamu
 */
@OptIn(ExperimentalMaterial3Api::class)  // Oznacenie pouzitia experimentalneho API
@Composable
fun DiaryEntryDialog(
    initialEntry: DiaryEntry?,
    onDismiss: () -> Unit,
    onSave: (DiaryEntry) -> Unit,
    onDelete: ((DiaryEntry) -> Unit)? = null
) {
    // Definovanie moznych typov zaznamov
    val types = listOf("Kŕmenie", "Liečenie", "Bratie medu", "Prehliadka", "Iné")

    // State pre uchovavanie hodnot formularovych poli
    var selectedType by remember { mutableStateOf(initialEntry?.type ?: types.first()) }
    var time by remember { mutableStateOf(initialEntry?.timestamp ?: getCurrentTime()) }
    var note by remember { mutableStateOf(initialEntry?.note ?: "") }
    var expanded by remember { mutableStateOf(false) }  // Pre stav rozbaleneho dropdownu

    // Sledovanie zmien oproti povodnemu zaznamu
    val hasChanged = initialEntry == null
            || selectedType != initialEntry.type
            || time != initialEntry.timestamp
            || note != initialEntry.note

    // Hlavna struktura dialogu
    Dialog(onDismissRequest = onDismiss) {
        // Karta s formularom
        Card(
            shape = RoundedCornerShape(24.dp),  // Vyrazne zaoblene rohy
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,  // Farba pozadia karty
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),  // Tien pre vizualny efekt
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                // Hlavicka dialogu s nadpisom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        // Rozlisenie pre novy vs. existujuci zaznam
                        text = if (initialEntry == null) "Nový záznam" else "Úprava záznamu",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center  // Centrovanie textu
                    )
                }

                // Oddelujuca ciara
                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Obsah formulara
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)  // Medzery medzi polami
                ) {
                    // Vyberovy zoznam s typom ukonov
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        // Textove pole s moznostou rozbalenia
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},  // Prazdny handler, hodnota sa meni iba vyberom
                            readOnly = true,     // Nie je mozne priamo pisat
                            label = {
                                Text(
                                    "Typ úkonu",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                // Ikona sipky dole alebo hore podla stavu rozbalenia
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            // Nastavenie farieb pre textove pole
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)  // Ukotvenie menu k tomuto prvku
                        )
                        // Rozbalovaci zoznam typov zaznamu
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },  // Zatvorenie po kliknuti mimo
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            // Vykreslenie vsetkych dostupnych typov
                            types.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            type,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedType = type  // Nastavenie vybranej hodnoty
                                        expanded = false     // Zatvorenie menu
                                    }
                                )
                            }
                        }
                    }

                    // Pole pre zadanie datumu a casu
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = {
                            Text(
                                "Čas a dátum",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        placeholder = {
                            Text(
                                "HH:mm d.M.yyyy",  // Format datumu a casu
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        // Rovnake nastavenie farieb ako pre predchadzajuce pole
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Viacviadkove pole pre poznamku
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = {
                            Text(
                                "Poznámka",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        // Rovnake nastavenie farieb ako vyssie
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)  // Pevna vyska pre viacriadkovy text
                    )

                    Spacer(modifier = Modifier.height(8.dp))  // Medzera pred tlacidlami

                    // Riadok s akciami (tlacidlami)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)  // Medzera medzi tlacidlami
                    ) {
                        // Tlacidlo DELETE sa zobrazi iba pri uprave existujuceho zaznamu
                        if (initialEntry != null) {
                            Button(
                                onClick = { onDelete?.invoke(initialEntry) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)  // Vyplna polovicu sirky
                            ) {
                                Text(
                                    "VYMAZAŤ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        } else {
                            // Tlacidlo ZRUSIT sa zobrazi iba pri vytvarani noveho zaznamu
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)  // Vyplna polovicu sirky
                            ) {
                                Text(
                                    "ZRUŠIŤ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        // Tlacidlo ULOZIT - vzdy pritomne
                        Button(
                            onClick = {
                                // Ulozi iba ak doslo k zmene
                                if (hasChanged) {
                                    // Vytvorenie objektu so zmenami alebo uplne novy objekt
                                    val entry = initialEntry?.copy(
                                        type = selectedType,
                                        timestamp = time,
                                        note = note
                                    ) ?: DiaryEntry(0, selectedType, time, note)
                                    onSave(entry)  // Zavolanie callback-u pre ulozenie
                                } else {
                                    onDismiss()    // Zavrieme bez ulozenia ak neboli zmeny
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)  // Vyplna polovicu sirky
                        ) {
                            Text(
                                "ULOŽIŤ",
                                fontSize = 13.sp,
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

/**
 * Pomocna funkcia pre ziskanie aktualneho casu naformatovaneho pre zobrazenie.
 *
 * @return String s aktualnym casom a datumom vo formate "HH:MM DD.MM.YYYY"
 */
private fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    return String.format(
        Locale.getDefault(),
        "%02d:%02d %02d.%02d.%04d",
        calendar.get(Calendar.HOUR_OF_DAY),  // Hodiny (0-23)
        calendar.get(Calendar.MINUTE),       // Minuty
        calendar.get(Calendar.DAY_OF_MONTH), // Den v mesiaci
        calendar.get(Calendar.MONTH) + 1,    // Mesiac (0-11, preto +1)
        calendar.get(Calendar.YEAR)          // Rok
    )
}
