package com.gdgnantes.devfest.android.features.base.util

import android.content.Context
import android.support.annotation.MainThread
import android.util.TypedValue
import com.gdgnantes.devfest.android.features.base.R

object ThemeUtils {

    private val typedValue: TypedValue = TypedValue()

    @MainThread
    fun ensureRuntimeTheme(context: Context) {
        context.theme.resolveAttribute(R.attr.runtimeTheme, typedValue, true)
        require(typedValue.resourceId > 0) {
            "runtimeTheme is not defined in the preview theme"
        }
        context.setTheme(typedValue.resourceId)
    }

}