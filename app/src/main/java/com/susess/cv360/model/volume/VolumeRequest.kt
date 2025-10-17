package com.susess.cv360.model.volume

import java.math.BigDecimal

data class VolumeRequest(
    var valorNumerico: BigDecimal,
    var unidadDeMedida: String
)
