package com.susess.cv360.ui.operations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomOpenDelegate
import com.susess.cv360.helpers.DateTimeUtils
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.helpers.ValidateFieldsHelper.validateField
import com.susess.cv360.model.deliveries.DeliveryRequest
import com.susess.cv360.model.deliveries.DeliveryResp
import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.receptions.ReceptionRequest
import com.susess.cv360.model.receptions.ReceptionResp
import com.susess.cv360.model.settings.SettingsEntity
import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.VolumeRequest
import com.susess.cv360.repository.ApiRepository
import com.susess.cv360.repository.SettingsRepository
import com.susess.cv360.validations.ValidationResult
import com.susess.cv360.validations.ValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private val _fileResponse = MutableLiveData<FileResponse>()
    val fileResponse: LiveData<FileResponse> = _fileResponse

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    private var idFile: String? = null

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
                //val file = File(filePath)

                Log.i("uploadFile", "Uploading file: $filePath")

                val response = repositoryApi.uploadFile(sessionManager.authHeaders(),filePath)
                idFile = response.publicKey
                _fileResponse.postValue(response)
                _uiState.postValue(UiState.FileUploaded(response))

            } catch (e: Exception) {
                Log.e("uploadFile", e.message.toString())
                _uiState.postValue(UiState.Error(e.localizedMessage?: "Error enviando archivo"))
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
                            volumenFinalTanque = BigDecimal.ZERO
                            volumenRecepcion = VolumeRequest(volumenBD, um)
                            temperatura = temp
                            presionAbsoluta = presion
                            fechaYHoraInicioRecepcion = fechaIniFmt
                            fechaYHoraFinalRecepcion = fechaFinFmt
                            producto = productoId
                            documents.add(idFile!!)
                        }
                        sendReception(request)
                    }

                    "Descarga" -> {
                        val request = DeliveryRequest().apply {
                            volumenInicialTanque = InitialVolumeRequest(BigDecimal.ZERO, um)
                            volumenFinalTanque = BigDecimal.ZERO
                            volumenEntregado = VolumeRequest(volumenBD, um)
                            temperatura = temp
                            presionAbsoluta = presion
                            fechaYHoraInicialEntrega = fechaIniFmt
                            fechaYHoraFinalEntrega = fechaFinFmt
                            producto = productoId
                            documents.add(idFile!!)
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

                val response: ReceptionResp = repositoryApi.sendReception(
                    sessionManager.authHeaders(),
                    cfg.facilityId, cfg.tankId, req
                )
                _uiState.postValue(UiState.SendReceptionOps(response))
                waitAndRedirect()

            } catch (e: Exception) {
                Log.e("sendReception", e.message.toString())
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error enviando recepcion"))
            }
        }
    }

    private suspend fun waitAndRedirect() {
        delay(500)
        _navigationEvent.postValue(NavigationEventOps.ToDashboard)
    }


    private fun sendDelivery(req: DeliveryRequest) {
        viewModelScope.launch {
            try {
                val cfg: SettingsEntity = repositoryDb.findSetting() ?: return@launch
                val response: DeliveryResp = repositoryApi.sendDelivery(
                    sessionManager.authHeaders(),
                    cfg.facilityId, cfg.tankId, req
                )

                _uiState.postValue(UiState.SendDeliveryOps(response))
                waitAndRedirect()

            } catch (e: Exception) {
                Log.e("sendDelivery", e.message.toString())
                _uiState.postValue(UiState.Error(e.localizedMessage ?: "Error enviando entrega"))
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class OperationsLoaded(val operations: List<String>) : UiState()
        data class FileUploaded(val fileResponse: FileResponse?) : UiState()
        data class SendReceptionOps(val receptionResponse: ReceptionResp?) : UiState()
        data class SendDeliveryOps(val deliveryResponse: DeliveryResp?) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class NavigationEventOps {
        object ToDashboard : NavigationEventOps()
    }

    fun validateFieldEvents(field: String, value: String){
        val validation = when (field) {
            "operation" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "volume" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "dateStart" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "dateEnd" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "timeStart" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "timeEnd" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            "file" -> validateField(
                value,
                listOf(ValidationRules.NOT_EMPTY)
            )
            else -> ValidationResult.Valid
        }

        val currentState = _formState.value ?: FormState()

        val newState = when (field) {
            "operation" -> currentState.copy(
                operationResult = validation,
                isFormValid = validation is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "volume" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "dateStart" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "dateEnd" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "timeStart" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "timeEnd" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid &&
                        currentState.fileResult is ValidationResult.Valid
            )
            "file" -> currentState.copy(
                operationResult = validation,
                isFormValid = currentState.operationResult is ValidationResult.Valid &&
                        currentState.volumenResult is ValidationResult.Valid &&
                        currentState.dateStartResult is ValidationResult.Valid &&
                        currentState.dateEndResult is ValidationResult.Valid &&
                        currentState.timeStartResult is ValidationResult.Valid &&
                        currentState.timeEndResult is ValidationResult.Valid &&
                        validation is ValidationResult.Valid
            )


            else -> currentState
        }

        _formState.value = newState
    }

}

data class FormState(
    val operationResult: ValidationResult = ValidationResult.Valid,
    val volumenResult: ValidationResult = ValidationResult.Valid,

    val dateStartResult: ValidationResult = ValidationResult.Valid,
    val dateEndResult: ValidationResult = ValidationResult.Valid,
    val timeStartResult: ValidationResult = ValidationResult.Valid,
    val timeEndResult: ValidationResult = ValidationResult.Valid,

    val fileResult: ValidationResult = ValidationResult.Valid,

    val isFormValid: Boolean = false
)