package com.susess.cv360.model.deliveries

import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.VolumeRequest
import java.math.BigDecimal

data class DeliveryRequest(
    var volumenInicialTanque: InitialVolumeRequest,
    var volumenFinalTanque: BigDecimal,
    var volumenEntregado: VolumeRequest,
    var temperatura: BigDecimal,
    var presionAbsoluta: BigDecimal,
    var fechaYHoraInicialEntrega: String,
    var fechaYHoraFinalEntrega: String,
    var producto: String,
    var documents: MutableList<String>
){
    constructor() : this(InitialVolumeRequest(BigDecimal.ZERO, ""),
        BigDecimal.ZERO,
        VolumeRequest(BigDecimal.ZERO, ""),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        "",
        "",
        "",
        mutableListOf()
    )
}

