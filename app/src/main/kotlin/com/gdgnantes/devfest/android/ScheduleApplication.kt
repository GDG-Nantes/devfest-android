package com.gdgnantes.devfest.android

import android.app.Application
import android.support.v7.app.AppCompatDelegate

class ScheduleApplication : Application() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}