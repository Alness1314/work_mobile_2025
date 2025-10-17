package com.susess.cv360.model.volume

import java.math.BigDecimal

data class InitialVolumeRequest(
    var valorNumerico: BigDecimal,
    var unidadDeMedida: String
)
