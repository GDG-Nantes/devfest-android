package com.gdgnantes.devfest.android.app

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v7.app.AppCompatActivity
import com.gdgnantes.devfest.android.BuildConfig


abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    companion object {
        const val PREFIX_EXTRA = "${BuildConfig.APPLICATION_ID}.extra."
    }

    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = registry

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                val upIntent = NavUtils.getParentActivityIntent(this)
//                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//                    TaskStackBuilder.create(this)
//                            .addNextIntentWithParentStack(upIntent)
//                            .startActivities()
//                } else {
//                    NavUtils.navigateUpTo(this, upIntent)
//                }
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

}
