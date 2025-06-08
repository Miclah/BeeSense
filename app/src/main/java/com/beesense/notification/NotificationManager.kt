package com.beesense.notification

/**
 * Manager pre planovanie a spracovanie periodickych kontrol API a notifikacii.
 *
 * Tato trieda zabezpecuje pravidelnu kontrolu ulov pomocou WorkManager-a,
 * nastavuje podmienky pre spustanie kontrol a umoznuje zastavenie monitorovania.
 */

// Pomoc s AI

// Import pre pristup ku kontextu aplikacie
import android.content.Context

// Import pre logovanie
import android.util.Log

// Importy pre planovu pracu na pozadi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

// Import pre casove jednotky
import java.util.concurrent.TimeUnit

/**
 * Manager pre planovanie periodickych kontrol ulov a notifikacii.
 *
 * @property context Kontext aplikacie potrebny pre pristup k WorkManager
 */
class HiveNotificationManager(private val context: Context) {
    companion object {
        // Tag pre logovanie
        private const val TAG = "HiveNotificationManager"

        // Identifikator pre periodicku ulohu monitorovania
        private const val HIVE_MONITORING_WORK_NAME = "hive_monitoring_work"

        // Predvoleny interval kontrol: 30 minut
        // Poznamka: Aktualne nastavene na 1 minutu pre testovacie ucely
        private const val DEFAULT_CHECK_INTERVAL_MINUTES = 1L
    }

    /**
     * Spusti periodicke monitorovanie zmien vahy ulov.
     *
     * Naplanuje pracu na pozadi, ktora sa bude periodicky opakovat
     * a kontrolovat zmeny v udajoch ulov. Ak sa najdu vyznamne zmeny,
     * vygeneruju sa notifikacie.
     */
    fun startMonitoring() {
        Log.d(TAG, "Starting periodic hive monitoring")

        // Nastavenie podmienok pre spustenie prace - vyzaduje pripojenie k internetu
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Vytvorenie poziadavky na periodicku pracu
        val workRequest = PeriodicWorkRequestBuilder<HiveMonitoringWorker>(
            DEFAULT_CHECK_INTERVAL_MINUTES,  // Ako casto sa ma vykonavat
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)  // Aplikovanie podmienok
            .build()

        // Zaradenie prace s jedinecnym nazvom, aby bezala len jedna instancia naraz
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HIVE_MONITORING_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Nahradenie existujucej prace
            workRequest
        )

        // Logovanie uspesneho nastavenia monitorovania
        Log.d(TAG, "Periodic monitoring scheduled for every $DEFAULT_CHECK_INTERVAL_MINUTES minutes")
    }

    /**
     * Zastavi periodicke monitorovanie ulov.
     *
     * Zrusi vsetky naplanove monitorovacie ulohy.
     */
    fun stopMonitoring() {
        Log.d(TAG, "Stopping periodic hive monitoring")
        // Zrusenie prace podla jej jedinecneho identifikatora
        WorkManager.getInstance(context).cancelUniqueWork(HIVE_MONITORING_WORK_NAME)
    }
}
