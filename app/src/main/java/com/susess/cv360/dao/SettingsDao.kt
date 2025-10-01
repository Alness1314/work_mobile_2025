package com.susess.cv360.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.susess.cv360.model.settings.SettingsEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settingsEntity")
    fun find(): Flow<List<SettingsEntity>>

    @Query("SELECT * FROM settingsEntity WHERE id = :id")
    suspend fun findOne(id: UUID): SettingsEntity?

    @Query("SELECT * FROM settingsEntity WHERE setting_key = :key")
    suspend fun findByKey(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun save(settings: SettingsEntity)

    @Update
    suspend fun update(settings: SettingsEntity)

    @Delete
    suspend fun delete(settings: SettingsEntity)
}