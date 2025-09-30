package com.susess.cv360.model.auth

data class Session(
    val token: String?,
    val username: String?,
    val isLoggedIn: Boolean
)
