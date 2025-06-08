/**
 * Subor definujuci navigacne ciele aplikacie BeeSense
 * Obsahuje sealed class so vsetkymi obrazovkami aplikacie
 */
package com.beesense.ui.navigation

import com.beesense.R

/**
 * Sealed trieda reprezentujuca vsetky obrazovky dostupne v aplikacii
 * Kazda obrazovka ma definovanu svoju routu, nazov a volitelne ikonu
 *
 * @param route unikatna cesta pouzita pre navigaciu
 * @param label nazov obrazovky zobrazeny pouzivatelovi
 * @param icon volitelna ikona pre obrazovku (pouzivana v navigacnych prvkoch)
 */
sealed class Screen(val route: String, val label: String, val icon: Int? = null) {
    // Hlavne obrazovky dostupne z bottom navigation
    object Overview : Screen("overview", "Prehľad", R.drawable.ic_overview)        // Obrazovka s hlavnym prehladom
    object Graphs : Screen("graphs", "Grafy", R.drawable.ic_graph)                 // Obrazovka s grafmi nameranych hodnot
    object Menu : Screen("menu", "Menu", R.drawable.ic_menu)                       // Obrazovka s menu aplikacie

    // Sekundarne obrazovky dostupne z menu alebo inych casti aplikacie
    object HiveManagement : Screen("hive_management", "Zoznam Úľov")               // Obrazovka pre spravu ulov
    object Diary : Screen("diary", "Denník")                                       // Obrazovka s vcielarsym dennikom
    object SQLManagement : Screen("sql_management", "Zobrazenie/Editovanie SQL dát") // Obrazovka pre priamu pracu s databazou
    object Settings : Screen("settings", "Nastavenia")                             // Obrazovka s nastaveniami aplikacie
    object HiveEditor : Screen("hive_editor", "Pridať/Editovať úľ")                // Obrazovka pre pridavanie alebo editaciu ula
}
