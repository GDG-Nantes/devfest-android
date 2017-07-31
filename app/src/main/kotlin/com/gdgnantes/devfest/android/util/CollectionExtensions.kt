package com.gdgnantes.devfest.android.util

fun <T> List<T>.asArrayList() = if (this is ArrayList) this else ArrayList(this)