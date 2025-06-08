package com.beesense.data.db

import android.content.Context
import com.beesense.data.db.repository.DiaryRepository
import com.beesense.data.db.repository.HiveRepository
import com.beesense.data.db.repository.OfflineDiaryRepository
import com.beesense.data.db.repository.OfflineHiveRepository
import com.beesense.data.db.repository.OfflineSettingsRepository
import com.beesense.data.db.repository.SettingsRepository

//pomoc AI
/**
 * Kontajner poskytujuci pristupy k repozitarom v celej aplikacii.
 *
 * Trieda funguje ako jednoduchy service locator alebo dependency provider
 * pre repozitarove implementacie. Centralizuje vytvaranie a poskytovanie
 * repozitarov a zabezpecuje tiez zdielanie databazovej instancie.
 */
class AppContainer(context : Context) {
    /**
     * Instancia Room databazy zdielana vsetkymi repozitarmi
     */
    private val database = AppDatabase.getDatabase(context)

    /**
     * Repozitar pre praci s nastaveniami aplikacie.
     * Vytvori sa "lenivo" az pri prvom pouziti.
     */
    val settingsRepository: SettingsRepository by lazy {
        OfflineSettingsRepository(database.settingsDao())
    }

    /**
     * Repozitar pre pracu s dennikmi vcelara.
     * Vytvori sa "lenivo" az pri prvom pouziti.
     */
    val diaryRepository: DiaryRepository by lazy {
        OfflineDiaryRepository(database.diaryEntryDao())
    }

    /**
     * Repozitar pre pracu s ulmi.
     * Vytvori sa "lenivo" az pri prvom pouziti.
     */
    val hiveRepository: HiveRepository by lazy {
        OfflineHiveRepository(database.hiveDao())
    }
}
