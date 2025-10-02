package com.susess.cv360.model.events

data class EventResponse(
    var publicKey: String,
    var externalKey: String,
    var tipoBitacora: String,
    var numeroRegistro: Int,
    var fechaYHoraEvento: String,
    var usuarioResponsable: String,
    var tipoEvento: TypeEventResponse,
    var descripcionEvento: String,
    var identificacionComponenteAlarma: String,
    var cabeceraOrigen: String,
    var macAddress: String,
    var iporigen: String
)