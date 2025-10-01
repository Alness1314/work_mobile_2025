package com.susess.cv360.ui.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.model.facility.FacilityResponse
import com.susess.cv360.model.settings.SettingsEntity
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                Log.i("instalaciones cargadas viewmodel", list.size.toString())
                _facilities.postValue(list)
                _uiState.postValue(UiState.FacilitiesLoaded(list))
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
                _uiState.postValue(UiState.TanksLoaded(list))
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage?: "Error cargando tanques"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class FacilitiesLoaded(val facilities: List<FacilityResponse>) : UiState()
        data class TanksLoaded(val tanks: List<TankResponse>) : UiState()
        data class SettingCfgSaved(val settingCfg: SettingsEntity) : UiState()
        data class DefaultsCfgLoaded(val settingCfg: SettingsEntity?) : UiState()
        data class Error(val message: String) : UiState()
    }
}