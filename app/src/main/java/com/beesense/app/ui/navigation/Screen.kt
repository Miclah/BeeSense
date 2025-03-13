package com.beesense.app.ui.navigation

import com.beesense.app.R

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Overview : Screen("overview", "Prehľad", R.drawable.ic_overview)
    object Graphs : Screen("graphs", "Grafy", R.drawable.ic_graph)
}
