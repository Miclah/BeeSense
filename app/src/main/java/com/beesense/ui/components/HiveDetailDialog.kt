package com.beesense.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.beesense.data.model.HiveData

@Composable
fun HiveDetailDialog(hiveData: HiveData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Detaily úľa #${hiveData.id}",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hmotnosť: ${hiveData.totalWeight} kg" +
                            (hiveData.weightLeft?.let { " (L: $it" } ?: "") +
                            (hiveData.weightRight?.let { " / P: $it)" } ?: if (hiveData.weightLeft != null) ")" else ""),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Teplota: ${hiveData.temperatureSensor} °C", style = MaterialTheme.typography.bodyMedium)

                hiveData.humidity?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Vlhkosť: $it%", style = MaterialTheme.typography.bodyMedium)
                }

                hiveData.temperatureOutside?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Teplota vonku: $it°C", style = MaterialTheme.typography.bodyMedium)
                }

                hiveData.pressure?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tlak: $it hPa", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Čas merania: ${hiveData.timestamp}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ZAVRIEŤ", fontSize = 16.sp)
                }
            }
        }
    }
}
