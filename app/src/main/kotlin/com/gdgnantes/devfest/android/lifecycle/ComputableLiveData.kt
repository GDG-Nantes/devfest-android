package com.gdgnantes.devfest.android.lifecycle

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import java.util.concurrent.atomic.AtomicBoolean

abstract class ComputableLiveData<T> : MutableLiveData<T>() {

    private val mInvalid = AtomicBoolean(true)
    private val mComputing = AtomicBoolean(false)

    override fun onActive() {
        super.onActive()
        TaskExecutor.executeOnDiskIO(mRefreshRunnable)
    }

    fun invalidate() {
        TaskExecutor.executeOnMainThread(mInvalidationRunnable)
    }

    @WorkerThread
    protected abstract fun compute(): T

    private val mRefreshRunnable: Runnable = Runnable {
        var computed: Boolean
        do {
            computed = false
            // compute can happen only in 1 thread but no reason to lock others.
            if (mComputing.compareAndSet(false, true)) {
                // as long as it is invalid, keep computing.
                try {
                    var value: T? = null
                    while (mInvalid.compareAndSet(true, false)) {
                        computed = true
                        value = compute()
                    }
                    if (computed) {
                        postValue(value)
                    }
                } finally {
                    // release compute lock
                    mComputing.set(false)
                }
            }
            // check invalid after releasing compute lock to avoid the following scenario.
            // Thread A runs compute()
            // Thread A checks invalid, it is false
            // Main thread sets invalid to true
            // Thread B runs, fails to acquire compute lock and skips
            // Thread A releases compute lock
            // We've left invalid in set state. The check below recovers.
        } while (computed && mInvalid.get())
    }

    private val mInvalidationRunnable: Runnable = Runnable {
        val isActive = hasActiveObservers()
        if (mInvalid.compareAndSet(false, true)) {
            if (isActive) {
                TaskExecutor.executeOnDiskIO(mRefreshRunnable)
            }
        }
    }
}
