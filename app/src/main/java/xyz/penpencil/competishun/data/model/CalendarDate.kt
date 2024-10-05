package xyz.penpencil.competishun.data.model

import java.time.ZonedDateTime


data class CalendarDate(
    val date: String,
    val day: String,
    val task: String?,
    val zonedDateTime: ZonedDateTime?
)