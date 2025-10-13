package com.susess.cv360.model.about

import java.util.UUID

data class AboutResponse(
    val publicKey: UUID? = null,
    val name: String? = null,
    val shortName: String? = null,
    val version: String? = null,
    val copyright: String? = null,
    val serviceProvider: String? = null,
    val sha256Key: String? = null,
    val numeroLicencia: String? = null,
    val contribuyente: ContribuyenteResponse? = null,
)
