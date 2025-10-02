package com.susess.cv360.model.about

import java.util.UUID

data class AboutResponse(
    val publicKey: UUID,
    val name: String,
    val shortName: String,
    val version: String,
    val copyright: String,
    val serviceProvider: String,
    val sha256Key: String,
    val numeroLicencia: String,
    val contribuyente: ContribuyenteResponse,
)
