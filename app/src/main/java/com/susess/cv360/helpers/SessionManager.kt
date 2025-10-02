package com.susess.cv360.helpers

import com.susess.cv360.common.KeyFilters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    var token: String? = null
    var username: String? = null
    var isLoggedIn: Boolean = false

    fun clear() {
        token = null
        username = null
        isLoggedIn = false
    }

    fun authHeaders() = mapOf(KeyFilters.HEADER_AUTH to "Bearer ${token}")

    override fun toString(): String {
        return StringBuilder().append("token=${token} ")
            .append("username=${username} ")
            .append("session=${isLoggedIn} ")
            .toString()
    }
}