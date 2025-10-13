package com.susess.cv360.model.events

data class EventResponse(
    var publicKey: String? = null,
    var externalKey: String? = null,
    var tipoBitacora: String? = null,
    var numeroRegistro: Int? = null,
    var fechaYHoraEvento: String? = null,
    var usuarioResponsable: String? = null,
    var tipoEvento: TypeEventResponse? = null,
    var descripcionEvento: String? = null,
    var identificacionComponenteAlarma: String? = null,
    var cabeceraOrigen: String? = null,
    var macAddress: String? = null,
    var iporigen: String? = null
)