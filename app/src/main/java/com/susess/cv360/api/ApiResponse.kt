package com.susess.cv360.api

data class ApiResponse<T>(
    val isSuccessful: Boolean,
    val code: Int,
    val headers: Map<String, String>,
    val body: T?,
    val rawBody: String? = null
)
