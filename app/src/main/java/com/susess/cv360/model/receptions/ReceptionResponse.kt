package com.susess.cv360.model.receptions

import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.model.volume.InitialVolumeResponse
import com.susess.cv360.model.volume.VolumeResponse
import java.math.BigDecimal

data class ReceptionResponse(
    val tanque: TankResponse,
    val publicKey: String,
    val numeroDeRegistro: Int,
    val volumenInicialTanque: InitialVolumeResponse,
    val volumenFinalTanque: BigDecimal,
    val volumenRecepcion: VolumeResponse,
    val temperatura: BigDecimal,
    val presionAbsoluta: BigDecimal,
    val fechaYHoraInicioRecepcion: String,
    val fechaYHoraFinalRecepcion: String,
    val extra: String,
    val documents: MutableList<FileResponse>
)
