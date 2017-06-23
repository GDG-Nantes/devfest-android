package com.gdgnantes.devfest.android.lifecycle

import android.content.Context
import android.database.ContentObserver
import android.util.Log
import com.gdgnantes.devfest.android.provider.ScheduleContract

abstract class ProviderLiveData<T>(private val context: Context) : ComputableLiveData<T>() {

    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            invalidate()
        }
    }

    init {
        context.contentResolver.registerContentObserver(ScheduleContract.CONTENT_URI, true, observer)
    }

    protected fun finalize() {
        Log.e("Prout", "Finalizing ProviderLiveData$this")
        context.contentResolver.unregisterContentObserver(observer)
    }

}