package com.gdgnantes.devfest.android.model

import android.database.Cursor
import android.support.annotation.Keep
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.provider.ScheduleContract
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

fun Cursor.toSession(): Session {
    return Session(
            id = getStringOrThrow(ScheduleContract.Sessions.SESSION_ID),
            description = getStringOrThrow(ScheduleContract.Sessions.SESSION_DESCRIPTION),
            endTimestamp = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(ScheduleContract.Sessions.SESSION_END_TIMESTAMP)))),
            room = getStringOrThrow(ScheduleContract.Sessions.SESSION_ROOM_ID),
            startTimestamp = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(ScheduleContract.Sessions.SESSION_START_TIMESTAMP)))),
            title = getStringOrThrow(ScheduleContract.Sessions.SESSION_TITLE)
    )
}
