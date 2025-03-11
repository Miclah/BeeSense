package com.beesense.app.data

data class HiveMeasurement(
    val timestamp: String,
    val temperatureSensor: Float,
    val temperatureOutside: Float?,
    val weightLeft: Float?,
    val weightRight: Float?,
    val totalWeight: Float,
    val pressure: Float?,
    val humidity: Float?
)
