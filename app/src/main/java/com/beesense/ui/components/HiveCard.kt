package com.beesense.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesense.data.model.HiveData

@Composable
fun HiveCard(
    hiveData: HiveData,
    isFavorite: Boolean,
    onFavoriteToggle: (Int) -> Unit
) {
    var showDetailDialog by remember { mutableStateOf(false) }
    var showFavoriteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { showDetailDialog = true },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Úľ #${hiveData.id}",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            showFavoriteConfirmation = true
                        } else {
                            onFavoriteToggle(hiveData.id)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Obľúbený",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hmotnosť: ${hiveData.totalWeight} kg",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Teplota: ${hiveData.temperatureSensor} °C",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDetailDialog) {
        HiveDetailDialog(hiveData = hiveData, onDismiss = { showDetailDialog = false })
    }

    if (showFavoriteConfirmation) {
        FavoriteDialog (
            onConfirm = {
                onFavoriteToggle(hiveData.id)
                showFavoriteConfirmation = false
            },
            onDismiss = { showFavoriteConfirmation = false }
        )
    }
}
