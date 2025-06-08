package com.beesense.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.beesense.data.model.TrendType

// Pomoc s AI

/**
 * Komponenta na vizualizaciu trendu hodnoty pomocou grafickeho indikatora.
 *
 * Zobrazuje rozne graficke prvky v zavislosti od typu trendu (rast, pokles, stabilny).
 * Pre vyrazny rast/pokles sa zobrazuje cervena ikona, pre bezny rast/pokles
 * je ikona vo farbe temy a pre stabilny stav sa zobrazuje horizontalna ciara.
 *
 * @param trendType Typ trendu, ktory sa ma zobrazit
 * @param modifier Volitelny Modifier pre upravu vzhadu
 */
@Composable
fun TrendIndicator(
    trendType: TrendType,
    modifier: Modifier = Modifier
) {
    when (trendType) {
        TrendType.UP -> {
            // Ikona sipky nahor pre rastovy trend
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Zvýšenie",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(16.dp)
            )
        }
        TrendType.SIGNIFICANT_UP -> {
            // Cervena ikona sipky nahor pre vyrazny rast
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Výrazné zvýšenie",
                tint = Color(0xFFD32F2F), // Cervena farba pre vyrazny narast
                modifier = modifier.size(16.dp)
            )
        }
        TrendType.DOWN -> {
            // Ikona sipky nadol pre klesajuci trend
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Zníženie",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(16.dp)
            )
        }
        TrendType.SIGNIFICANT_DOWN -> {
            // Cervena ikona sipky nadol pre vyrazny pokles
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Výrazné zníženie",
                tint = Color(0xFFD32F2F), // Cervena farba pre vyrazny pokles
                modifier = modifier.size(16.dp)
            )
        }
        TrendType.STABLE -> {
            // Vlastna horizontalna ciara pre stabilnu hodnotu
            val outlineColor = MaterialTheme.colorScheme.outline
            Canvas(modifier = modifier.size(16.dp)) {
                drawLine(
                    color = outlineColor,
                    start = center.copy(x = 0f),
                    end = center.copy(x = size.width),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        TrendType.UNAVAILABLE -> {
            // Ak nie su dostupne udaje, nic nezobrazime
        }
    }
}

/**
 * Komponenta zobrazuje riadok s popisom hodnoty a jej trendom.
 *
 * Sklada sa z textoveho popisu hodnoty (nazov a hodnota) a indikatora trendu,
 * ktory zobrazuje ci hodnota rastie, klesa alebo je stabilna.
 *
 * @param label Nazov hodnoty (napr. "Teplota")
 * @param value Hodnota ako retazec, vratane jednotiek (napr. "25 °C")
 * @param trendType Typ trendu hodnoty (rast/pokles/stabilny)
 * @param modifier Volitelny Modifier pre upravu vzhadu
 */
@Composable
fun DataRowWithTrend(
    label: String,
    value: String,
    trendType: TrendType,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Zobrazenie nazvu hodnoty a samotnej hodnoty
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium
        )
        // Medzera medzi textom a indikatorom trendu
        Spacer(modifier = Modifier.width(4.dp))
        // Zobrazenie indikatora trendu
        TrendIndicator(trendType = trendType)
    }
}
