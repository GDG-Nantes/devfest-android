package com.gdgnantes.devfest.android.view

import android.view.View
import android.view.ViewGroup
import kotlin.LazyThreadSafetyMode.NONE

inline fun <reified T : View> View.bind(resId: Int): Lazy<T> = lazy(NONE) { findViewById<T>(resId) }

fun View.removeFromParent() {
    val parent = parent
    if (parent is ViewGroup) {
        parent.removeView(this)
    }
}
