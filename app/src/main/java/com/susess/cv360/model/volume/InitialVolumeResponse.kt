package com.susess.cv360.model.volume

import java.math.BigDecimal

data class InitialVolumeResponse(
    val valorNumerico: BigDecimal,
    val unidadDeMedida: UnitMeasurementResponse,
)
