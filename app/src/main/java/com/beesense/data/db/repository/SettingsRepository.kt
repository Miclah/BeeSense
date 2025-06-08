package com.beesense.data.db.repository

import com.beesense.data.db.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Rozhranie definujuce operacie s nastaveniami aplikacie.
 *
 * Toto rozhranie abstrahuje operacie s nastaveniami a umoznuje
 * pouzivat rozne implementacie - lokalnu perzistenciu
 * v Room databaze, sietove volania, alebo ich kombinaciu.
 */
interface SettingsRepository {
    /**
     * Ziska aktualne nastavenia aplikacie ako reaktivny stream.
     *
     * @return Flow s nastaveniami alebo null ak nastavenia este neexistuju
     */
    fun getSettingsStream(): Flow<SettingsEntity?>

    /**
     * Ulozi alebo aktualizuje nastavenia aplikacie.
     *
     * @param settings Objekt s nastaveniami na ulozenie
     */
    suspend fun insertSettings(settings: SettingsEntity)
}
