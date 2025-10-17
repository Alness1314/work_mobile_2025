package com.susess.cv360.ui.deliveries

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.deliveries.DeliveryResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import com.susess.cv360.ui.receptions.ReceptionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveriesViewModel@Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _deliveries = MutableLiveData<List<DeliveryResponse>>()
    val deliveries: LiveData<List<DeliveryResponse>> = _deliveries

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _navigationEvent = MutableLiveData<NavigationEventDeliveries>()
    val navigationEvent: LiveData<NavigationEventDeliveries> get() = _navigationEvent

    fun loadDeliveries(startDate: String, endDate: String){
        viewModelScope.launch {
            try {
                _uiState.postValue(UiState.Loading)
                val cfg = repositoryDb.findSetting()
                if (cfg != null) {
                    val response = repositoryApi.findDeliveriesApi(sessionManager.authHeaders(),
                        mapOf("from" to startDate, "to" to endDate, "order_by" to "asc"),
                        cfg.facilityId)
                    _deliveries.postValue(response)
                    _uiState.postValue(UiState.Success)
                }else{
                    delay(500)
                    _uiState.postValue(UiState.Error("No hay configuración disponible"))
                    // No hay configuración → lanzar navegación
                    _navigationEvent.postValue(NavigationEventDeliveries.ToDashboard)
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

    sealed class NavigationEventDeliveries {
        object ToDashboard : NavigationEventDeliveries()
    }
}