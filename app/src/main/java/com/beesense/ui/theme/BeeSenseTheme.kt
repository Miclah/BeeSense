package com.beesense.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = HoneyYellow,
    secondary = BeeOrange,
    tertiary = LightYellow,
    background = LightCream,
    surface = LightCream,
    onPrimary = BeeBlack,
    onSecondary = BeeBlack,
    onBackground = BeeBlack,
    onSurface = BeeBlack
)

private val DarkColors = darkColorScheme(
    primary = DarkHoney,
    secondary = BeeOrange,
    background = BeeBlack,
    surface = BeeBlack,
    onPrimary = LightCream,
    onSecondary = LightCream,
    onBackground = LightCream,
    onSurface = LightCream
)

@Composable
fun BeeSenseTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
