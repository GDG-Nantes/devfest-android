package com.gdgnantes.devfest.android.util

import java.util.*

inline val Date.timeAsSeconds: Long
    get() = time / 1000