package com.beesense.data.db.dao
import androidx.room.*
import com.beesense.data.db.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diary_entries ORDER BY time DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntryEntity)

    @Update
    suspend fun updateDiaryEntry(entry: DiaryEntryEntity)

    @Delete
    suspend fun deleteDiaryEntry(entry: DiaryEntryEntity)
}