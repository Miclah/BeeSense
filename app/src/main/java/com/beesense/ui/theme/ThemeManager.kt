/**
 * Subor pre spravu temy aplikacie BeeSense
 * Obsahuje ThemeManager ako singleton a ThemeViewModel pre UI vrstvu
 */
package com.beesense.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beesense.data.db.entities.SettingsEntity
import com.beesense.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Singleton objekt, ktory poskytuje pristup k nastaveniam temy v celej aplikacii
 * Sluzi ako centralne ulozisko pre informaciu o aktualnom rezime temy
 */
object ThemeManager {
    // Default hodnota pre rezim temy je svetly rezim (false = svetla tema)
    private val _isDarkTheme = MutableStateFlow(false)  // Privatny MutableStateFlow pre internu spravu
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme  // Verejny StateFlow pre citanie hodnoty zvonku

    /**
     * Metoda na aktualizaciu rezimu temy
     * @param isDark true pre tmavy rezim, false pre svetly rezim
     */
    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark  // Nastavenie novej hodnoty pre tmavy rezim
    }
}

/**
 * ViewModel pre spravu temy v UI vrstve
 * Predstavuje spojenie medzi ThemeManager a UI komponentami
 */
class ThemeViewModel : ViewModel() {
    // Stavova premenna na sledovanie aktualneho rezimu temy v UI
    var isDarkTheme by mutableStateOf(false)
        private set  // Nastavenie je mozne len interne, zvonka je hodnota len na citanie

    init {
        // Pri spusteni ViewModelu sledujeme hodnotu z ThemeManager
        viewModelScope.launch {
            // Pouzitie coroutiny na zber hodnoty z ThemeManager.isDarkTheme
            ThemeManager.isDarkTheme.collect { isDark ->
                isDarkTheme = isDark  // Aktualizacia lokalnej hodnoty podla ThemeManager
            }
        }
    }

    /**
     * Metoda na zmenu rezimu temy
     * @param isDark true pre tmavy rezim, false pre svetly rezim
     */
    fun updateDarkThemeMode(isDark: Boolean) {
        ThemeManager.setDarkTheme(isDark)  // Delegovanie aktualizacie na ThemeManager
    }
}
