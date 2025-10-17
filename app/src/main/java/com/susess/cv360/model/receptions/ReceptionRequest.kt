package com.susess.cv360.model.receptions

import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.VolumeRequest
import java.math.BigDecimal

data class ReceptionRequest(
    var volumenInicialTanque: InitialVolumeRequest,
    var volumenFinalTanque: BigDecimal,
    var volumenRecepcion: VolumeRequest,
    var temperatura: BigDecimal,
    var presionAbsoluta: BigDecimal,
    var fechaYHoraInicioRecepcion: String,
    var fechaYHoraFinalRecepcion: String,
    var producto: String,
    var documents: MutableList<String>
){
    constructor(): this(
        InitialVolumeRequest(BigDecimal.ZERO, ""),
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

