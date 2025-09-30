package com.susess.cv360.ui.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
import com.susess.cv360.model.auth.AuthRequest
import com.susess.cv360.model.auth.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: GenericRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    fun login(auth: AuthRequest){
        if(auth.username.isNullOrBlank()|| auth.password.isNullOrBlank()){
            _uiState.postValue(UiState.Error("Usuario y contraseña son obligatorios"))
            return
        }

        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                val authResponse: AuthResponse = repo.post(url = Endpoints.AUTH, body = auth, clazz = AuthResponse::class.java) as AuthResponse
                Log.i("Login cv360", authResponse.toString())
                _uiState.postValue(UiState.Success(authResponse.token, auth.username))
            }catch (e: Exception){
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error de conexión"))
            }
        }

    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val token: String?, val username: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}