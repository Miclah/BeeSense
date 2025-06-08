package com.beesense.notification

// pomoc s AI
/**
 * Worker pre monitorovanie stavu ulov a generovanie notifikacii.
 *
 * Tato trieda sa pravidelne spusta na pozadi a kontroluje udaje o uloch,
 * detekuje vyznamne zmeny vahy a posiela notifikacie pouzivatelovi.
 * Implementuje rozne urovne zavaznosti notifikacii na zaklade casoveho
 * intervalu a nastaveni pouzivatela.
 */

// Importy pre notifikacie a manazment systemu Android
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

// Import pre logovanie
import android.util.Log

// Importy pre notifikacie a komponenty AndroidX
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// Importy pre pristup k aktivitam, API a lokalnej databaze
import com.beesense.MainActivity
import com.beesense.R
import com.beesense.data.api.ApiService
import com.beesense.data.api.HiveDataDto
import com.beesense.data.db.AppContainer
import com.beesense.data.db.entities.SettingsEntity

// Importy pre korutiny a asynchronnu pracu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

// Importy pre formatovanie a pracu s datumom
import java.text.DecimalFormat

/**
 * CoroutineWorker pre monitorovanie stavu úľov a posielanie notifikácií
 */
class HiveMonitoringWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        // Tag pre logovanie
        private const val TAG = "HiveMonitoringWorker"

        // Pouziva sa rovnaky ID kanala ako v triede Notification pre konzistenciu
        private const val NOTIFICATION_CHANNEL_ID = "beesense_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "BeeSense Notifications"

        // ID notifikacii pre rozne urovne zavaznosti
        private const val NOTIFICATION_ID_INFO = 1001
        private const val NOTIFICATION_ID_WARNING = 1002
        private const val NOTIFICATION_ID_ALERT = 1003

        // Urovne zavaznosti notifikacii
        private const val SEVERITY_INFO = 0      // Informacna uroven
        private const val SEVERITY_WARNING = 1   // Varovanie
        private const val SEVERITY_ALERT = 2     // Najvyssia uroven - vystraha

        // SharedPreferences pre sledovanie historie notifikacii
        private const val PREF_NAME = "hive_monitoring_prefs"
        private const val KEY_LAST_NOTIF_TIME = "last_notification_time_"
        private const val KEY_NOTIF_LEVEL = "notification_level_"
    }

    // Datova trieda pre detaily notifikacie na zjednodusenie handlovania notifikacii
    private data class NotificationDetails(
        val title: String,
        val message: String,
        val priority: Int,
        val notificationId: Int
    )

    // Format pre zobrazenie vahy s presnostou na 2 desatinne miesta
    private val weightFormat = DecimalFormat("0.##")

    /**
     * Metóda, ktorá sa volá keď sa má worker vykonať.
     * Momentálne je kompletne deaktivovaná, aby sa zabránilo pádom aplikácie.
     *
     * Tato metoda kontroluje vsetky uly, ich zmeny vahy a podla nastaveni
     * generuje relevantne notifikacie.
     */
    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting hive monitoring work")

        try {
            // Pouziva withContext(Dispatchers.IO) pre operacie s databazou
            val settings = withContext(Dispatchers.IO) {
                val appContainer = AppContainer(applicationContext)
                try {
                    // Nacitanie nastaveni z databazy pomocou repozitara
                    appContainer.settingsRepository.getSettingsStream().first()
                } catch (e: Exception) {
                    // Logovanie chyby pri nacitavani nastaveni
                    Log.e(TAG, "Error fetching settings: ${e.message}", e)
                    null
                }
            }

            // Ak su notifikacie vypnute alebo nastavenia nie su dostupne, koncime
            if (settings == null || !settings.areNotificationsEnabled) {
                Log.d(TAG, "Notifications are disabled or settings not available")
                return Result.success()
            }

            // Inicializacia API sluzby pre komunikaciu so serverom
            val apiService = ApiService(applicationContext)

            // Ziskanie vsetkych tabuliek s ich poslednym riadkom
            val allTablesLastRow = apiService.getAllTablesLastRow()
            Log.d(TAG, "Found ${allTablesLastRow.size} tables")

            // Ak neboli najdene ziadne tabulky, koncime
            if (allTablesLastRow.isEmpty()) {
                Log.d(TAG, "No tables found, skipping check")
                return Result.success()
            }

            // Pre kazdu tabulku (ul) nacitame posledne dve merania a kontrolujeme zmeny vahy
            for (tableName in allTablesLastRow.keys) {
                Log.d(TAG, "Checking table: $tableName")
                val lastTwoMeasurements = apiService.getLastTwoMeasurements(tableName)

                // Ak nemame dostatocny pocet merani, preskakujeme
                if (lastTwoMeasurements.size < 2) {
                    Log.d(TAG, "Not enough measurements for table $tableName, skipping")
                    continue
                }

                // Kontrola zmien vahy pre dany ul
                checkWeightChanges(tableName, lastTwoMeasurements, settings)
            }

            return Result.success()
        } catch (e: Exception) {
            // Logovanie chyby pri monitorovani
            Log.e(TAG, "Error during hive monitoring: ${e.message}", e)
            return Result.failure()
        }
    }

    /**
     * Kontrola zmien vahy pre konkretny ul.
     *
     * @param tableName Nazov tabulky reprezentujucej ul
     * @param measurements Zoznam merani z daneho ula
     * @param settings Nastavenia aplikacie ovplyvnujuce prahy notifikacii
     */
    private fun checkWeightChanges(tableName: String, measurements: List<HiveDataDto>, settings: SettingsEntity) {
        try {
            // Ziskanie poslednych dvoch merani
            val newest = measurements[0].toHiveData()
            val previous = measurements[1].toHiveData()

            // Vypocet zmeny vahy
            val weightChange = newest.totalWeight - previous.totalWeight
            val absWeightChange = Math.abs(weightChange)

            Log.d(TAG, "Table $tableName: Weight change: $weightChange kg")

            // Notifikaciu posielame len ak je zmena vahy vacsia ako nastaveny prah
            if (absWeightChange >= settings.weightThresholdKg) {
                // Urcenie urovne notifikacie podla casu od poslednej notifikacie
                val notificationLevel = determineNotificationLevel(tableName, settings)

                // Odoslanie notifikacie o zmene vahy
                sendWeightChangeNotification(tableName, weightChange, notificationLevel)
            }
        } catch (e: Exception) {
            // Logovanie chyby pri kontrole zmien vahy
            Log.e(TAG, "Error checking weight changes for $tableName: ${e.message}", e)
        }
    }

    /**
     * Urcenie urovne zavaznosti notifikacie na zaklade historie a nastaveni.
     *
     * @param tableName Nazov tabulky reprezentujucej ul
     * @param settings Nastavenia aplikacie
     * @return Uroven zavaznosti notifikacie (0-2)
     */
    private fun determineNotificationLevel(tableName: String, settings: SettingsEntity): Int {
        // Ziskanie pristup k ulozeniu historie notifikacii
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()

        // Nacitanie casu poslednej notifikacie a jej urovne
        val lastNotificationTime = prefs.getLong(KEY_LAST_NOTIF_TIME + tableName, 0)
        val lastNotificationLevel = prefs.getInt(KEY_NOTIF_LEVEL + tableName, SEVERITY_INFO)

        // Vypocet uplynuleho casu od poslednej notifikacie v hodinach
        val hoursElapsed = (currentTime - lastNotificationTime) / (1000 * 60 * 60)

        // Ak je to prva notifikacia, alebo sme uz za notifikacnym intervalom,
        // zacneme od INFO urovne
        if (lastNotificationTime == 0L || hoursElapsed >= settings.notificationIntervalHours) {
            // Ulozenie aktualneho casu a urovne
            prefs.edit().apply {
                putLong(KEY_LAST_NOTIF_TIME + tableName, currentTime)
                putInt(KEY_NOTIF_LEVEL + tableName, SEVERITY_INFO)
                apply()
            }
            return SEVERITY_INFO
        }
        // Eskalacia na zaklade poslednej urovne
        else if (hoursElapsed >= settings.inactivityThresholdHours) {
            // Zvysenie zavaznosti ak sme za prahom neaktivity
            val newLevel = Math.min(lastNotificationLevel + 1, SEVERITY_ALERT)

            // Ulozenie aktualneho casu a novej urovne
            prefs.edit().apply {
                putLong(KEY_LAST_NOTIF_TIME + tableName, currentTime)
                putInt(KEY_NOTIF_LEVEL + tableName, newLevel)
                apply()
            }
            return newLevel
        }

        // V opacnom pripade ponechavame rovnaku uroven
        return lastNotificationLevel
    }

    /**
     * Odoslanie notifikacie o zmene vahy s prislusnou urovnou zavaznosti.
     *
     * @param tableName Nazov tabulky reprezentujucej ul
     * @param weightChange Zmena vahy v kg (moze byt zaporna)
     * @param notificationLevel Uroven zavaznosti notifikacie (0-2)
     */
    private fun sendWeightChangeNotification(tableName: String, weightChange: Float, notificationLevel: Int) {
        try {
            // Nastavenie detailov notifikacie podla zavaznosti
            val notificationDetails = when (notificationLevel) {
                SEVERITY_WARNING -> {
                    // Stredna uroven zavaznosti - vyrazne upozornenie
                    NotificationDetails(
                        "Výrazná zmena váhy!",
                        "Úľ $tableName: Zmena váhy o ${weightFormat.format(weightChange)} kg",
                        NotificationCompat.PRIORITY_HIGH,
                        NOTIFICATION_ID_WARNING
                    )
                }
                SEVERITY_ALERT -> {
                    // Najvyssia uroven zavaznosti - urgentne upozornenie
                    NotificationDetails(
                        "URGENTNÉ: Kritická zmena váhy!",
                        "Úľ $tableName: Zmena váhy o ${weightFormat.format(weightChange)} kg, vyžaduje okamžitú pozornosť!",
                        NotificationCompat.PRIORITY_MAX,
                        NOTIFICATION_ID_ALERT
                    )
                }
                else -> { // SEVERITY_INFO
                    // Najnizsia uroven zavaznosti - informativne upozornenie
                    NotificationDetails(
                        "Zmena váhy úľa",
                        "Úľ $tableName: Zmena váhy o ${weightFormat.format(weightChange)} kg",
                        NotificationCompat.PRIORITY_DEFAULT,
                        NOTIFICATION_ID_INFO
                    )
                }
            }

            // Vytvorenie intentu, ktory spusti hlavnu aktivitu po kliknuti na notifikaciu
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Nastavenie flagov pre PendingIntent podla verzie Androidu
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Vytvorenie PendingIntent-u pre notifikaciu
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags
            )

            // Ziskanie builderu notifikacie
            val builder = Notification.getNotificationBuilder(
                context,
                notificationDetails.title,
                notificationDetails.message
            ).apply {
                priority = notificationDetails.priority  // Nastavenie priority notifikacie
                setContentIntent(pendingIntent)          // Intent pri kliknuti
                setAutoCancel(true)                      // Notifikacia sa automaticky zrusi po kliknuti
            }

            // Odoslanie notifikacie
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationDetails.notificationId, builder.build())

            // Logovanie uspesneho odoslania notifikacie
            Log.d(TAG, "Notification sent for $tableName with level $notificationLevel")

        } catch (e: Exception) {
            // Logovanie chyby pri odosielani notifikacie
            Log.e(TAG, "Error sending notification: ${e.message}", e)
        }
    }
}
