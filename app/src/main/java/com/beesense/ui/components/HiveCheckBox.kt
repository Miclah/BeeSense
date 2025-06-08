package com.beesense.ui.components

/**
 * Komponenta zaznacovacieho policka (checkboxu) pre uly.
 *
 * Tato komponenta vytvara riadok s popisom ula a zaznacovacim polickom,
 * ktore umoznuje vyber alebo zrusenie vyberu daneho ula.
 */

// Importy pre layout a zarovnanie
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

// Importy pre Material Design komponenty
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text

// Import pre Compose funkcionalitu
import androidx.compose.runtime.Composable

// Importy pre vizualne nastavenia
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * Polozka zaznacovacieho policka pre vyber ula.
 *
 * Zobrazi riadok s popisom ula na lavej strane a zaznacovacim polickom na pravej strane,
 * umoznujuc jednododuchy vyber/zrusenie vyberu konkretneho ula.
 *
 * @param label Text popisku (nazov ula)
 * @param checked Ci je policko aktualne zaznacene
 * @param onCheckedChange Callback volany pri zmene stavu zaznacovacieho policka
 */
@Composable
fun HiveCheckboxItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Riadok s obsahom rozmiestneny na cele siroke
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,      // Zarovnanie obsahu vertikalne na stred
        horizontalArrangement = Arrangement.SpaceBetween     // Polozky su od seba co najdalej (nazov vlavo, checkbox vpravo)
    ) {
        // Nazov ula s nastavenou velkostou pisma
        Text(text = label, fontSize = 20.sp)

        // Zaznacovacke policko s callbackom pre zmenu stavu
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}