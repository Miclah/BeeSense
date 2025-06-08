package com.beesense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.beesense.data.model.HiveDataWithTrend

/**
 * Dialog zobrazujuci detailne informacie o ule.
 *
 * Tento dialog zobrazuje vsetky dostupne udaje o ule ako su hmotnost, teplota, vlhkost a tlak.
 * Pri kazdom udaji je zobrazeny aj trend zmeny hodnoty. Dialog sa otvara po kliknuti na kartu ula
 * v hlavnom prehlade.
 *
 * @param hiveData Objekt obsahujuci vsetky udaje o ule a ich trendy
 * @param onDismiss Funkcia volana pri zatvoreni dialogu
 */
@Composable
fun HiveDetailDialog(hiveData: HiveDataWithTrend, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                // Hlavicka s titulkom bez tlacidla zatvorenia
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Zobrazenie nazvu ula alebo ID ak nazov nie je nastaveny
                        Text(
                            text = if (hiveData.displayName.isNotEmpty())
                                hiveData.displayName
                            else "Úľ #${hiveData.current.id}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        // Ak je nastaveny vlastny nazov, zobrazime ID ako podtitulok
                        if (hiveData.displayName.isNotEmpty()) {
                            Text(
                                text = "ID: ${hiveData.current.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Oddelovacia ciara medzi hlavickou a obsahom
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Obsah dialogu
                Column(modifier = Modifier.padding(24.dp)) {
                    // Celkova hmotnost ula s indikatorom trendu
                    DataRowWithTrend(
                        label = "Hmotnosť",
                        value = "${hiveData.current.totalWeight} kg",
                        trendType = hiveData.weightTrend
                    )

                    // Hmotnost lava strana - zobrazuje sa len ak su udaje dostupne
                    if (hiveData.current.weightLeft != null) {
                        DataRowWithTrend(
                            label = "Hmotnosť (ľavá)",
                            value = "${hiveData.current.weightLeft} kg",
                            trendType = hiveData.leftWeightTrend
                        )
                    }

                    // Hmotnost prava strana - zobrazuje sa len ak su udaje dostupne
                    if (hiveData.current.weightRight != null) {
                        DataRowWithTrend(
                            label = "Hmotnosť (pravá)",
                            value = "${hiveData.current.weightRight} kg",
                            trendType = hiveData.rightWeightTrend
                        )
                    }

                    // Medzera medzi skupinami udajov
                    Spacer(modifier = Modifier.height(8.dp))

                    // Teplota vnutri ula s indikatorom trendu
                    DataRowWithTrend(
                        label = "Teplota",
                        value = "${hiveData.current.temperatureSensor} °C",
                        trendType = hiveData.temperatureTrend
                    )

                    // Vonkajsia teplota - zobrazuje sa len ak su udaje dostupne
                    if (hiveData.current.temperatureOutside != null) {
                        DataRowWithTrend(
                            label = "Teplota vonku",
                            value = "${hiveData.current.temperatureOutside} °C",
                            trendType = hiveData.outsideTemperatureTrend
                        )
                    }

                    // Vlhkost vzduchu - zobrazuje sa len ak su udaje dostupne
                    if (hiveData.current.humidity != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRowWithTrend(
                            label = "Vlhkosť",
                            value = "${hiveData.current.humidity} %",
                            trendType = hiveData.humidityTrend
                        )
                    }

                    // Atmosfericky tlak - zobrazuje sa len ak su udaje dostupne
                    if (hiveData.current.pressure != null) {
                        DataRowWithTrend(
                            label = "Tlak",
                            value = "${hiveData.current.pressure} hPa",
                            trendType = hiveData.pressureTrend
                        )
                    }

                    // Vacsie odsadenie pred tlacidlom
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tlacidlo na zatvorenie dialogu
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "ZAVRIEŤ",
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
