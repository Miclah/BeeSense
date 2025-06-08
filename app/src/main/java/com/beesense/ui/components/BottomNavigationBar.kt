package com.beesense.ui.components

/**
 * Komponenta spodnej navigacnej listy aplikacie.
 *
 * Zobrazuje ikony a popisky pre hlavne sekcie aplikacie,
 * umoznuje navigaciu medzi obrazovkami a vizualne indikuje
 * aktivnu sekciu.
 */

// Importy pre Material Design komponenty
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text

// Import pre Compose funkcionalitu
import androidx.compose.runtime.Composable

// Import pre nacitanie zdrojov
import androidx.compose.ui.res.painterResource

// Import pre navigaciu
import androidx.navigation.NavController

// Importy pre definovanie obrazoviek a farieb
import com.beesense.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme

/**
 * Spodna navigacna lista aplikacie.
 *
 * @param navController Kontroler pre navigaciu medzi obrazovkami
 * @param items Zoznam obrazoviek pre zobrazenie v navigacii
 * @param currentRoute Aktualna cesta (aktivna obrazovka)
 * @param isSubScreen Ci sa nachadzame v podrazenej obrazovke (ovplyvnuje aktivovanie poloziek)
 * @param onItemSelected Callback volany pri vybere polozky
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Screen>,
    currentRoute: String,
    isSubScreen: Boolean,
    onItemSelected: (Screen) -> Unit
) {
    // Hlavny kontajner spodnej navigacnej listy
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary  // Farba pozadia listy
    ) {
        // Vykreslenie vsetkych poloziek v navigacii
        items.forEach { screen ->
            // Aktivovanie/deaktivovanie poloziek na zaklade aktualnej obrazovky
            // Na podrazenych obrazovkach je aktivna iba polozka Menu
            val isEnabled = !isSubScreen || screen == Screen.Menu

            // Jedna polozka v spodnej navigacii
            NavigationBarItem(
                // Ci je tato polozka aktualne vybrata
                selected = currentRoute == screen.route,
                // Akcia pri kliknuti
                onClick = {
                    // Ak je polozka deaktivovana, nic sa nedeje
                    if (!isEnabled) return@NavigationBarItem

                    // Specialna navigacia pre Menu - navrat na hlavnu obrazovku
                    if (screen.route == Screen.Menu.route) {
                        navController.navigate(Screen.Menu.route) {
                            // Odstranenie vsetkych predchadzajucich obrazoviek az po Menu z backstack
                            popUpTo(Screen.Menu.route) {
                                inclusive = true  // Vrati sa az po Menu a vytvori novu instanciu
                            }
                            launchSingleTop = true  // Zabrani vytvoreniu duplikatov
                        }
                    } else {
                        // Pre ostatne polozky pouzivame standardny callback
                        onItemSelected(screen)
                    }
                },
                // Ci je polozka aktivna alebo deaktivovana
                enabled = isEnabled,
                // Ikona polozky
                icon = {
                    screen.icon?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = screen.label  // Popis pre pristupnost
                        )
                    }
                },
                // Popisok pod ikonou
                label = { Text(screen.label) },
                // Nastavenie farieb pre rozne stavy polozky
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,  // Farba ikony vybranej polozky
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (isEnabled) 0.6f else 0.3f  // Priehladnost pre nevybrane/deaktivovane polozky
                    ),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,  // Farba textu vybranej polozky
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (isEnabled) 0.6f else 0.3f  // Priehladnost pre nevybrane/deaktivovane polozky
                    )
                )
            )
        }
    }
}
