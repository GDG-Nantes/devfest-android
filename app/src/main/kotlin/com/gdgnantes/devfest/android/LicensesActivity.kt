package com.gdgnantes.devfest.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gdgnantes.devfest.android.app.BaseActivity

class LicensesActivity : BaseActivity() {

    companion object {
        const val FRAGMENT_LICENSES = "fragment:licenses"

        fun newIntent(context: Context): Intent
                = Intent(context, LicensesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_LICENSES) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, LicensesFragment.newInstance(), FRAGMENT_LICENSES)
                    .commit()
        }
    }

}