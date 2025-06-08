package com.beesense.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// S pomocu AI
/**
 * Factory pre vytvaranie [HiveEditorViewModel] s podporou SavedStateHandle
 *
 * Tato tovaren zabezpecuje, ze ViewModel bude mat pristup k uloznym stavom,
 * ktore preziju ukoncenie aplikacie alebo jej znovuspustenie
 */
class HiveEditorViewModelFactory(
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {

    // Potlacenie upozornenia na neskontrolovane pretypovanie
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kontrola, ci pozadovany typ je kompatibilny s HiveEditorViewModel
        if (modelClass.isAssignableFrom(HiveEditorViewModel::class.java)) {
            // Vytvorenie a vratenie novej instancie HiveEditorViewModel s ulozenym stavom
            return HiveEditorViewModel(savedStateHandle) as T
        }
        // Ak sa pozaduje nekompatibilny typ, vyhodime vynimku
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
