package com.susess.cv360.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.model.module.ModuleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    //private val repo: GenericRepository
): ViewModel() {

    private val _modules = MutableLiveData<List<ModuleResponse>>()
    val modules: LiveData<List<ModuleResponse>> = _modules

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState

    fun loadModules(){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            try {
                //llamada desde retrofit
                //val list = repo.getList("/v1/api/modules", queries = mapOf("level" to "home"), clazz = ModuleResponse::class.java)
                _modules.postValue(mutableListOf<ModuleResponse>(ModuleResponse(1,"Recepciones", "receptionsFragment", "ic_reception"),
                    ModuleResponse(2,"Entregas", "deliveriesFragment", "ic_delivery"),
                    ModuleResponse(3,"Transacciones", "opsVolumetricFragment", "ic_ops_vol"),
                    ModuleResponse(4,"Alarmas y Eventos", "eventFragment", "ic_events")))
                _uiState.postValue(UiState.Success)

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
}