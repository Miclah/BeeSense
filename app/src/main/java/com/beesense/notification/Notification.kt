package com.beesense.notification

/**
 * Utilitna trieda pre pracu s notifikaciami v aplikacii.
 *
 * Poskytuje metody pre vytvaranie notifikacneho kanala a
 * poskytovania zakladnych notifikacnych builderov s predvolenym formatovanim.
 */

// Pomoc s AI

// Importy pre notifikacie a Android system
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

// Import pre logovanie
import android.util.Log

// Import pre notifikacie v AndroidX
import androidx.core.app.NotificationCompat

// Import pre ikony/zdroje
import com.beesense.R

/**
 * Utilitna trieda pre pracu s notifikaciami.
 *
 * Umoznuje jednoduchu pracu s notifikaciami, zabezpecuje kompatibilitu
 * s rozlicnymi verziami Androidu a poskytuje predvolene formatovanie.
 */
class Notification {
    companion object {
        // Tag pre logovanie, urychluje filtrovanie v logoch
        private const val TAG = "Notification"

        // Konstanty pre identifikaciu a popis notifikacneho kanala
        private const val CHANNEL_ID = "beesense_channel"
        private const val CHANNEL_NAME = "BeeSense Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifikácie o stave včelích úľov"

        /**
         * Vytvori notifikacny kanal pre aplikaciu.
         *
         * Notifikacne kanaly su vyzadovane od Androidu 8 (Oreo) vyssie,
         * a umoznuju pouzivatelom kontrolovat, ake typy notifikacii budu dostavat.
         *
         * @param context Kontext aplikacie potrebny pre pristup k systemovym sluzbam
         */
        fun createNotificationChannel(context: Context) {
            // Kontrola ci je verzia Android 8 (API 26) alebo vyssia
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Nastavenie vysokej dolezitosti pre notifikacie
                val importance = NotificationManager.IMPORTANCE_HIGH

                // Vytvorenie notifikacneho kanala s nastaveniami
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESCRIPTION
                }

                // Registracia kanala v systeme
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                // Logovanie uspesneho vytvorenia kanala
                Log.d(TAG, "Notification channel created")
            }
        }

        /**
         * Poskytuje predvoleny builder pre notifikacie.
         *
         * Tato pomocna metoda vytvara predpripraveny builder s nastavenymi
         * standardnymi prvkami ako ikona, nadpis, obsah a priorita.
         *
         * @param context Kontext aplikacie
         * @param title Nadpis notifikacie
         * @param content Obsah/text notifikacie
         * @return Predpripraveny builder pre notifikaciu, ktory je mozne dalej upravit
         */
        fun getNotificationBuilder(context: Context, title: String, content: String): NotificationCompat.Builder {
            // Vytvorenie a vratenie notifikacneho buildera s predvolenymi nastaveniami
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Mala ikona v notifikacnej liste
                .setContentTitle(title)                    // Nadpis notifikacie
                .setContentText(content)                   // Obsah/text notifikacie
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Predvolena priorita
        }
    }
}
