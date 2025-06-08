package com.beesense.ui.components

// Pomoc s AI

/**
 * Komponenta pre vyber datumu.
 *
 * Tato komponenta zobrazuje kartu, ktora po kliknuti otvori
 * standardny Android vyberac datumov. Umoznuje nastavit minimalny datum
 * a poskytuje spatne callback s vybranym datumom.
 */

// Importy pre interakciu a layout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

// Import pre ikonu kalendara
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange

// Importy Material Design komponentov
import androidx.compose.material3.*

// Importy pre Compose funkcionalitu
import androidx.compose.runtime.*

// Importy pre vizualne nastavenia a pristup ku kontextu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Import pre aktivitu fragmentu potrebnu pre dialog vyberaca datumu
import androidx.fragment.app.FragmentActivity

// Import pre Material Date Picker komponentu z Google Material kniznice
import com.google.android.material.datepicker.MaterialDatePicker

// Importy pre pracu s datumami a formatovanim
import java.text.SimpleDateFormat
import java.util.*

/**
 * Komponenta pre vyberanie datumu s Material Design vzhladom.
 *
 * @param label Popisok zobrazeny nad vybranym datumom
 * @param date Aktualny datum alebo null ak nie je nic vybrate
 * @param onDateSelected Callback funkcia volana ked pouzivatel vyberie datum
 * @param modifier Volitelne modifiery pre upravu kompozicie
 * @param minDate Volitelny minimalny datum, ktory moze pouzivatel vybrat
 */
@Composable
fun DateRangePicker(
    label: String,
    date: Date?,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier,
    minDate: Date? = null
) {
    // Ziskanie aktualneho kontextu potrebneho pre dialog
    val context = LocalContext.current

    // Format pre zobrazenie datumu v slovenskom formate (den.mesiac.rok)
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // Vytvorenie karty s moznostou kliknutia
    OutlinedCard(
        modifier = modifier.clickable {
            // Vytvorenie vyberaca datumu zo standardnej Android kniznice
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(label)  // Nastavenie nadpisu dialogu

            // Nastavenie aktualneho datumu ako predvolenej hodnoty, ak existuje
            date?.let {
                val milliseconds = it.time
                builder.setSelection(milliseconds)
            }

            // Nastavenie minimalneho datumu, ak je poskytnuty
            minDate?.let {
                val milliseconds = it.time
                builder.setCalendarConstraints(
                    com.google.android.material.datepicker.CalendarConstraints.Builder()
                        .setStart(milliseconds)  // Nastavenie minimalneho datumu
                        .build()
                )
            }

            // Vytvorenie finalneho vyberaca datumu
            val datePicker = builder.build()

            // Nastavenie listenera pre tlacidlo potvrdenia (OK)
            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = Date(selection)  // Konverzia na objekt Date
                onDateSelected(selectedDate)        // Volanie callback-u s vybranym datumom
            }

            // Zobrazenie dialogu vyberaca datumu s pouzitim Fragment Managera
            datePicker.show((context as FragmentActivity).supportFragmentManager, "DATE_PICKER")
        }
    ) {
        // Obsah karty - riadok s popiskom, datumom a ikonou kalendara
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,      // Zarovnanie na stred vertikalne
            horizontalArrangement = Arrangement.SpaceBetween     // Rozdelenie prvkov na zaciatku a konci
        ) {
            // Stlpec s textami (popis a aktualny datum)
            Column(
                modifier = Modifier.weight(1f)  // Zaberanie vsetkeho dostupneho miesta
            ) {
                // Popisok nad vybranym datumom
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Medzera medzi textami
                Spacer(modifier = Modifier.height(4.dp))

                // Zobrazenie vybraneho datumu alebo vyzvy na vyber
                Text(
                    text = if (date != null) dateFormatter.format(date) else "Vyberte dátum",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Ikona kalendara na pravej strane
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Výber dátumu"  // Popis pre citace obrazovky
            )
        }
    }
}
