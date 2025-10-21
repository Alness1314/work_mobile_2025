package com.susess.cv360.model.receptions

import com.susess.cv360.model.files.FileResponse
import com.susess.cv360.model.tank.TankResponse
import com.susess.cv360.model.volume.InitialVolumeRequest
import com.susess.cv360.model.volume.InitialVolumeResponse
import com.susess.cv360.model.volume.VolumeRequest
import com.susess.cv360.model.volume.VolumeResponse
import java.math.BigDecimal

class ReceptionResp (
    val tanque: TankResponse? = null,
    val publicKey: String? = null,
    val numeroDeRegistro: Int? = null,
    val volumenInicialTanque: InitialVolumeRequest? = null,
    val volumenFinalTanque: BigDecimal? = null,
    val volumenRecepcion: VolumeRequest? = null,
    val temperatura: BigDecimal? = null,
    val presionAbsoluta: BigDecimal? = null,
    val fechaYHoraInicioRecepcion: String? = null,
    val fechaYHoraFinalRecepcion: String? = null,
    val producto: String? = null,
    val extra: String? = null,
    val documents: MutableList<FileResponse>? = null
)