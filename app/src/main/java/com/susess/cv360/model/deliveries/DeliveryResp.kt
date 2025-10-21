package com.susess.cv360.model.deliveries

import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.VolumeRequest
import java.math.BigDecimal

class DeliveryResp (
    val tanque: TankResponse? = null,
    val publicKey: String? = null,
    val numeroDeRegistro: Int? = null,
    val volumenInicialTanque: InitialVolumeRequest? = null,
    val volumenFinalTanque: BigDecimal? = null,
    val volumenEntregado: VolumeRequest? = null,
    val temperatura: BigDecimal? = null,
    val presionAbsoluta: BigDecimal? = null,
    val fechaYHoraInicialEntrega: String? = null,
    val fechaYHoraFinalEntrega: String? = null,
    val extra: String? = null,
    val documents: MutableList<FileResponse>? = null
)