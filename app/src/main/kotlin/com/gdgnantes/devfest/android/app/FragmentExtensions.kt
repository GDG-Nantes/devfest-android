package com.gdgnantes.devfest.android.app

import android.content.Intent
import android.support.v4.app.Fragment
import android.widget.Toast

fun Fragment.startActivitySafely(intent: Intent, message: String = ""): Boolean {
    if (intent.resolveActivity(context.packageManager) != null) {
        startActivity(intent)
        return true
    }
    if (message.isNotEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    return false
}
