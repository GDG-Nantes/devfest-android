package com.gdgnantes.devfest.android.database

import android.database.Cursor
import java.util.*
import java.util.concurrent.TimeUnit

fun Cursor.getStringOrThrow(columnName: String): String? {
    return getString(getColumnIndexOrThrow(columnName))
}

fun Cursor.getDateOrThrow(columnName: String): Date? {
    val columnIndex = getColumnIndexOrThrow(columnName)
    if (isNull(columnIndex)) {
        return null
    }
    return Date(TimeUnit.SECONDS.toMillis(getLong(columnIndex)))
}