package com.gdgnantes.devfest.android.model

import android.content.ContentValues
import android.database.Cursor
import android.support.annotation.Keep
import com.gdgnantes.devfest.android.database.getDateOrThrow
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.gdgnantes.devfest.android.util.timeAsSeconds
import java.util.*

@Keep
data class Session(
        val id: String,
        val description: String,
        val endTimestamp: Date,
        val roomId: String,
        val startTimestamp: Date,
        val title: String,

        val speakersIds: List<String> = emptyList())

fun Session.toContentValues() = ContentValues().apply {
    put(ScheduleContract.Sessions.SESSION_ID, id)
    put(ScheduleContract.Sessions.SESSION_DESCRIPTION, description)
    put(ScheduleContract.Sessions.SESSION_END_TIMESTAMP, endTimestamp.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_ROOM_ID, roomId)
    put(ScheduleContract.Sessions.SESSION_START_TIMESTAMP, startTimestamp.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_TITLE, title)
}

fun Cursor.toSession() = Session(
        id = getStringOrThrow(ScheduleContract.Sessions.SESSION_ID)!!,
        description = getStringOrThrow(ScheduleContract.Sessions.SESSION_DESCRIPTION)!!,
        endTimestamp = getDateOrThrow(ScheduleContract.Sessions.SESSION_END_TIMESTAMP)!!,
        roomId = getStringOrThrow(ScheduleContract.Sessions.SESSION_ROOM_ID)!!,
        startTimestamp = getDateOrThrow(ScheduleContract.Sessions.SESSION_START_TIMESTAMP)!!,
        title = getStringOrThrow(ScheduleContract.Sessions.SESSION_TITLE)!!
)
