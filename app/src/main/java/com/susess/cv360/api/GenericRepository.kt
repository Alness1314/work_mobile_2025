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
    ): Any? {
        val resp = api.get(url, headers, queries)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> post(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): Any? {
        val resp = api.post(url, headers, body)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun <T> put(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): Any? {
        val resp = api.put(url, headers, body)
        return handleResponse(resp, clazz, listType)
    }

    suspend fun delete(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): Boolean {
        val resp = api.delete(url, headers)
        return resp.isSuccessful
    }

    suspend fun <T> postMultipart(
        url: String,
        parts: List<MultipartBody.Part>,
        headers: Map<String, String> = emptyMap(),
        clazz: Class<T>? = null,
        listType: Boolean = false
    ): Any? {
        val resp = api.postMultipart(url, headers, parts)
        return handleResponse(resp, clazz, listType)
    }

    private fun <T> handleResponse(
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
            throw Exception("Error ${resp.code()}: ${resp.errorBody()?.string()}")
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
            throw Exception("Error ${resp.code()}: ${resp.errorBody()?.string()}")
        }
    }
}