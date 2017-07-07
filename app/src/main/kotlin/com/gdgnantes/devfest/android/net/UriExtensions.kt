package com.gdgnantes.devfest.android.net

import android.net.Uri

fun Uri.getIntQueryParameter(key: String, defaultValue: Int = 0): Int = getQueryParameter(key)?.toIntOrNull() ?: defaultValue
