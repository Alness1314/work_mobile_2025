package com.susess.cv360.ui.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.helpers.ValidateFieldsHelper.validateField
import com.susess.cv360.model.events.EventRequest
import com.susess.cv360.model.events.EventResponse
import com.susess.cv360.model.events.TypeEventResponse
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import com.susess.cv360.ui.about.AboutViewModel
import com.susess.cv360.validations.ValidationResult
import com.susess.cv360.validations.ValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel@Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    private val _events = MutableLiveData<List<TypeEventResponse>>()
    val eventLive: LiveData<List<TypeEventResponse>> = _events

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    private val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String> = _currentUser

    private val _navigationEvent = MutableLiveData<NavigationEventAbout>()
    val navigationEvent: LiveData<NavigationEventAbout> get() = _navigationEvent


    fun loadTypeEvents(){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val result = repositoryApi.findEvents(sessionManager.authHeaders())
                _events.postValue(result)
                _uiState.postValue(UiState.EventsLoaded(result))
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error("Error cargando eventos"))
            }
        }
    }

    fun checkConfig(){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            val cfg = repositoryDb.findSetting()
            if (cfg == null) {
                delay(500)
                _uiState.postValue(UiState.Error("No hay configuración disponible"))
                _navigationEvent.postValue(NavigationEventAbout.ToDashboard)
            }
        }
    }

    fun getCurrentUser(){
        viewModelScope.launch {
            // No necesitas Loading state para esto
            Log.i("EventsViewModel", "getCurrentUser: ${sessionManager.username}")
            val user = sessionManager.username ?: "Sistema"
            _currentUser.postValue(user)
        }
    }

    fun sendEvent(request: EventRequest){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val cfg = repositoryDb.findSetting()
                if (cfg != null) {
                    val response: EventResponse = repositoryApi.createEvent(sessionManager.authHeaders(), cfg.facilityId, request)
                    _uiState.postValue(UiState.SendEventApi(response))
                    delay(500)
                    _navigationEvent.postValue(NavigationEventAbout.ToDashboard)
                }else{
                    // No hay configuración → lanzar navegación
                    _navigationEvent.postValue(NavigationEventAbout.ToDashboard)
                }
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error enviando bitácora"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class EventsLoaded(val listEvents: List<TypeEventResponse>) : UiState()
        data class SendEventApi(val eventResponse: EventResponse?) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class NavigationEventAbout {
        object ToDashboard : NavigationEventAbout()
    }

    fun validateFieldEvents(field: String, value: String){
        val validation = when (field) {
            "description" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "date" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "time" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "component" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "typeEvent" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            else -> ValidationResult.Valid // si el campo no está definido
        }

        val currentState = _formState.value ?: FormState()

        val newState = when (field) {
            "description" -> currentState.copy(
                descriptionResult = validation,
                isFormValid = validation is ValidationResult.Valid &&
                        currentState.dateResult is ValidationResult.Valid &&
                currentState.timeResult is ValidationResult.Valid &&
                        currentState.typeEventResult is ValidationResult.Valid &&
                currentState.componentResult is ValidationResult.Valid
            )
            "date" -> currentState.copy(
                dateResult = validation, // ← CORREGIDO: era dateResult para todos
                isFormValid = currentState.descriptionResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.timeResult is ValidationResult.Valid &&
                        currentState.typeEventResult is ValidationResult.Valid &&
                        currentState.componentResult is ValidationResult.Valid
            )
            "time" -> currentState.copy(
                timeResult = validation, // ← CORREGIDO: ahora es timeResult
                isFormValid = currentState.descriptionResult is ValidationResult.Valid &&
                        currentState.dateResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.typeEventResult is ValidationResult.Valid &&
                        currentState.componentResult is ValidationResult.Valid
            )
            "component" -> currentState.copy(
                componentResult = validation, // ← CORREGIDO: ahora es componentResult
                isFormValid = currentState.descriptionResult is ValidationResult.Valid &&
                        currentState.dateResult is ValidationResult.Valid &&
                        currentState.timeResult is ValidationResult.Valid &&
                        currentState.typeEventResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid
            )
            "typeEvent" -> currentState.copy(
                typeEventResult = validation, // ← CORREGIDO: ahora es componentResult
                isFormValid = currentState.descriptionResult is ValidationResult.Valid &&
                        currentState.dateResult is ValidationResult.Valid &&
                        currentState.timeResult is ValidationResult.Valid &&
                        currentState.componentResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid
            )
            else -> currentState
        }

        _formState.value = newState
    }

}

data class FormState(
    val descriptionResult: ValidationResult = ValidationResult.Valid,
    val dateResult: ValidationResult = ValidationResult.Valid,
    val timeResult: ValidationResult = ValidationResult.Valid,
    val componentResult: ValidationResult = ValidationResult.Valid,
    val typeEventResult: ValidationResult = ValidationResult.Valid,
    val isFormValid: Boolean = false
)

