plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.0"
    id("kotlin-kapt")
}

android {
    namespace = "com.beesense"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.beesense"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Enable core library desugaring
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose dependencies
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Lifecycle & navigation
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Android core libraries
    implementation(libs.androidx.core.ktx)
    implementation("org.jetbrains:annotations:23.0.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Room database
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.room.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.androidx.room.compiler)

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    // Network libraries - added for API integration
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // MPAndroidChart for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Core library desugaring for java.time support on API < 26
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}