package com.beesense.viewmodel

/**
 * ViewModel pre spravu nastaveni aplikacie.
 *
 * Tato trieda zabezpecuje nacitanie, spracovanie a ulozenie nastaveni aplikacie,
 * ako su tema, notifikacie a prahy pre notifikacie. Poskytuje rozhranie pre UI
 * komponenty na zobrazenie a upravu tychto nastaveni.
 */

// Import pre pristup k aplikacnemu kontextu
import android.app.Application

// Importy pre ViewModel funkcionalitu
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

// Importy pre pristup k databaze a entitam
import com.beesense.data.db.AppContainer
import com.beesense.data.db.entities.SettingsEntity
import com.beesense.data.db.repository.SettingsRepository

// Importy pre reaktivne datove toky a korutiny
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pre spravu nastaveni aplikacie.
 *
 * @property application Instancia aplikacie pre pristup ku kontextu
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    // Inicializacia repozitara pre pracu s nastaveniami
    private val repository: SettingsRepository = AppContainer(application).settingsRepository

    // Predvolene hodnoty nastaveni
    private val defaultSettings = SettingsEntity(
        isDarkMode = false,                 // Predvolene svetla tema
        areNotificationsEnabled = false,    // Predvolene vypnute notifikacie
        weightThresholdKg = 3f,            // Predvoleny prah zmeny vahy pre notifikacie (3 kg)
        inactivityThresholdHours = 5,      // Predvoleny prah neaktivity pre eskalaciju notifikacii (5 hodin)
        notificationIntervalHours = 4       // Predvoleny interval medzi notifikaciami (4 hodiny)
    )

    // Lokálny stavový tok pre sledovanie hodnôt vo formulári, ktoré sa majú zachovať pri otočení obrazovky
    private val _formState = MutableStateFlow(defaultSettings)

    // Verejny pristup k stavu formulara
    val formState: StateFlow<SettingsEntity> = _formState
        .stateIn(
            viewModelScope,                 // Rozsah korutiny viazany na zivotnost ViewModelu
            SharingStarted.WhileSubscribed(5000),  // Flow ostava aktivny 5 sekund po tom, co neexistuje ziadny odberatel
            defaultSettings                 // Predvolena hodnota
        )

    // Nastavenia nacitane z databazy
    val settings: StateFlow<SettingsEntity> =
        repository.getSettingsStream()      // Ziskame Flow z repozitora
            .map { it ?: defaultSettings }  // Ak je null, pouzijeme predvolene nastavenia
            .stateIn(
                viewModelScope,             // Rozsah korutiny viazany na zivotnost ViewModelu
                SharingStarted.Eagerly,     // Flow sa aktivuje okamzite pri vytvoreni
                defaultSettings             // Predvolena hodnota, kym sa Flow neinicializuje
            )

    /**
     * Inicializacia ViewModelu - nastavi formular podla hodnot z databazy.
     */
    init {
        // Inicializacia stavu formulara z databazy pri vytvoreni ViewModelu
        viewModelScope.launch {
            // Zacneme sledovat nastavenia z databazy
            repository.getSettingsStream().collect { dbSettings ->
                // Ak mame nastavenia, aktualizujeme stav formulara
                if (dbSettings != null) {
                    _formState.value = dbSettings
                }
            }
        }
    }

    /**
     * Aktualizuje cely stav formulara naraz.
     *
     * @param settings Nove nastavenia, ktore maju nahradit existujuce
     */
    fun updateFormState(settings: SettingsEntity) {
        _formState.value = settings
    }

    /**
     * Ulozi aktualne nastavenia formulara do databazy.
     */
    fun save() {
        // Spustenie korutiny vo ViewModelScope
        viewModelScope.launch {
            // Ulozenie hodnot z formulara do databazy
            repository.insertSettings(_formState.value)
        }
    }

    // Metody na aktualizaciu jednotlivych nastaveni s zachovanim ostatnych hodnot

    /**
     * Aktualizuje nastavenie temneho rezimu.
     *
     * @param isDarkMode True pre aktivaciu temneho rezimu, False pre svetly rezim
     */
    fun updateDarkMode(isDarkMode: Boolean) {
        // Vytvori novu kopiu stavu s aktualizovanou hodnotou isDarkMode
        _formState.value = _formState.value.copy(isDarkMode = isDarkMode)
    }

    /**
     * Aktualizuje nastavenie povolenia notifikacii.
     *
     * @param areNotificationsEnabled True pre povolenie notifikacii, False pre zakazanie
     */
    fun updateNotificationsEnabled(areNotificationsEnabled: Boolean) {
        // Vytvori novu kopiu stavu s aktualizovanou hodnotou areNotificationsEnabled
        _formState.value = _formState.value.copy(areNotificationsEnabled = areNotificationsEnabled)
    }

    /**
     * Aktualizuje prah zmeny vahy pre notifikacie.
     *
     * @param weightThresholdKg Nova hodnota prahu vahy v kilogramoch
     */
    fun updateWeightThreshold(weightThresholdKg: Float) {
        // Vytvori novu kopiu stavu s aktualizovanou hodnotou weightThresholdKg
        _formState.value = _formState.value.copy(weightThresholdKg = weightThresholdKg)
    }

    /**
     * Aktualizuje prah neaktivity pre eskalaciju notifikacii.
     *
     * @param inactivityThresholdHours Novy prah neaktivity v hodinach
     */
    fun updateInactivityThreshold(inactivityThresholdHours: Int) {
        // Vytvori novu kopiu stavu s aktualizovanou hodnotou inactivityThresholdHours
        _formState.value = _formState.value.copy(inactivityThresholdHours = inactivityThresholdHours)
    }

    /**
     * Aktualizuje interval medzi notifikaciami.
     *
     * @param notificationIntervalHours Novy interval notifikacii v hodinach
     */
    fun updateNotificationInterval(notificationIntervalHours: Int) {
        // Vytvori novu kopiu stavu s aktualizovanou hodnotou notificationIntervalHours
        _formState.value = _formState.value.copy(notificationIntervalHours = notificationIntervalHours)
    }
}
