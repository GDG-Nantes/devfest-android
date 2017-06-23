package com.gdgnantes.devfest.android.view

import android.view.View
import android.view.ViewGroup

fun View.removeFromParent() {
    val parent = parent
    if (parent is ViewGroup) {
        parent.removeView(this)
    }
}