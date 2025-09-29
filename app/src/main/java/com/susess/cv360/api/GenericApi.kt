package com.susess.cv360.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.QueryMap
import retrofit2.http.HeaderMap
import retrofit2.http.GET
import retrofit2.http.Url

interface GenericApi {
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap(encoded = true) queries: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Body body: Any? = null
    ): Response<ResponseBody>

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Body body: Any? = null
    ): Response<ResponseBody>

    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

    @Multipart
    @POST
    suspend fun postMultipart(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Part parts: List<MultipartBody.Part>
    ): Response<ResponseBody>
}