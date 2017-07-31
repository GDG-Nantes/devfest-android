package com.gdgnantes.devfest.android.util

import java.util.*
import java.util.concurrent.TimeUnit

inline val Date.timeAsSeconds: Long
    get() = TimeUnit.MILLISECONDS.toSeconds(time)