package com.beesense.data.model

data class HiveData(
    val id: Int,
    val timestamp: String, // format "dd-MM-yyyy HH:mm:ss"
    val totalWeight: Float,
    val temperatureSensor: Float,
    val temperatureOutside: Float? = null,
    val weightLeft: Float? = null,
    val weightRight: Float? = null,
    val pressure: Float? = null,
    val humidity: Float? = null
)
