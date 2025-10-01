package com.susess.cv360.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.susess.cv360.dao.SettingsDao
import com.susess.cv360.model.settings.SettingsEntity

@Database(entities = [SettingsEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}