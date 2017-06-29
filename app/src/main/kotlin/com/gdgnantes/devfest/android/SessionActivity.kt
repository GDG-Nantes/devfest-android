package com.gdgnantes.devfest.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gdgnantes.devfest.android.app.BaseActivity

class SessionActivity : BaseActivity() {

    companion object {
        private val FRAGMENT_SESSION_DETAIL = "fragment:sessionDetails"

        private val EXTRA_SESSION_ID = "$PREFIX_EXTRA.sessionDetails"

        fun newIntent(context: Context, sessionId: String): Intent
                = Intent(context, SessionActivity::class.java).putExtra(EXTRA_SESSION_ID, sessionId)
    }

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_SESSION_DETAIL) == null) {
            val sessionId: String = intent.getStringExtra(EXTRA_SESSION_ID)
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, SessionFragment.newInstance(sessionId), FRAGMENT_SESSION_DETAIL)
                    .commit()
        }
    }

}