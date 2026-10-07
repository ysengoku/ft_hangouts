package com.ysengoku.ft_hangouts.ui.format

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.ysengoku.ft_hangouts.R
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId 
import java.util.Date

fun timeLabel(context: Context, millis: Long): String =
    DateFormat.getTimeFormat(context).format(Date(millis))

fun dateLabel(context: Context, millis: Long): String? {
    val zone = ZoneId.systemDefault()
    val day = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)

    return when (day) {
        today -> null
        today.minusDays(1) -> context.getString(R.string.yesterday)
        else -> {
            var flags = DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
            if (day.year != today.year) flags = flags or DateUtils.FORMAT_SHOW_YEAR
            DateUtils.formatDateTime(context, millis, flags)
        }
    }
}

val birthdayFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

fun birthdayLabel(date: LocalDate) =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

