package com.susess.cv360.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.common.KeyFilters
import com.susess.cv360.model.facility.FacilityResponse
import com.susess.cv360.model.settings.SettingsEntity
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    private val _facilities = MutableLiveData<List<FacilityResponse>>()
    val facilities: LiveData<List<FacilityResponse>> = _facilities

    private val _tanks = MutableLiveData<List<TankResponse>>()
    val tanks: LiveData<List<TankResponse>> = _tanks

    fun loadFacilities(headers: Map<String, String>, username: String){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val list = repositoryApi.findFacilityApi(headers, username)
                _facilities.postValue(list)
                _uiState.postValue(UiState.Idle)
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage?: "Error cargando instalaciones"))
            }
        }
    }

    fun loadTanks(headers: Map<String, String>, facilty: String){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val list = repositoryApi.findTanksApi(headers, facilty)
                _tanks.postValue(list)
                _uiState.postValue(UiState.Idle)
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage?: "Error cargando tanques"))
            }
        }
    }

    fun saveSettings(facility: FacilityResponse, tank: TankResponse) {
        viewModelScope.launch {
            try {
                val entity = SettingsEntity(
                    id = UUID.randomUUID(),
                    settingKey = KeyFilters.SETTING_KEY,
                    facilityId = facility.publicKey,
                    facilityName = facility.externalKey,
                    tankId = tank.publicKey,
                    tankName = tank.externalKey,
                    productName = tank.producto.marcaComercial,
                    productId = tank.producto.publicKey,
                    productKey = tank.producto.claveSubProducto,
                    unitMeasurement = tank.producto.unidadMedida
                )
                repositoryDb.saveSetting(entity)
                _uiState.postValue(UiState.SettingCfgSaved(entity))
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error guardando configuración"))
            }
        }
    }

    fun loadDefaults() {
        viewModelScope.launch {
            val cfg = repositoryDb.findSetting()
            if(cfg != null)
                _uiState.postValue(UiState.DefaultsCfgLoaded(cfg))
            else
                _uiState.postValue(UiState.Error("No hay valores por defecto."))
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class SettingCfgSaved(val settingCfg: SettingsEntity) : UiState()
        data class DefaultsCfgLoaded(val settingCfg: SettingsEntity?) : UiState()
        data class Error(val message: String) : UiState()
    }
}