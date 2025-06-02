package com.beesense.ui.theme


import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFFFC107),
    primaryContainer = Color(0xFFFFD54F),
    secondary = Color(0xFFFF7043),
    secondaryContainer = Color(0xFFFFAB91),
    background = Color(0xFFFFF8E1),
    surface = Color(0xFFFFF8E1),
    onPrimary = Color(0xFF212121),
    onSecondary = Color(0xFF212121),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
)


private val DarkColors = darkColorScheme(
    primary = Color(0xFF8D6E63),
    primaryContainer = Color(0xFF795548),
    secondary = Color(0xFFFF7043),
    secondaryContainer = Color(0xFFD84315),
    background = Color(0xFF212121),
    surface = Color(0xFF212121),
    onPrimary = Color(0xFFFFF8E1),
    onSecondary = Color(0xFFFFF8E1),
    onBackground = Color(0xFFFFF8E1),
    onSurface = Color(0xFFFFF8E1)
)

@Composable
fun BeeSenseTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
