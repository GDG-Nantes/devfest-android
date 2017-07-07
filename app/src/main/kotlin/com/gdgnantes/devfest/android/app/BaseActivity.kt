package com.gdgnantes.devfest.android.app

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gdgnantes.devfest.android.util.ThemeUtils

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)

    override fun onCreate(inState: Bundle?) {
        ThemeUtils.ensureRuntimeTheme(this)
        super.onCreate(inState)
    }

    override fun getLifecycle(): LifecycleRegistry = registry

}
