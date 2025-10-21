package com.susess.cv360.repository

import android.util.Log
import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
import com.susess.cv360.common.KeyFilters
import com.susess.cv360.model.about.AboutResponse
import com.susess.cv360.model.deliveries.DeliveryRequest
import com.susess.cv360.model.deliveries.DeliveryResp
import com.susess.cv360.model.deliveries.DeliveryResponse
import com.susess.cv360.model.events.EventRequest
import com.susess.cv360.model.events.EventResponse
import com.susess.cv360.model.events.TypeEventResponse
import com.susess.cv360.model.facility.FacilityResponse
import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.receptions.ReceptionRequest
import com.susess.cv360.model.receptions.ReceptionResp
import com.susess.cv360.model.receptions.ReceptionResponse
import com.susess.cv360.model.tank.TankResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: GenericRepository
) {
    suspend fun findFacilityApi(
        headers: Map<String, String>,
        username: String
    ): List<FacilityResponse> {
        val response = apiService.getList(
            Endpoints.FACILITIES, headers,
            mapOf(KeyFilters.FILTER_USERNAME to username), FacilityResponse::class.java
        )
        return if (response.isSuccessful) {
            response.body ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun findTanksApi(
        headers: Map<String, String>,
        facilityKey: String
    ): List<TankResponse> {
        val url = String.format(Endpoints.TANKS, facilityKey)
        val response = apiService.getList(url, headers, clazz = TankResponse::class.java)
        return if (response.isSuccessful) {
            response.body ?: emptyList()
        } else {
            emptyList()
        }

    }

    suspend fun findAbout(
        headers: Map<String, String>,
        facilityKey: String
    ): AboutResponse {
        val url = String.format(Endpoints.ABOUT, facilityKey)
        val response = apiService.get<AboutResponse>(url, headers, clazz = AboutResponse::class.java)

        // VERIFICACIÓN EXPLÍCITA DE ERROR
        if (!response.isSuccessful) {
            // Para debug - imprime lo que recibiste
            Log.i("ApiRepository","ERROR API: Code=${response.code}, Body=${response.rawBody}")

            // Lanza excepción con detalles del error
            throw Exception("ERROR API: Code=${response.code}, Body=${response.rawBody?:"N/D"}")
        }

        return response.body?: AboutResponse()
    }

    suspend fun findEvents(headers: Map<String, String>): List<TypeEventResponse> {
        val response = apiService.getList<TypeEventResponse>(
            Endpoints.EVENT_TYPE,
            headers,
            clazz = TypeEventResponse::class.java
        )
        return if (response.isSuccessful) {
            response.body ?: emptyList()
        } else {
            emptyList()
        }

    }

    suspend fun createEvent(
        headers: Map<String, String>,
        facilityKey: String,
        request: EventRequest
    ): EventResponse {
        val url = String.format(Endpoints.EVENT_SEND, facilityKey)
        val response = apiService.post<EventResponse>(url, request, headers, EventResponse::class.java)

        // VERIFICACIÓN EXPLÍCITA DE ERROR
        if (!response.isSuccessful) {
            // Para debug - imprime lo que recibiste
            Log.i("ApiRepository","ERROR API: Code=${response.code}, Body=${response.rawBody}")

            // Lanza excepción con detalles del error
            throw Exception("ERROR API: Code=${response.code}, Body=${response.rawBody?:"N/D"}")
        }

        return response.body ?: EventResponse()
    }

    suspend fun findReceptionsApi(
        headers: Map<String, String>,
        queries: Map<String, String>,
        facilityKey: String
    ): List<ReceptionResponse> {
        val url = String.format(Endpoints.TANK_RECEPTIONS, facilityKey)
        val response = apiService.getList(url, queries = queries, headers = headers, clazz = ReceptionResponse::class.java)
        return if (response.isSuccessful) {
            response.body ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun findDeliveriesApi(
        headers: Map<String, String>,
        queries: Map<String, String>,
        facilityKey: String
    ): List<DeliveryResponse> {
        val url = String.format(Endpoints.TANK_DELIVERIES, facilityKey)
        val response = apiService.getList(url, queries = queries, headers = headers, clazz = DeliveryResponse::class.java)
        return if (response.isSuccessful) {
            response.body ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun sendReception(
        headers: Map<String, String>,
        facilityKey: String,
        tankKey: String,
        request: ReceptionRequest
    ): ReceptionResp {
        val url = String.format(Endpoints.TANK_SEND_RECEPTION, facilityKey, tankKey)
        val response = apiService.post<ReceptionResp>(url, request, headers, ReceptionResp::class.java)

        // VERIFICACIÓN EXPLÍCITA DE ERROR
        if (!response.isSuccessful) {
            // Para debug - imprime lo que recibiste
            Log.i("ApiRepository","ERROR API: Code=${response.code}, Body=${response.rawBody}")

            // Lanza excepción con detalles del error
            throw Exception("ERROR API: Code=${response.code}, Body=${response.rawBody?:"N/D"}")
        }

        return response.body ?: ReceptionResp()
    }

    suspend fun sendDelivery(
        headers: Map<String, String>,
        facilityKey: String,
        tankKey: String,
        request: DeliveryRequest
    ): DeliveryResp{
        val url = String.format(Endpoints.TANK_SEND_DELIVERY, facilityKey, tankKey)
        val response = apiService.post<DeliveryResp>(url, request, headers, DeliveryResp::class.java)

        // VERIFICACIÓN EXPLÍCITA DE ERROR
        if (!response.isSuccessful) {
            // Para debug - imprime lo que recibiste
            Log.i("ApiRepository","ERROR API: Code=${response.code}, Body=${response.rawBody}")

            // Lanza excepción con detalles del error
            throw Exception("ERROR API: Code=${response.code}, Body=${response.rawBody?:"N/D"}")
        }

        return response.body ?: DeliveryResp()
    }

    suspend fun uploadFile(
        headers: Map<String, String>,
        path: String
    ): FileResponse {
        // 1️⃣ Preparar archivo y multipart
        val file = File(path)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val nameRequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file.name)
        val namePart = MultipartBody.Part.createFormData("name", null, nameRequestBody)

        // 2️⃣ Construir lista de partes
        val parts = listOf(namePart, filePart)

        // 3️⃣ Endpoint
        val url = Endpoints.FILE_SEND // asegúrate de definirlo en tu objeto Endpoints

        // 4️⃣ Llamar al repositorio genérico
        val response = apiService.postMultipart<FileResponse>(
            url = url,
            parts = parts,
            headers = headers,
            clazz = FileResponse::class.java
        )

        // 5️⃣ Manejo de respuesta
        if (!response.isSuccessful) {
            Log.e("ApiRepository", "ERROR FILE UPLOAD: Code=${response.code}, Body=${response.rawBody}")
            throw Exception("Error al subir archivo: ${response.rawBody ?: "Sin detalle"}")
        }

        return response.body ?: FileResponse()
    }


}