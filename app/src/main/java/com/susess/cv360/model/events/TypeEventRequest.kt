package com.susess.cv360.model.events

data class TypeEventRequest(
    var  event: String,
    var data: String,
){
    constructor(): this("", "")
}
