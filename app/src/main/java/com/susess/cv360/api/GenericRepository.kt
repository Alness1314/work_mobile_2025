package com.susess.cv360.api

import com.google.gson.Gson
import com.susess.cv360.helpers.TypeHelper
import okhttp3.MultipartBody
import javax.inject.Inject

class GenericRepository @Inject constructor(
    private val api: GenericApi,
    private val gson: Gson
){
    suspend fun <T> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        queries: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,       // para objetos simples
        listType: Boolean = false      // si esperamos lista
    ): ApiResponse<T?> {
        val resp = api.get(url, headers, queries)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> post(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): ApiResponse<T?> {
        val resp = api.post(url, headers, body)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> put(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): ApiResponse<T?> {
        val resp = api.put(url, headers, body)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> delete(
        url: String,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): ApiResponse<T?> {
        val resp = api.delete(url, headers)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> postMultipart(
        url: String,
        parts: List<MultipartBody.Part>,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): ApiResponse<T?> {
        val resp = api.postMultipart(url, headers, parts)
        return handleResponse(resp, clazz, listType)
    }

    private fun <T> handleResponse(
        resp: retrofit2.Response<okhttp3.ResponseBody>,
        clazz: Class<T>?,
        listType: Boolean
    ): ApiResponse<T?> {
        // Extraer headers - FORMA CORREGIDA
        val responseHeaders = mutableMapOf<String, String>()
        resp.headers().names().forEach { headerName ->
            val headerValue = resp.headers()[headerName]
            if (headerValue != null) {
                responseHeaders[headerName] = headerValue
            }
        }

        val bodyStr = resp.body()?.string() ?: ""

        return if (resp.isSuccessful) {
            try {
                val parsedBody = when {
                    clazz != null && listType -> {
                        // Para listas
                        TypeHelper.listFromJson(bodyStr, clazz) as? T
                    }
                    clazz != null -> {
                        // Para objetos individuales
                        TypeHelper.fromJson(bodyStr, clazz)
                    }
                    else -> {
                        // Para respuestas sin mapeo específico
                        bodyStr as? T
                    }
                }

                ApiResponse(
                    isSuccessful = true,
                    code = resp.code(),
                    headers = responseHeaders,
                    body = parsedBody,
                    rawBody = bodyStr
                )
            } catch (e: Exception) {
                // En caso de error en el parsing
                ApiResponse(
                    isSuccessful = false,
                    code = resp.code(),
                    headers = responseHeaders,
                    body = null,
                    rawBody = bodyStr
                )
            }
        } else {
            // Respuesta no exitosa
            ApiResponse(
                isSuccessful = false,
                code = resp.code(),
                headers = responseHeaders,
                body = null,
                rawBody = bodyStr
            )
        }
    }

    // Método específico para listas (manteniendo compatibilidad)
    suspend fun <T> getList(
        url: String,
        headers: Map<String, String> = emptyMap(),
        queries: Map<String, String> = emptyMap(),
        clazz: Class<T>
    ): ApiResponse<List<T>> {
        val resp = api.get(url, headers, queries)
        return handleResponse(resp, clazz, true) as ApiResponse<List<T>>
    }




    /*private fun <T> handleResponse(
        resp: retrofit2.Response<okhttp3.ResponseBody>,
        clazz: Class<T>?,
        listType: Boolean
    ): Any? {
        if (resp.isSuccessful) {
            val bodyStr = resp.body()?.string() ?: return null
            return when {
                clazz != null -> TypeHelper.fromJson(bodyStr, clazz)
                listType -> throw IllegalArgumentException("List type requires clazz parameter")
                else -> bodyStr
            }
        } else {
            throw Exception("${resp.code()}: ${resp.errorBody()?.string()}")
        }
    }

    // Método para listas
    suspend fun <T> getList(
        url: String,
        headers: Map<String, String> = emptyMap(),
        queries: Map<String, String> = emptyMap(),
        clazz: Class<T>
    ): List<T> {
        val resp = api.get(url, headers, queries)
        if (resp.isSuccessful) {
            val bodyStr = resp.body()?.string() ?: return emptyList()
            return TypeHelper.listFromJson(bodyStr, clazz)
        } else {
            throw Exception("${resp.code()}: ${resp.errorBody()?.string()}")
        }
    }*/
}