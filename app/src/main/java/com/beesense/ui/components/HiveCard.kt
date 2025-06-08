package com.beesense.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.data.model.HiveDataWithTrend
import com.beesense.data.model.TrendType

/**
 * Komponent pre zobrazenie kompaktnej karty ula.
 *
 * Tato komponenta zobrazuje zakladne udaje o ule v minimalistickom dizajne.
 * Karta obsahuje nazov ula, jeho hmotnost a teplotu, pricom pri kazdej hodnote
 * zobrazuje aj indikator trendu. Karta je navhnuta pre efektivne zobrazenie
 * v mriezke, kde dve karty mozu byt zobrazene vedla seba.
 *
 * @param hiveData Objekt obsahujuci aktualne data ula a ich trendy
 * @param isFavorite Priznak, ci je ul oznaceny ako oblubeny
 * @param onFavoriteToggle Callback funkcia volana pri zmene stavu oblubeny
 */
@Composable
fun HiveCard(
    hiveData: HiveDataWithTrend,
    isFavorite: Boolean,
    onFavoriteToggle: (Int) -> Unit
) {
    // Stavove premenne pre zobrazenie dialogov
    var showDetailDialog by remember { mutableStateOf(false) }
    var showFavoriteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(180.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(14.dp)  // Zaoblenie rohov pre maksi vzhlad
            )
            .clickable { showDetailDialog = true },  // Otvori detail po kliknuti
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            // Vrchny riadok s nazvom ula a tlacidlom pre oblubene
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nazov ula - pouziva vlastny nazov ak je nastaveny, inak ID
                Text(
                    text = if (hiveData.displayName.isNotEmpty())
                        hiveData.displayName
                    else "Úľ #${hiveData.current.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,  // Skrati text ak je prilis dlhy
                    modifier = Modifier.weight(1f)
                )

                // Ikona pre oznacenie ula ako oblubeny
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            showFavoriteConfirmation = true  // Zobrazi dialog pre potvrdenie odstranenia
                        } else {
                            onFavoriteToggle(hiveData.current.id)  // Priamo oznaci ako oblubeny
                        }
                    },
                    modifier = Modifier
                        .padding(0.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Obľúbený",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Medzera medzi nazvom a udajmi
            Spacer(modifier = Modifier.height(8.dp))

            // Obsah karty s nameranymi udajmi
            Column(
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                // Hmotnost ula s indikatorom trendu
                MiniDataRowWithTrend(
                    label = "Hmotnosť:",
                    value = "${hiveData.current.totalWeight} kg",
                    trendType = hiveData.weightTrend
                )

                // Medzera medzi udajmi
                Spacer(modifier = Modifier.height(5.dp))

                // Teplota ula s indikatorom trendu
                MiniDataRowWithTrend(
                    label = "Teplota:",
                    value = "${hiveData.current.temperatureSensor} °C",
                    trendType = hiveData.temperatureTrend
                )
            }
        }
    }

    // Dialogy, ktore sa zobrazia podla potreby
    // Dialog s detailnymi informaciami o ule
    if (showDetailDialog) {
        HiveDetailDialog(hiveData = hiveData, onDismiss = { showDetailDialog = false })
    }

    // Potvrdenie odstranenia z oblubenych
    if (showFavoriteConfirmation) {
        FavoriteDialog(
            onConfirm = {
                onFavoriteToggle(hiveData.current.id)
                showFavoriteConfirmation = false
            },
            onDismiss = { showFavoriteConfirmation = false }
        )
    }
}

/**
 * Kompaktne zobrazenie udaju s trendom pre HiveCard.
 *
 * Podobne ako standardna komponenta DataRowWithTrend, ale optimalizovane
 * pre mensiu velkost a kompaktny vzhlad v karte ula.
 *
 * @param label Nazov merania (napr. "Hmotnost:")
 * @param value Hodnota merania vratane jednotiek (napr. "15.5 kg")
 * @param trendType Typ trendu hodnoty (rast/pokles/stabilny)
 */
@Composable
private fun MiniDataRowWithTrend(
    label: String,
    value: String,
    trendType: TrendType
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        // Nazov merania - mierne zmenseny text
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            modifier = Modifier.padding(end = 4.dp)
        )

        // Hodnota merania - vyraznejsie zobrazena
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,  // Vyraznejsi font pre hodnoty
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        // Medzera pred indikatorom trendu
        Spacer(modifier = Modifier.width(3.dp))
        // Zobrazenie indikatora trendu
        MiniTrendIndicator(trendType = trendType)
    }
}

/**
 * Ziskava farbu pre ikony trendov podla typu trendu.
 *
 * Zabezpecuje konzistentne farby pre indikaciu trendov v celej aplikacii.
 *
 * @param trendType Typ trendu pre ktory sa ma ziskat farba
 * @return Farba zodpovedajuca danemu typu trendu
 */
@Composable
private fun getTrendColor(trendType: TrendType): Color {
    return when (trendType) {
        TrendType.UP -> MaterialTheme.colorScheme.primary
        TrendType.SIGNIFICANT_UP -> Color(0xFFD32F2F)  // Cervena pre vyznamny narast
        TrendType.DOWN -> MaterialTheme.colorScheme.primary
        TrendType.SIGNIFICANT_DOWN -> Color(0xFFD32F2F)  // Cervena pre vyznamny pokles
        else -> MaterialTheme.colorScheme.onSurface
    }
}

/**
 * Kompaktna verzia indikatora trendu so spravnymi farbami.
 *
 * Zobrazuje sipky nahor alebo nadol podla typu trendu, pricom pre
 * vyznamne zmeny pouziva cervenu farbu.
 *
 * @param trendType Typ trendu ktory sa ma zobrazit
 */
@Composable
private fun MiniTrendIndicator(trendType: TrendType) {
    when (trendType) {
        TrendType.UP, TrendType.SIGNIFICANT_UP -> {
            // Sipka nahor pre rastovy trend
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Nárast",
                tint = if (trendType == TrendType.SIGNIFICANT_UP)
                    Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        TrendType.DOWN, TrendType.SIGNIFICANT_DOWN -> {
            // Sipka nadol pre klesajuci trend
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Pokles",
                tint = if (trendType == TrendType.SIGNIFICANT_DOWN)
                    Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        else -> {
            // Pre STABLE a UNAVAILABLE nezobrazujeme nic
        }
    }
}
