package com.susess.cv360.repository

import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
import com.susess.cv360.common.KeyFilters
import com.susess.cv360.model.about.AboutResponse
import com.susess.cv360.model.events.EventRequest
import com.susess.cv360.model.events.EventResponse
import com.susess.cv360.model.events.TypeEventResponse
import com.susess.cv360.model.facility.FacilityResponse
import com.susess.cv360.model.tank.TankResponse
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

        return response.body ?: EventResponse()
    }
}