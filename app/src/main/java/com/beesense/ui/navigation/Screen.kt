    package com.beesense.ui.navigation

    import com.beesense.R

    sealed class Screen(val route: String, val label: String, val icon: Int? = null) {
        object Overview : Screen("overview", "Prehľad", R.drawable.ic_overview)
        object Graphs : Screen("graphs", "Grafy", R.drawable.ic_graph)
        object Menu : Screen("menu", "Menu", R.drawable.ic_menu)

        object HiveManagement : Screen("hive_management", "Pridať/Editovať úle")
        object Diary : Screen("diary", "Denník")
        object SQLManagement : Screen("sql_management", "Zobrazenie/Editovanie SQL dát")
        object Settings : Screen("settings", "Nastavenia")
    }
