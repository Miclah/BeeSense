package com.beesense.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beesense.data.db.dao.DiaryEntryDao
import com.beesense.data.db.dao.HiveDao
import com.beesense.data.db.dao.SettingsDao
import com.beesense.data.db.entities.DiaryEntryEntity
import com.beesense.data.db.entities.HiveEntity
import com.beesense.data.db.entities.SettingsEntity

/**
 * Room databaza pre aplikaciu BeeSense.
 *
 * Tato trieda definuje lokalnu databazu a poskytuje pristupy k jednotlivym DAO
 * rozhraniam pre manipulaciu s datami. Pouziva singleton pattern na zabezpecenie
 * jednej instancie databazy v celej aplikacii.
 */
@Database(
    entities = [SettingsEntity::class, DiaryEntryEntity::class, HiveEntity::class],
    version = 2, // Zvysena verzia z 1 na 2 kvoli zmenam v scheme
    exportSchema = false // Zabranuje exportu schemy, co potlaci varovania
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Poskytuje pristup k operaciam s nastaveniami aplikacie.
     * @return DAO rozhranie pre nastavenia
     */
    abstract fun settingsDao(): SettingsDao

    /**
     * Poskytuje pristup k operaciam s dennikmi.
     * @return DAO rozhranie pre dennikove zaznamy
     */
    abstract fun diaryEntryDao(): DiaryEntryDao

    /**
     * Poskytuje pristup k operaciam s ulmi.
     * @return DAO rozhranie pre uly
     */
    abstract fun hiveDao(): HiveDao

    companion object {
        /** Jedinecna instancia databazy (singleton pattern) */
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Ziska instanciu databazy - vytvori novu, ak este neexistuje.
         *
         * Tato metoda je synchronizovana, aby sa zabranilo vytvoreniu viacerych instancii
         * databazy pri sucasnych volaniach z roznych vlakien.
         *
         * @param context Kontext aplikacie potrebny na vytvorenie databazy
         * @return Jedinecna instancia databazy
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(false) // Ak sa zmeni schema, databaza sa vymaze a znova vytvori
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
