package com.susess.cv360.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
import com.susess.cv360.helpers.ValidateFieldsHelper.validateField
import com.susess.cv360.model.auth.AuthRequest
import com.susess.cv360.model.auth.AuthResponse
import com.susess.cv360.validations.ValidationResult
import com.susess.cv360.validations.ValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: GenericRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun login(auth: AuthRequest){
        if(auth.username.isNullOrBlank()|| auth.password.isNullOrBlank()){
            _uiState.postValue(UiState.Error("Usuario y contraseña son obligatorios"))
            return
        }

        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val response = repo.post(url = Endpoints.AUTH, body = auth, clazz = AuthResponse::class.java)
                if(response.isSuccessful){
                    val authHeader = response.headers["Authorization"]
                        ?: response.headers["authorization"] // por si viene en minúsculas

                    val token = authHeader?.replace("Bearer ", "") ?: ""
                    // También puedes obtener el body si viene en el cuerpo de la respuesta
                    val authResponseBody = response.body

                    // Usar el token del header o del body según tu API
                    val finalToken = token.ifBlank {
                        authResponseBody?.token ?: ""
                    }

                    if (finalToken.isNotBlank()) {
                        _uiState.postValue(UiState.Success(finalToken, auth.username))
                    } else {
                        _uiState.postValue(UiState.Error("No se recibió token de autenticación"))
                    }
                }else{
                    _uiState.postValue(UiState.Error("Error ${response.code}: ${response.rawBody}"))
                }
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error de conexión"))
            }
        }

    }

    fun validateFieldLogin(field: String, value: String){
        val validation = when (field) {
            "username" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY, ValidationRules.VALID_EMAIL)
            )
            "password" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY, ValidationRules.SECURE_PASSWORD)
            )
            else -> ValidationResult.Valid // si el campo no está definido
        }

        val currentState = _formState.value ?: FormState()

        val newState = when (field) {
            "username" -> currentState.copy(
                usernameResult = validation,
                isFormValid = validation is ValidationResult.Valid &&
                        currentState.passwordResult is ValidationResult.Valid
            )
            "password" -> currentState.copy(
                passwordResult = validation,
                isFormValid = currentState.usernameResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid
            )
            else -> currentState
        }

        _formState.value = newState
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val token: String?, val username: String) : UiState()
        data class Error(val message: String) : UiState()
    }

}

data class FormState(
    val usernameResult: ValidationResult = ValidationResult.Valid,
    val passwordResult: ValidationResult = ValidationResult.Valid,
    val isFormValid: Boolean = false
)