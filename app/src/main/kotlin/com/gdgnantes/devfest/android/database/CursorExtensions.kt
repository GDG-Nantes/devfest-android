package com.gdgnantes.devfest.android.database

import android.database.Cursor

fun Cursor.getStringOrThrow(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}