package com.gdgnantes.devfest.android.features.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gdgnantes.devfest.android.features.base.app.BaseActivity

class AboutActivity : BaseActivity() {

    companion object {
        private const val FRAGMENT_ABOUT = "fragment:about"

        fun newIntent(context: Context): Intent
                = Intent(context, AboutActivity::class.java)
    }

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_ABOUT) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, AboutFragment.newInstance(), FRAGMENT_ABOUT)
                    .commit()
        }
    }

}