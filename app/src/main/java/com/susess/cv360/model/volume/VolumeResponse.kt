package com.susess.cv360.model.volume

import java.math.BigDecimal

data class VolumeResponse (
    val valorNumerico: BigDecimal,
    val unidadDeMedida: UnitMeasurementResponse
)