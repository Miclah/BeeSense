/**
 * Hlavny subor pre definovanie temy aplikacie BeeSense
 * Obsahuje definicie farebnych schem pre svetly a tmavy rezim
 */
package com.beesense.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Farebna schema pre svetly rezim aplikacie
 * Pouziva prevazne medove a vcele odtiene
 */
private val LightColorScheme = lightColorScheme(
    primary = HoneyYellow,  // Hlavna medova farba
    onPrimary = DarkText,   // Tmavy text na medovej farbe
    primaryContainer = HoneyGold,  // Kontajner s tmavsiou medovou farbou
    onPrimaryContainer = DarkText,
    secondary = VibrantOrange,  // Sekundarna oranzova farba
    onSecondary = LightText,    // Svetly text na oranzovej farbe
    secondaryContainer = BeeOrange.copy(alpha = 0.15f),  // Sekundarny kontajner s priesvitnou oranzovou
    onSecondaryContainer = DarkText,
    tertiary = VibrantGreen,  // Terciarna zelena farba
    onTertiary = LightText,
    background = HoneycombBackground, // Pozadie s motivom plastu
    onBackground = DarkText,
    surface = WaxWhite,  // Teplejsia biela farba pre povrchy
    onSurface = DarkText,
    surfaceVariant = PollenYellow, // Variant povrchu s pelovou zltoou farbou
    onSurfaceVariant = DarkText.copy(alpha = 0.8f),
    outline = HiveBrown.copy(alpha = 0.5f),  // Priesvitna ulova hneda pre ohranicenia
    surfaceTint = HoneyYellow.copy(alpha = 0.2f),  // Slaby medovy nadych pre povrchy
    error = Color(0xFFB00020),  // Standardna cervena farba pre chyby
    onError = LightText
)

/**
 * Farebna schema pre tmavy rezim aplikacie
 * Pouziva tmavsi podtony medovych farieb
 */
private val DarkColorScheme = darkColorScheme(
    primary = HoneyYellow,  // Zachovana medova farba aj v tmavom rezime
    onPrimary = DarkText,   // Upravene pre lepsiu viditelnost v tmavom rezime
    primaryContainer = DarkAmber,  // Tmavsi medovy odtien pre kontajnery
    onPrimaryContainer = LightText,
    secondary = VibrantOrange,  // Zachovana oranzova sekundarna farba
    onSecondary = DarkText,     // Upravene pre lepsiu viditelnost v tmavom rezime
    secondaryContainer = BeeOrange.copy(alpha = 0.3f),  // Tmavsi priesvitny oranzovy kontajner
    onSecondaryContainer = LightText,
    tertiary = PastelGreen,  // Pastelova zelena pre tmavy rezim
    onTertiary = DarkText,
    background = DeepCharcoal,  // Hlboky tmavosedy odtien pre pozadie
    onBackground = LightText,
    surface = CharcoalWax,   // Tmavosedy voskovy odtien pre povrchy
    onSurface = LightText,
    surfaceVariant = MidnightHive,  // Tmavosiva farba s nadychom ula
    onSurfaceVariant = LightText.copy(alpha = 0.8f),
    outline = HiveBrown.copy(alpha = 0.7f),  // Vyraznejsie ohranicenia v tmavom rezime
    surfaceTint = HoneyYellow.copy(alpha = 0.2f),  // Zachovany medovy nadych pre povrchy
    error = Color(0xFFCF6679),  // Upravena cervena farba pre chyby v tmavom rezime
    onError = LightText
)

/**
 * Definicia zaoblenia rohov pre rozne velkosti prvkov
 */
val BeeSenseShapes = Shapes(
    small = RoundedCornerShape(4.dp),   // Male zaoblenie (napr. tlacidla)
    medium = RoundedCornerShape(8.dp),  // Stredne zaoblenie (napr. karty)
    large = RoundedCornerShape(12.dp)   // Velke zaoblenie (napr. dialogy)
)

/**
 * Kompozitna funkcia pre aplikaciu temy BeeSense
 *
 * @param darkTheme ci sa ma pouzit tmavy rezim, predvolene podla nastavenia systemu
 * @param dynamicColor ci sa ma pouzit dynamicke farbenie (Material You), predvolene vypnute
 * @param content obsah aplikacie, ktory bude pouzivat tuto temu
 */
@Composable
fun BeeSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Predvolene vypnute, aby sa pouzili nase vlastne farby
    content: @Composable () -> Unit
) {
    // Vyber farebnej schemy podla rezimu a nastaveni
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)  // Dynamicke farby pre tmavy rezim
            else dynamicLightColorScheme(context)           // Dynamicke farby pre svetly rezim
        }
        darkTheme -> DarkColorScheme  // Nase vlastne farby pre tmavy rezim
        else -> LightColorScheme      // Nase vlastne farby pre svetly rezim
    }

    // Aplikacia materialovej temy s nasimi farbami, typografiou a tvarmi
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = BeeSenseShapes,
        content = content
    )
}
