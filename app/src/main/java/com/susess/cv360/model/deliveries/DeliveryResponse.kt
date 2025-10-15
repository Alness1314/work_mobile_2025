package com.susess.cv360.model.deliveries

import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.model.volume.InitialVolumeResponse
import com.susess.cv360.model.volume.VolumeResponse
import java.math.BigDecimal

data class DeliveryResponse(
    val tanque: TankResponse,
    val publicKey: String,
    val numeroDeRegistro: Int,
    val volumenInicialTanque: InitialVolumeResponse,
    val volumenFinalTanque: BigDecimal,
    val volumenEntregado: VolumeResponse,
    val temperatura: BigDecimal,
    val presionAbsoluta: BigDecimal,
    val fechaYHoraInicialEntrega: String,
    val fechaYHoraFinalEntrega: String,
    val extra: String,
    val documents: MutableList<FileResponse>
)
