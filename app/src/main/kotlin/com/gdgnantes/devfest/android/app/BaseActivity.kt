package com.gdgnantes.devfest.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.gdgnantes.devfest.android.util.ThemeUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(inState: Bundle?) {
        ThemeUtils.ensureRuntimeTheme(this)
        super.onCreate(inState)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
