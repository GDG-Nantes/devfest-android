package com.gdgnantes.devfest.android.util

import android.graphics.Color
import android.support.v4.util.ArrayMap
import java.util.*

object Tags {

    private const val DEFAULT_COLOR = Color.GRAY

    private val tags: Map<String, Int>

    init {
        tags = ArrayMap<String, Int>().apply {
            put("android", 0xFF4DBDB5.toInt())
            put("html5", 0xFFF5BFFF.toInt())
            put("css", 0xFFEDBBAB.toInt())
            put("docker", 0xFFA0EBE6.toInt())
        }
    }

    fun colorForTag(tag: String): Int {
        return tags[tag.toLowerCase(Locale.US)] ?: DEFAULT_COLOR
    }

}
