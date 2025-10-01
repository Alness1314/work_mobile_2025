package com.susess.cv360.repository

import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
import com.susess.cv360.common.KeyFilters
import com.susess.cv360.dao.SettingsDao
import com.susess.cv360.model.facility.FacilityResponse
import com.susess.cv360.model.settings.SettingsEntity
import com.susess.cv360.model.tank.TankResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    fun getAllSettings(): Flow<List<SettingsEntity>> = settingsDao.find()

    suspend fun getById(id: java.util.UUID) = settingsDao.findOne(id)

    suspend fun getByKey(key: String) = settingsDao.findByKey(key)

    suspend fun insert(setting: SettingsEntity) = settingsDao.save(setting)

    suspend fun update(setting: SettingsEntity) = settingsDao.update(setting)

    suspend fun delete(setting: SettingsEntity) = settingsDao.delete(setting)

    suspend fun saveSetting(setting: SettingsEntity){
        val exists = settingsDao.findByKey(KeyFilters.SETTING_KEY)
        if (exists != null) settingsDao.update(setting) else settingsDao.save(setting)
    }

    suspend fun findSetting(): SettingsEntity? = settingsDao.findByKey(KeyFilters.SETTING_KEY)

}