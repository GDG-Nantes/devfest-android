package com.gdgnantes.devfest.android.widget

import android.view.View
import android.widget.TextView

fun TextView.applyText(text: String?) {
    if (!text.isNullOrEmpty()) {
        visibility = View.VISIBLE
        setText(text)
    } else {
        visibility = View.GONE
    }
}