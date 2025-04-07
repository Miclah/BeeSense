package com.beesense.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.beesense.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu", fontSize = 22.sp) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuItem("📦 Pridať/Editovať úle") {
                navController.navigate(Screen.HiveManagement.route) {
                    popUpTo(Screen.Menu.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            MenuItem("📔 Denník") {
                navController.navigate(Screen.Diary.route) {
                    popUpTo(Screen.Menu.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            MenuItem("📊 Zobrazenie/Editovanie SQL dát") {
                navController.navigate(Screen.SQLManagement.route) {
                    popUpTo(Screen.Menu.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            MenuItem("⚙️ Nastavenia") {
                navController.navigate(Screen.Settings.route) {
                    popUpTo(Screen.Menu.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
fun MenuItem(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
