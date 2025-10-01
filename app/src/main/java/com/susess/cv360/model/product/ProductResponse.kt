package com.susess.cv360.model.product

data class ProductResponse(
    val publicKey: String,
    val marcaComercial: String,
    val claveProducto: String,
    val claveSubProducto: String,
    val unidadMedida: String,
)
