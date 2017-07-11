package com.gdgnantes.devfest.android.model

import android.content.ContentValues
import android.database.Cursor
import android.support.annotation.Keep
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.provider.ScheduleContract

@Keep
data class Room(
        val id: String,
        val name: String)

fun Room.toContentValues() = ContentValues().apply {
    put(ScheduleContract.Rooms.ROOM_ID, id!!)
    put(ScheduleContract.Rooms.ROOM_NAME, name!!)
}

fun Cursor.toRoom() = Room(
        id = getStringOrThrow(ScheduleContract.Rooms.ROOM_ID)!!,
        name = getStringOrThrow(ScheduleContract.Rooms.ROOM_NAME)!!
)