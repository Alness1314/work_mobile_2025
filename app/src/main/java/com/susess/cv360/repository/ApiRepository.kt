package com.susess.cv360.repository

import com.susess.cv360.api.GenericRepository
import com.susess.cv360.common.Endpoints
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
            mapOf(Endpoints.FILTER_USERNAME to username), FacilityResponse::class.java
        )
    }

    suspend fun findTanksApi(
        headers: Map<String, String>,
        facilityKey: String
    ): List<TankResponse> {
        val url = String.format(Endpoints.TANKS, facilityKey)
        return apiService.getList(url, headers, clazz = TankResponse::class.java)

    }
}