package com.beesense.data.repository

import com.beesense.data.model.HiveData
import kotlinx.coroutines.delay

class HiveRepository {
    suspend fun getHiveData(): List<HiveData> {
        delay(1000)
        // test data
        return listOf(
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(2, "03-11-2025 22:00:00", 48f, 31.0f, 26.5f, humidity = 0.42f),
            HiveData(3, "03-11-2025 23:15:00", 45f, 29.8f, 25.5f, weightRight = null),
            HiveData(4, "03-11-2025 23:59:59", 50f, 32.0f, 28.0f, 21.0f, 27.0f, 102.0f, 78.5f),
            HiveData(5, "04-11-2025 08:00:00", 52f, 28.0f, 27.0f, 22.0f, 28.0f, 100.5f, null),
            HiveData(6, "04-11-2025 09:30:30", 47.5f, 30.0f, 26.0f, 20.0f, 26.5f, 101.0f, 76.0f),
            HiveData(7, "04-11-2025 10:45:00", 49f, 30.5f, 27.0f, 21.5f, 27.5f, 103.0f, 77.0f),
            HiveData(8, "04-11-2025 12:00:00", 46f, 29.5f, 25.5f, weightLeft = 22.0f, weightRight = 30.0f),
            HiveData(9, "04-11-2025 14:20:00", 47.2f, 30.4f, 26.7f, 20.5f, 26.7f, 101.8f, 75.5f),
            HiveData(10, "04-11-2025 16:45:00", 48.8f, 31.2f, 27.3f, 22.5f, 28.5f, 100.8f, null),
            HiveData(11, "04-11-2025 16:45:00", 48.8f, 31.2f, 27.3f, 22.5f, 28.5f, 100.8f, null)
        )
    }
}
