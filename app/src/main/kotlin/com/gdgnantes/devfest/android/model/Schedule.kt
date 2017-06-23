package com.gdgnantes.devfest.android.model

import android.support.annotation.Keep

@Keep
class Schedule {
    @JvmField val rooms: List<Room>? = null
    @JvmField val sessions: List<Session>? = null
    @JvmField val speakers: List<Speaker>? = null
}