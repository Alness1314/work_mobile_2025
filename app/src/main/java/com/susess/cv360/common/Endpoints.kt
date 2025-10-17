package com.susess.cv360.common

object Endpoints {
    const val URL_PRODUCTION = "https://77d2e0e401e1.ngrok-free.app"
    const val URL_DEVELOPMENT = "https://cloud.controlvolumetrico360.com:7080"
    const val AUTH = "/v1/auth"
    const val FACILITIES = "/v1/cv/instalaciones"
    const val TANKS = "/v1/cv/instalaciones/%s/tanques/int"
    const val TANK_DELIVERIES = "/v1/cv/instalaciones/%s/tanques/entregas/int"
    const val TANK_RECEPTIONS = "/v1/cv/instalaciones/%s/tanques/recepciones/int"
    const val TANK_SEND_DELIVERY ="/v1/cv/instalaciones/%s/tanques/%s/entregas/int"
    const val TANK_SEND_RECEPTION = "/v1/cv/instalaciones/%s/tanques/%s/recepciones/int"
    const val ABOUT = "/v1/cv/instalaciones/%s/aplicacion-info/CV360"
    const val EVENT_SEND = "/v1/cv/instalaciones/%s/bitacoras/int"
    const val EVENTS_SEND_TYPE = "/v1/cv/bitacoras/type"
    const val EVENT_TYPE ="/v1/cv/companias/tipos/eventos"
    const val FILE_SEND = "/v1/files/documents"
}