package com.gdgnantes.devfest.android.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

operator fun ViewGroup.get(position: Int): View? = getChildAt(position)

inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (i in 0..childCount - 1) {
        action(getChildAt(i))
    }
}

inline fun <reified T : View> ViewGroup.inflate(resId: Int, attach: Boolean = true): T {
    val view: T = LayoutInflater.from(context).inflate(resId, this, false) as T
    if (attach) {
        addView(view)
    }
    return view
}