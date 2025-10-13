package com.susess.cv360.helpers

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.LocalTime

object DateTimeUtils {
     fun convertirMilisegundosAFecha(milisegundos: String): String {
        return Instant.ofEpochMilli(milisegundos.toLong())
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun obtenerSegundos(): Int = LocalTime.now().second

    fun formatDateTimeToOffsetDateTime(dateTime: String): String {
        val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS xxxxx")

        val localDateTime = LocalDateTime.parse(dateTime, formatterIn)
        val offsetDateTime = OffsetDateTime.of(localDateTime, OffsetDateTime.now().offset)

        return offsetDateTime.format(formatterOut)
    }

    fun formatOffsetDateTimeToDateTime(dateTime: String): String {
        val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS xxxxx")
        val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val localDateTime = LocalDateTime.parse(dateTime, formatterIn)
        val offsetDateTime = OffsetDateTime.of(localDateTime, OffsetDateTime.now().offset)

        return offsetDateTime.format(formatterOut)
    }

    fun formatearHora(hora: Int, minutos: Int): String {
        return LocalTime.of(hora, minutos, obtenerSegundos())
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}