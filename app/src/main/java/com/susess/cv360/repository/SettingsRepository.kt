package com.susess.cv360.repository

import com.susess.cv360.common.KeyFilters
import com.susess.cv360.dao.SettingsDao
import com.susess.cv360.model.settings.SettingsEntity
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    suspend fun saveSetting(setting: SettingsEntity){
        val exists = settingsDao.findByKey(KeyFilters.SETTING_KEY)
        if (exists != null) {
            setting.id = exists.id
            settingsDao.update(setting)
        } else settingsDao.save(setting)
    }

    suspend fun findSetting(): SettingsEntity? = settingsDao.findByKey(KeyFilters.SETTING_KEY)

}