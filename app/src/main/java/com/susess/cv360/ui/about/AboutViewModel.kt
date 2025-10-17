package com.susess.cv360.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.about.AboutResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import com.susess.cv360.ui.events.EventsViewModel.NavigationEventAbout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    // Eventos de navegación (usar LiveData normal porque son one-shot events)
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    fun loadAbout(){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val cfg = repositoryDb.findSetting()
                if (cfg != null) {
                    val headers = sessionManager.authHeaders()
                    val about: AboutResponse = repositoryApi.findAbout(headers, cfg.facilityId)
                    _uiState.postValue(UiState.AboutLoaded(about))
                } else {
                    // No hay configuración → lanzar navegación
                    delay(500)
                    _uiState.postValue(UiState.Error("No hay configuración disponible"))
                    _navigationEvent.postValue(NavigationEvent.ToDashboard)
                    //throw Exception("No hay configuración disponible")
                }
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage?: "Error al cargar Acerca de"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class AboutLoaded(val about: AboutResponse?) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class NavigationEvent {
        object ToDashboard : NavigationEvent()
    }
}