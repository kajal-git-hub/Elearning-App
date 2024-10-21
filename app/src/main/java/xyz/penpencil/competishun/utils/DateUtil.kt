package xyz.penpencil.competishun.utils

import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.utcToIst(): String {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val dateTime = LocalDateTime.parse(this, formatter)

    val year = dateTime.year
    val month = dateTime.monthValue
    val day = dateTime.dayOfMonth
    val hour = dateTime.hour
    val minute = dateTime.minute
    val second = dateTime.second

    val d = if (day < 10) "0$day" else day.toString()
    val m = if (month < 10) "0$month" else month.toString()
    val h = if (hour < 10) "0$hour" else hour.toString()
    val min = if (minute < 10) "0$minute" else minute.toString()
    val sec = if (second < 10) "0$second" else second.toString()
    Log.e("UtcToIst", "$this ||  UtcToIst: $d-$m-$year $h:$min:$sec")
    return "$d-$m-$year $h:$min:$sec"
}


fun String.toIstZonedDateTime(): ZonedDateTime {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    val localDateTime = LocalDateTime.parse(this, formatter)
    val istZone = ZoneId.of("Asia/Kolkata")
    return ZonedDateTime.of(localDateTime, istZone)
}