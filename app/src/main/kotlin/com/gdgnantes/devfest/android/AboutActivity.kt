package com.gdgnantes.devfest.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gdgnantes.devfest.android.app.BaseActivity

class AboutActivity : BaseActivity() {

    companion object {
        private const val FRAGMENT_ABOUT = "fragment:about"

        fun newIntent(context: Context): Intent
                = Intent(context, AboutActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_ABOUT) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, AboutFragment.newInstance(), FRAGMENT_ABOUT)
                    .commit()
        }
    }

}