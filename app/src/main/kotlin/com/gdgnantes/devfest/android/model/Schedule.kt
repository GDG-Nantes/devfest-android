package com.gdgnantes.devfest.android.model

import android.support.annotation.Keep

@Keep
class Schedule {
    @JvmField val rooms: List<Room> = emptyList()
    @JvmField val sessions: List<Session> = emptyList()
    @JvmField val speakers: List<Speaker> = emptyList()
}