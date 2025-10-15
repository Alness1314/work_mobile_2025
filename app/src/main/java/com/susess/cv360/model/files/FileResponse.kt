package com.susess.cv360.model.files

data class FileResponse(
    val extension: String,
    val publicKey: String,
    val mimeType: String,
    val name: String,
    val url: String
)
