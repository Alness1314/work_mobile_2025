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
        return apiService.getList(
            Endpoints.FACILITIES, headers,
            mapOf(KeyFilters.FILTER_USERNAME to username), FacilityResponse::class.java
        )
    }

    suspend fun findTanksApi(
        headers: Map<String, String>,
        facilityKey: String
    ): List<TankResponse> {
        val url = String.format(Endpoints.TANKS, facilityKey)
        return apiService.getList(url, headers, clazz = TankResponse::class.java)

    }

    suspend fun findAbout(
        headers: Map<String, String>,
        facilityKey: String
    ): AboutResponse {
        val url = String.format(Endpoints.ABOUT, facilityKey)
        return apiService.get(url, headers, clazz = AboutResponse::class.java) as AboutResponse
    }

    suspend fun findEvents(headers: Map<String, String>): List<TypeEventResponse> {
        return apiService.getList(
            Endpoints.EVENT_TYPE,
            headers,
            clazz = TypeEventResponse::class.java
        )
    }

    suspend fun createEvent(
        headers: Map<String, String>,
        facilityKey: String,
        request: EventRequest
    ): EventResponse {
        val url = String.format(Endpoints.EVENT_SEND, facilityKey)
        return apiService.post(url, request,headers, EventResponse::class.java) as EventResponse
    }
}