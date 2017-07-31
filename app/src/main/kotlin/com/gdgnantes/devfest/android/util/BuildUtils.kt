package com.gdgnantes.devfest.android.util

import android.os.Build

object BuildUtils {

    fun hasO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

}