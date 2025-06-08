package com.beesense

// Pomoc s AI

/**
 * Hlavna aktivita aplikacie BeeSense.
 *
 * Tato aktivita riadi hlavny uzivatelsky tok aplikacie, zabezpecuje inicializaciu
 * komponentov, spracovava navigaciu a riadi zivotny cyklus notifikacii.
 * Je zodpovedna za pripravu WorkManager-a pre monitorovanie ulov na pozadi.
 */

// Importy pre povolenia notifikacii Android
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

// Import pre pracu so zivotnym cyklom aplikacie a logovanie
import android.os.Bundle
import android.util.Log

// Importy pre komponenty Jetpack a aktivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

// Importy pre Compose UI komponenty
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

// Importy pre prava a povolenia
import androidx.core.content.ContextCompat

// Importy pre ViewModel a Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel

// Importy pre navigaciu
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Importy pre pracu na pozadi
import androidx.work.Configuration
import androidx.work.WorkManager

// Importy pre pristup k databaze, notifikaciam a temam
import com.beesense.data.db.AppContainer
import com.beesense.notification.HiveNotificationManager
import com.beesense.notification.Notification

// Importy pre vlastne UI komponenty a obrazovky
import com.beesense.ui.components.BottomNavigationBar
import com.beesense.ui.navigation.Screen
import com.beesense.ui.screens.OverviewScreen
import com.beesense.ui.screens.GraphScreen
import com.beesense.ui.screens.MenuScreen
import com.beesense.ui.screens.subscreens.DiaryScreen
import com.beesense.ui.screens.subscreens.HiveEditorScreen
import com.beesense.ui.screens.subscreens.HiveManagementScreen
import com.beesense.ui.screens.subscreens.SQLManagementScreen
import com.beesense.ui.screens.subscreens.SettingsScreen
import com.beesense.ui.theme.BeeSenseTheme
import com.beesense.ui.theme.ThemeManager
import com.beesense.ui.theme.ThemeViewModel

// Importy pre ViewModely
import com.beesense.ui.viewmodels.HiveManagementViewModel
import com.beesense.viewmodel.SettingsViewModel

// Importy pre korutiny
import kotlinx.coroutines.launch

/**
 * Hlavna aktivita aplikacie, zodpovedna za inicializaciu a riadenie zivotneho cyklu.
 * Implementuje Configuration.Provider pre nastavenie WorkManager-a.
 */
class MainActivity : ComponentActivity(), Configuration.Provider {
    // Hlavne komponenty aplikacie
    private lateinit var appContainer: AppContainer
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var themeViewModel: ThemeViewModel
    private lateinit var hiveNotificationManager: HiveNotificationManager

    // Tag pre logovanie
    private val TAG = "MainActivity"

    // Implementacia metody z rozhrania Configuration.Provider
    // pre nastavenie konfiguracie WorkManager-a
    override val workManagerConfiguration: Configuration // Pomoc s AI
        get() {
            Log.d(TAG, "Setting up WorkManager configuration")
            return Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
        }

    // Launcher pre poziadavky na povolenie notifikacii
    // Pomoc s AI
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            // Spustenie monitorovania notifikacii ak su povolene v nastaveniach
            checkAndStartNotifications()
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    /**
     * Inicializacia aktivity a zakladnych komponentov pri vytvoreni.
     */
    // Pomoc s AI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // Inicializacia WorkManager s vlastnou konfiguraciou
        WorkManager.initialize(applicationContext, workManagerConfiguration)

        // Inicializacia kontajnera s pristupom k databaze a repozitarom
        appContainer = AppContainer(applicationContext)

        // Inicializacia ViewModelov
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]

        // Inicializacia manazera notifikacii a vytvorenie notifikacneho kanala
        hiveNotificationManager = HiveNotificationManager(applicationContext)
        Notification.createNotificationChannel(this)

        // Kontrola a ziadost o povolenie notifikacii ak je potrebne (pre Android 13+)
        checkNotificationPermission()

        // Nacitame nastavenia a aplikujeme temu pomocou lifecycleScope
        lifecycleScope.launch {
            settingsViewModel.settings.collect { settings ->
                // Nastavenie temneho rezimu podla ulozenych nastaveni
                ThemeManager.setDarkTheme(settings.isDarkMode)

                // Spustenie/zastavenie monitorovania notifikacii len ak mame potrebne povolenia
                if (areNotificationPermissionsGranted()) {
                    if (settings.areNotificationsEnabled) {
                        // Spustenie monitorovania notifikacii
                        hiveNotificationManager.startMonitoring()
                        Log.d(TAG, "Starting notification monitoring")
                    } else {
                        // Zastavenie monitorovania notifikacii
                        hiveNotificationManager.stopMonitoring()
                        Log.d(TAG, "Stopping notification monitoring")
                    }
                }
            }
        }

        // Nastavenie UI pomocou Jetpack Compose
        setContent {
            // Sledovanie aktualnej temy
            val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()

            // Aplikacia temy a hlavneho rozhrania
            BeeSenseTheme(darkTheme = isDarkTheme) {
                MainApp(appContainer)
            }
        }
    }

    /**
     * Aktivita sa obnovila a je v popredí.
     * Kontrola nastavenia notifikácií.
     */
    // Pomoc s AI
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        // Ked sa aplikacia vrati do popredia, kontrolujeme nastavenia notifikacii
        if (::hiveNotificationManager.isInitialized && ::settingsViewModel.isInitialized) {
            val settings = settingsViewModel.settings.value
            if (settings.areNotificationsEnabled && areNotificationPermissionsGranted()) {
                // Ak su notifikacie povolene, obnovujeme monitorovanie
                Log.d(TAG, "Refreshing notification monitoring in onResume")
                hiveNotificationManager.startMonitoring()
            }
        }
    }

    /**
     * Aktivita je čiastočne zakrytá inou aktivitou.
     * Pri prechode do pozadia nie je potrebné zastavovať monitorovanie.
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
        // Pri prechode do pozadia nie je potrebne zastavovat monitorovanie,
        // pretoze chceme, aby notifikacie fungovali aj na pozadí
    }

    /**
     * Aktivita už nie je viditeľná pre používateľa.
     * WorkManager pokračuje v monitorovaní úľov aj keď je aktivita zastavená.
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
        // Ked je aktivita zastavena, ale stale moze byt obnovena, nemusime nic robit
        // WorkManager bude pokracovat v planovani uloh aj ked je aplikacia zastavena
    }

    /**
     * Aktivita je kompletne zrušená.
     * WorkManager pokračuje v plánovaní úloh aj keď je aktivita zničená.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        // WorkManager bude pokracovat v planovani uloh aj ked je aktivita znicena
        // Nie je potrebne zastavovat monitorovanie, pretoze chceme, aby notifikacie
        // fungovali aj po ukonceni aplikacie
    }

    /**
     * Kontrola a pripadna ziadost o povolenie notifikacii.
     */
    // Pomoc s AI
    private fun checkNotificationPermission() {
        // Kontrola povolenia notifikacii len pre Android 13 a vyssie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Povolenie uz bolo udelene
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Mali by sme ukázať vysvetlenie, prečo potrebujeme povolenie
                    Log.d(TAG, "Should show notification permission rationale")
                    // V realnej aplikácii by sme tu mohli zobraziť vysvetľujúci dialóg
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Priama ziadost o povolenie
                    Log.d(TAG, "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    /**
     * Kontrola ci mame udelene povolenie na notifikacie.
     *
     * @return True ak mame povolenia, alebo nie su potrebne (starsia verzia Android)
     */
    // Pomoc s AI
    private fun areNotificationPermissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Od API 33 (Android 13) vyssie potrebujeme explicitne povolenie
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre Android 12 a nizsie nie je potrebne explicitne povolenie
        }
    }

    /**
     * Spustenie notifikacii ak su povolene v nastaveniach a mame potrebne povolenia.
     */
    // Pomoc s AI
    private fun checkAndStartNotifications() {
        lifecycleScope.launch {
            val settings = settingsViewModel.settings.value
            if (settings.areNotificationsEnabled) {
                hiveNotificationManager.startMonitoring()
                Log.d(TAG, "Starting notification monitoring after permission granted")
            }
        }
    }
}

/**
 * Hlavna Compose kompozicia aplikacie.
 *
 * @param appContainer Kontajner s pristupom k databaze a repozitarom
 */
@Composable
fun MainApp(appContainer: AppContainer) {
    // Vytvorenie nav controllera pre navigaciu medzi obrazovkami
    val navController = rememberNavController()

    // Definicia hlavnych poloziek spodnej navigacie
    val items = listOf(Screen.Overview, Screen.Graphs, Screen.Menu)

    // Vytvorenie HiveManagementViewModel raz pre celu aplikaciu
    val hiveManagementViewModel: HiveManagementViewModel = viewModel(
        factory = HiveManagementViewModel.Factory(appContainer.hiveRepository)
    )

    // Hlavna struktura aplikacie so spodnou navigaciou
    Scaffold(
        bottomBar = {
            // Ziskanie aktualnej navigacnej polozky
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route ?: Screen.Overview.route

            // Urcenie, ci sme na podrazenej obrazovke
            val isSubScreen = currentRoute !in listOf(
                Screen.Overview.route,
                Screen.Graphs.route,
                Screen.Menu.route
            )

            // Spodna navigacna lista
            BottomNavigationBar(
                navController = navController,
                items = items,
                currentRoute = currentRoute,
                isSubScreen = isSubScreen,
                onItemSelected = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true  // Zabrani vytvaraniu duplikatov obrazovky
                    }
                }
            )
        }
    ) { innerPadding ->
        // Navigacny host s definiciami obrazoviek
        NavHost(
            navController = navController,
            startDestination = Screen.Overview.route,  // Pociatocna obrazovka
            modifier = Modifier.padding(innerPadding)
        ) {
            // Definicie jednotlivych obrazoviek a ich navigacnych ciest

            // Hlavne obrazovky
            composable(Screen.Overview.route) { OverviewScreen() }
            composable(Screen.Graphs.route) { GraphScreen() }
            composable(Screen.Menu.route) { MenuScreen(navController) }

            // Podradene obrazovky
            composable(Screen.HiveManagement.route) {
                HiveManagementScreen(navController, hiveManagementViewModel)
            }
            composable(Screen.Diary.route) {
                DiaryScreen(navController = navController)
            }
            composable(Screen.SQLManagement.route) { SQLManagementScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            // Obrazovka editora ula s volitelnym parametrom hiveId
            // Pomoc s AI
            composable(
                route = "${Screen.HiveEditor.route}?hiveId={hiveId}",
                arguments = listOf(
                    navArgument("hiveId") {
                        type = NavType.IntType
                        defaultValue = -1  // Predvolena hodnota ak nie je specifikovane
                    }
                )
            ) { backStackEntry ->
                // Ziskanie ID ula z argumentov, null pre novy ul
                val hiveId = backStackEntry.arguments?.getInt("hiveId")?.let {
                    if (it == -1) null else it
                }
                // Spustenie editora s prislusnym ID alebo null pre novy ul
                HiveEditorScreen(
                    navController = navController,
                    hiveManagementViewModel = hiveManagementViewModel,
                    editHiveId = hiveId
                )
            }
        }
    }
}
