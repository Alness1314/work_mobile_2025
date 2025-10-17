package com.susess.cv360.model.deliveries

import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.model.volume.InitialVolumeResponse
import com.susess.cv360.model.volume.VolumeResponse
import java.math.BigDecimal

data class DeliveryResponse(
    val tanque: TankResponse? = null,
    val publicKey: String? = null,
    val numeroDeRegistro: Int? = null,
    val volumenInicialTanque: InitialVolumeResponse? = null,
    val volumenFinalTanque: BigDecimal? = null,
    val volumenEntregado: VolumeResponse? = null,
    val temperatura: BigDecimal? = null,
    val presionAbsoluta: BigDecimal? = null,
    val fechaYHoraInicialEntrega: String? = null,
    val fechaYHoraFinalEntrega: String? = null,
    val extra: String? = null,
    val documents: MutableList<FileResponse>? = null
)
