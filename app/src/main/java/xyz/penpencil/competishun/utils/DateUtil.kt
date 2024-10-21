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

fun String.timeStatus(duration: Int): String {
    try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val dateTime = LocalDateTime.parse(this, formatter)

        val year = dateTime.year
        val month = dateTime.monthValue
        val day = dateTime.dayOfMonth
        val hour = dateTime.hour
        val minute = dateTime.minute
        val second = dateTime.second

        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val remainingSeconds = duration % 60
        var rHours = hour + hours
        val rMin = minute + minutes

        fun getAmPmTime(hours: Int, minutes: Int, seconds: Int): Pair<String, String> {
            var localHour = hours
            val amPm = if (localHour >= 12) {
                if (localHour > 12) localHour -= 12
                "PM"
            } else {
                if (localHour == 0) localHour = 12
                "AM"
            }
            val formattedHour = if (localHour < 10) "0$localHour" else localHour.toString()
            val formattedMin = if (minutes < 10) "0$minutes" else minutes.toString()
//            val formattedSec = if (seconds < 10) "0$seconds" else seconds.toString()
            return Pair("$formattedHour:$formattedMin", amPm)
        }

        val (origTime, origAmPm) = getAmPmTime(hour, minute, second)
        val (calculatedTime, calcAmPm) = getAmPmTime(rHours, rMin, second)

        Log.e("UtcToIst", "$this || UtcToIst: $day-$month-$year $origTime $origAmPm")
        return "$origTime:$origAmPm-$calculatedTime:$calcAmPm"
    } catch (e: Exception) {
        return ""
    }
}


fun String.toIstZonedDateTime(): ZonedDateTime {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    val localDateTime = LocalDateTime.parse(this, formatter)
    val istZone = ZoneId.of("Asia/Kolkata")
    return ZonedDateTime.of(localDateTime, istZone)
}

private fun formatTimeDuration(totalDuration: Int): String {
    return when {
        totalDuration < 60 -> "${totalDuration} sec"
        totalDuration == 60 -> "1h"
        else -> {
            val hours = totalDuration / 3600
            val minutes = (totalDuration % 3600) / 60
            val seconds = totalDuration % 60
            listOf(
                if (hours > 0) "${hours} hr${if (hours > 1) "s" else ""}" else "",
                if (minutes > 0) "${minutes} min${if (minutes > 1) "s" else ""}" else "",
                if (seconds > 0) "${seconds} sec" else ""
            ).filter { it.isNotEmpty() }.joinToString(" ").trim()
        }
    }
}