package com.beesense.data.repository

import com.beesense.data.model.HiveData
import kotlinx.coroutines.delay

class HiveRepository {
    suspend fun getHiveData(): List<HiveData> {
        delay(1000)
        // test data
        return listOf(
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, humidity = .37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f,  26.5f, weightRight = null, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f,  weightRight = 40.5f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f),
            HiveData(1, "03-11-2025 21:28:13", 47f, 30.73f, 26.25f, 20.5f, 26.5f, 101.37f, 75.37f)
        )
    }
}
