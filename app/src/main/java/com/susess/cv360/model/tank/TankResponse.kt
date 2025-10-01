package com.susess.cv360.model.tank

import com.susess.cv360.model.product.ProductResponse

data class TankResponse(val publicKey: String,
                        val externalKey: String,
                        val claveIdentificacionTanque: String,
                        val producto: ProductResponse)