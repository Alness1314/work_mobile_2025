package com.susess.cv360.model.events

data class EventRequest(
    var fechaYHoraEvento: String,
    var usuarioResponsable: String,
    var tipoEvento: Int,
    var descripcionEvento: String,
    var identificacionComponenteAlarma: String,
){
    constructor() : this("","", 0, "", "")
}
