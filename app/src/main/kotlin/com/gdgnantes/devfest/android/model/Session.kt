package com.gdgnantes.devfest.android.model

import android.content.ContentValues
import android.database.Cursor
import android.support.annotation.Keep
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.gdgnantes.devfest.android.util.timeAsSeconds
import java.util.*
import java.util.concurrent.TimeUnit

@Keep
data class Session(
        val id: String,
        val description: String,
        val endTimestamp: Date,
        val room: String,
        val startTimestamp: Date,
        val title: String,

        val speakers: List<String> = emptyList())

fun Session.toContentValues() = ContentValues().apply {
    put(ScheduleContract.Sessions.SESSION_ID, id)
    put(ScheduleContract.Sessions.SESSION_DESCRIPTION, description)
    put(ScheduleContract.Sessions.SESSION_END_TIMESTAMP, endTimestamp.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_ROOM_ID, room)
    put(ScheduleContract.Sessions.SESSION_START_TIMESTAMP, startTimestamp.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_TITLE, title)
}

fun Cursor.toSession() = Session(
        id = getStringOrThrow(ScheduleContract.Sessions.SESSION_ID),
        description = getStringOrThrow(ScheduleContract.Sessions.SESSION_DESCRIPTION),
        endTimestamp = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(ScheduleContract.Sessions.SESSION_END_TIMESTAMP)))),
        room = getStringOrThrow(ScheduleContract.Sessions.SESSION_ROOM_ID),
        startTimestamp = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(ScheduleContract.Sessions.SESSION_START_TIMESTAMP)))),
        title = getStringOrThrow(ScheduleContract.Sessions.SESSION_TITLE)
)
