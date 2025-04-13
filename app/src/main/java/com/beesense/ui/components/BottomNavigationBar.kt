package com.beesense.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.beesense.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Screen>,
    currentRoute: String,
    isSubScreen: Boolean,
    onItemSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { screen ->
            val isEnabled = !isSubScreen || screen == Screen.Menu

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (!isEnabled) return@NavigationBarItem

                    if (screen.route == Screen.Menu.route) {
                        navController.navigate(Screen.Menu.route) {
                            popUpTo(Screen.Menu.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        onItemSelected(screen)
                    }
                },
                enabled = isEnabled,
                icon = {
                    screen.icon?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = screen.label
                        )
                    }
                },
                label = { Text(screen.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (isEnabled) 0.6f else 0.3f
                    ),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (isEnabled) 0.6f else 0.3f
                    )
                )
            )
        }
    }
}
