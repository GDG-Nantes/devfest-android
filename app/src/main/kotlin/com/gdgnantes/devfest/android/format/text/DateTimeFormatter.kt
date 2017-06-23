package com.gdgnantes.devfest.android.format.text

import android.text.format.DateFormat
import com.gdgnantes.devfest.android.AppConfig
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {

    private fun formatter(template: String): Lazy<SimpleDateFormat> {
        return lazy {
            val formatter = SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), template))
            formatter.timeZone = AppConfig.EVENT_TIMEZONE
            formatter
        }
    }

    private val formatterHHmm: SimpleDateFormat by formatter("HHmm")
    private val formatterMMMMd: SimpleDateFormat by formatter("MMMMd")
    private val formatterEEEEMMMMd: SimpleDateFormat by formatter("EEEEMMMMdYYYY")

    fun formatHHmm(date: Date): String {
        return formatterHHmm.format(date)
    }

    fun formatMMMMd(date: Date): String {
        return formatterMMMMd.format(date)
    }

    fun formatEEEEMMMMd(date: Date): String {
        return formatterEEEEMMMMd.format(date)
    }

}