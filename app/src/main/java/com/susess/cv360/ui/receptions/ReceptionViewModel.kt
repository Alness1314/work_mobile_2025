package com.susess.cv360.ui.receptions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.receptions.ReceptionResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceptionViewModel@Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _receptions = MutableLiveData<List<ReceptionResponse>>()
    val receptions: LiveData<List<ReceptionResponse>> = _receptions

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState

    private val _navigationEvent = MutableLiveData<NavigationEventReceptions>()
    val navigationEvent: LiveData<NavigationEventReceptions> get() = _navigationEvent

    fun loadReceptions(startDate: String, endDate: String){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val cfg = repositoryDb.findSetting()
                if (cfg != null) {
                    val result = repositoryApi.findReceptionsApi(sessionManager.authHeaders(),
                        mapOf("from" to startDate, "to" to endDate, "order_by" to "asc"),
                        cfg.facilityId)
                    _receptions.postValue(result)
                    _uiState.postValue(UiState.Success)
                }else{
                    // No hay configuración → lanzar navegación
                    delay(500)
                    _uiState.postValue(UiState.Error("No hay configuración disponible"))
                    _navigationEvent.postValue(NavigationEventReceptions.ToDashboard)
                }
            }catch (e: Exception){
                Log.e("api exception", "Ex:", e)
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error desconocido"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class NavigationEventReceptions {
        object ToDashboard : NavigationEventReceptions()
    }
}