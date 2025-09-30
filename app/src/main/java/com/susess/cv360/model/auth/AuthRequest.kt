package com.susess.cv360.model.auth

data class AuthRequest(
    var username: String,
    var password: String
){
    // Constructor secundario que llama al primario
    constructor() : this("", "")
}