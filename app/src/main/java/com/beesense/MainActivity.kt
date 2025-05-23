package com.beesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.beesense.ui.components.BottomNavigationBar
import com.beesense.ui.navigation.Screen
import com.beesense.ui.screens.OverviewScreen
import com.beesense.ui.screens.GraphScreen
import com.beesense.ui.screens.MenuScreen
import com.beesense.ui.screens.subscreens.DiaryScreen
import com.beesense.ui.screens.subscreens.HiveEditorScreen
import com.beesense.ui.screens.subscreens.HiveManagementScreen
import com.beesense.ui.screens.subscreens.SQLManagementScreen
import com.beesense.ui.screens.subscreens.SettingsScreen
import com.beesense.ui.theme.BeeSenseTheme

class MainActivity : ComponentActivity() {


    // TODO: Scroll list v menu a settings
    // TODO: zachovavanie hodnot po tom co dam landscape rezim
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeeSenseTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val items = listOf(Screen.Overview, Screen.Graphs, Screen.Menu)

    Scaffold(
        bottomBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route ?: Screen.Overview.route
            val isSubScreen = currentRoute !in listOf(
                Screen.Overview.route,
                Screen.Graphs.route,
                Screen.Menu.route
            )

            BottomNavigationBar(
                navController = navController,
                items = items,
                currentRoute = currentRoute,
                isSubScreen = isSubScreen,
                onItemSelected = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Overview.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Overview.route) { OverviewScreen() }
            composable(Screen.Graphs.route) { GraphScreen() }
            composable(Screen.Menu.route) { MenuScreen(navController) }
            composable(Screen.HiveManagement.route) { HiveManagementScreen(navController) }
            composable(Screen.Diary.route) { DiaryScreen() }
            composable(Screen.SQLManagement.route) { SQLManagementScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.HiveEditor.route) { HiveEditorScreen() }
        }
    }
}
