package com.susess.cv360.ui.operations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.susess.cv360.helpers.DateTimeUtils
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.deliveries.DeliveryRequest
import com.susess.cv360.model.deliveries.DeliveryResponse
import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.receptions.ReceptionRequest
import com.susess.cv360.model.receptions.ReceptionResponse
import com.susess.cv360.model.settings.SettingsEntity
import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.VolumeRequest
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class OperationsViewModel @Inject constructor(
    private val repositoryDb: SettingsRepository,
    private val repositoryApi: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _operationsList = MutableLiveData<List<String>>()
    val operationsList: LiveData<List<String>> = _operationsList

    private val _navigationEvent = MutableLiveData<NavigationEventOps>()
    val navigationEvent: LiveData<NavigationEventOps> = _navigationEvent

    fun checkConfig() {
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            val cfg = repositoryDb.findSetting()
            if (cfg == null) {
                delay(500)
                _uiState.postValue(UiState.Error("No hay configuración disponible"))
                _navigationEvent.postValue(NavigationEventOps.ToDashboard)
            }
        }
    }

    fun loadOperationsArray() {
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            val opsList = listOf("Carga", "Descarga")
            _operationsList.postValue(opsList)
            _uiState.postValue(UiState.OperationsLoaded(opsList))
        }
    }

    fun uploadFile(filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.postValue(UiState.Loading)
                val file = File(filePath)

            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e.message.toString()))
            }
        }
    }

    fun processOperation(
        type: String,
        fechaInicio: String,
        horaInicio: String,
        fechaFin: String,
        horaFin: String,
        volumen: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.postValue(UiState.Loading)
                val cfg: SettingsEntity = repositoryDb.findSetting() ?: return@launch

                val startDate = "$fechaInicio $horaInicio"
                val endDate = "$fechaFin $horaFin"

                val volumenBD = BigDecimal(volumen)
                val temp = BigDecimal(20.0)
                val presion = BigDecimal(101.325)
                val productoId = cfg.productId
                val um = cfg.unitMeasurement
                val fechaIniFmt = DateTimeUtils.formatDateTimeToOffsetDateTime(startDate)
                val fechaFinFmt = DateTimeUtils.formatDateTimeToOffsetDateTime(endDate)

                when (type) {
                    "Carga" -> {
                        val request = ReceptionRequest().apply {
                            volumenInicialTanque = InitialVolumeRequest(BigDecimal.ZERO, um)
                            volumenFinalTanque = volumenBD
                            volumenRecepcion = VolumeRequest(volumenBD, um)
                            temperatura = temp
                            presionAbsoluta = presion
                            fechaYHoraInicioRecepcion = fechaIniFmt
                            fechaYHoraFinalRecepcion = fechaFinFmt
                            producto = productoId
                            //documents.add(fileId)
                        }
                        sendReception(request)
                    }

                    "Descarga" -> {
                        val request = DeliveryRequest().apply {
                            volumenInicialTanque = InitialVolumeRequest(volumenBD, um)
                            volumenFinalTanque = BigDecimal.ZERO
                            volumenEntregado = VolumeRequest(volumenBD, um)
                            temperatura = temp
                            presionAbsoluta = presion
                            fechaYHoraInicialEntrega = fechaIniFmt
                            fechaYHoraFinalEntrega = fechaFinFmt
                            producto = productoId
                            //documents.add(fileId)
                        }
                        sendDelivery(request)
                    }
                }
            } catch (e: Exception) {
                Log.e("processOperation", e.message.toString())
                _uiState.postValue(UiState.Error("Error al generar solicitud"))
            }
        }
    }

    private fun sendReception(req: ReceptionRequest) {
        viewModelScope.launch {
            try {
                val cfg: SettingsEntity = repositoryDb.findSetting() ?: return@launch

                val response: ReceptionResponse = repositoryApi.sendReception(
                    sessionManager.authHeaders(),
                    cfg.facilityId, cfg.tankId, req
                )
                _uiState.postValue(UiState.SendReceptionOps(response))

            } catch (e: Exception) {
                Log.e("sendReception", e.message.toString())
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error enviando recepcion"))
            }
        }
    }

    private fun sendDelivery(req: DeliveryRequest) {
        viewModelScope.launch {
            try {
                val cfg: SettingsEntity = repositoryDb.findSetting() ?: return@launch
                val response: DeliveryResponse = repositoryApi.sendDelivery(
                    sessionManager.authHeaders(),
                    cfg.facilityId, cfg.tankId, req
                )

                _uiState.postValue(UiState.SendDeliveryOps(response))

            } catch (e: Exception) {
                Log.e("sendDelivery", e.message.toString())
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error enviando entrega"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object ConfigOk : UiState()
        data class OperationsLoaded(val operations: List<String>) : UiState()
        data class FileUploaded(val fileResponse: FileResponse?) : UiState()
        data class SendReceptionOps(val receptionResponse: ReceptionResponse?) : UiState()
        data class SendDeliveryOps(val deliveryResponse: DeliveryResponse?) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class NavigationEventOps {
        object ToDashboard : NavigationEventOps()
    }
}