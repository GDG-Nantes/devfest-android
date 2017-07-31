package com.gdgnantes.devfest.android.lifecycle

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal object TaskExecutor {

    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val diskIO: ExecutorService = Executors.newFixedThreadPool(2)

    fun executeOnDiskIO(runnable: Runnable) {
        diskIO.execute(runnable)
    }

    fun executeOnMainThread(runnable: Runnable) {
        mainHandler.post(runnable)
    }

}