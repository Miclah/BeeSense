/**
 * Subor definujuci typografiu pouzivanu v aplikacii BeeSense
 * Obsahuje rozne styly textov pre rozne urovne nadpisov, titulov a textu
 */
package com.beesense.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Hlavny objekt typografie pre aplikaciu BeeSense
 * Definuje vsetky textove styly pouzivane v celej aplikacii
 */
val Typography = Typography(
    // Velke zobrazovacie texty - pouzivaju sa pre hlavne nadpisy a vyrazne texty
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,        // Tucny text pre najvyraznejsi nadpis
        fontSize = 32.sp,                    // Velka velkost pisma pre hlavne nadpisy
        lineHeight = 40.sp                   // Vyska riadku pre lepsiu citatelnost
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,        // Tucny text pre vyrazny nadpis
        fontSize = 28.sp,                    // Stredne velka velkost pisma pre vyrazne nadpisy
        lineHeight = 36.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre mensi nadpis
        fontSize = 24.sp,                    // Mensia velkost pisma pre nadpisy
        lineHeight = 32.sp
    ),

    // Nadpisove texty - pouzivaju sa pre sekcie a podsekcie
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,    // Polotucny text pre velky nadpis sekcie
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,    // Polotucny text pre stredny nadpis sekcie
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,    // Polotucny text pre maly nadpis sekcie
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),

    // Titulkove texty - pouzivaju sa pre nazvy prvkov a mensi nadpisy
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,    // Polotucny text pre velky titulok
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre stredny titulok
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre maly titulok
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    // Texty pre telo - pouzivaju sa pre hlavny obsah aplikacie
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,      // Normalny text pre velke telo
        fontSize = 16.sp,                    // Standardna velkost pre citatelny text
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,      // Normalny text pre stredne telo
        fontSize = 14.sp,                    // Mensia velkost pre bezny text
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,      // Normalny text pre male telo
        fontSize = 12.sp,                    // Mala velkost pre menej dolezite informacie
        lineHeight = 16.sp
    ),

    // Popisne texty - pouzivaju sa pre popisky a mensie prvky
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre velky popisok
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre stredny popisok
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Stredne tucny text pre maly popisok
        fontSize = 11.sp,                    // Najmensia velkost pisma pre drobne popisky
        lineHeight = 16.sp
    )
)
