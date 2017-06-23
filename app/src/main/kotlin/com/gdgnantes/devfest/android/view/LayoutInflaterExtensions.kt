package com.gdgnantes.devfest.android.view

import android.view.LayoutInflater
import android.view.ViewGroup

inline fun <reified T> LayoutInflater.inflate(resId: Int, parent: ViewGroup, attachToRoot: Boolean): T {
    return inflate(resId, parent, attachToRoot) as T
}
