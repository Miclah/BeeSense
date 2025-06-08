package com.beesense.viewmodel

/**
 * ViewModel pre pracu s dennikovou casto aplikacie.
 *
 * Tato trieda zabezpecuje komunikaciu medzi UI a datovou vrstvou pre funkcie
 * suvisiace s dennikovu databazou. Umoznuje nacitanie vsetkych zaznamov a tiez
 * pridavanie, aktualizaciu a mazanie jednotlivych zaznamov.
 */

// Import pre pristup k aplikacnemu kontextu
import android.app.Application

// Importy pre ViewModel funkcionalitu
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

// Importy pre pristup k datovym objektom a databaze
import com.beesense.data.db.AppContainer
import com.beesense.data.db.entities.DiaryEntryEntity
import com.beesense.data.db.entities.toDiaryEntryEntity
import com.beesense.data.model.DiaryEntry

// Importy pre asynchrnonnu pracu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel pre spravu zaznamov v denniku.
 *
 * Rozsiruje AndroidViewModel pre pristup ku kontextu aplikacie,
 * ktory je potrebny pre vytvorenie repozitara.
 *
 * @property application Instancia aplikacie pre pristup ku kontextu
 */
class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    // Inicializacia kontajnera s databazovymi pristupmi
    private val appContainer = AppContainer(application)

    // Ziskanie repozitara pre operacie s dennikovou databazou
    private val diaryEntryRepository = appContainer.diaryRepository

    /**
     * Flow obsahujuci vsetky zaznamy z dennika.
     * Automaticky sa aktualizuje pri zmenach v databaze.
     */
    val allDiaryEntries: Flow<List<DiaryEntryEntity?>> = diaryEntryRepository.getDiaryStream()

    /**
     * Prida novy zaznam do dennika.
     *
     * @param diaryEntry Objekt obsahujuci data noveho zaznamu
     */
    fun addDiaryEntry(diaryEntry: DiaryEntry) {
        // Spustenie korutiny v scope ViewModel-u
        viewModelScope.launch {
            // Konverzia modelu DiaryEntry na entitu pre ulozenie v databaze
            val entity = diaryEntry.toDiaryEntryEntity()

            // Vlozenie entity do databazy cez repozitar
            diaryEntryRepository.insertDiaryEntry(entity)
        }
    }

    /**
     * Aktualizuje existujuci zaznam v denniku.
     *
     * @param diaryEntry Objekt obsahujuci aktualizovane data zaznamu
     */
    fun updateDiaryEntry(diaryEntry: DiaryEntry) {
        // Spustenie korutiny v scope ViewModel-u
        viewModelScope.launch {
            // Konverzia modelu DiaryEntry na entitu pre ulozenie v databaze
            val entity = diaryEntry.toDiaryEntryEntity()

            // Aktualizacia entity v databaze cez repozitar
            diaryEntryRepository.updateDiaryEntry(entity)
        }
    }

    /**
     * Vymaze zaznam z dennika.
     *
     * @param diaryEntry Objekt obsahujuci data zaznamu na zmazanie
     */
    fun deleteDiaryEntry(diaryEntry: DiaryEntry) {
        // Spustenie korutiny v scope ViewModel-u
        viewModelScope.launch {
            // Konverzia modelu DiaryEntry na entitu pre zmazanie z databazy
            val entity = diaryEntry.toDiaryEntryEntity()

            // Zmazanie entity z databazy cez repozitar
            diaryEntryRepository.deleteDiaryEntry(entity)
        }
    }
}
