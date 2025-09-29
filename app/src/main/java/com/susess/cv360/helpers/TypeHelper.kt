package com.susess.cv360.helpers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TypeHelper {
    private val gson = Gson()

    // Para un objeto simple
    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }

    // Para una lista de objetos
    fun <T> listFromJson(json: String, clazz: Class<T>): List<T> {
        val type = TypeToken.getParameterized(List::class.java, clazz).type
        return gson.fromJson(json, type)
    }
}