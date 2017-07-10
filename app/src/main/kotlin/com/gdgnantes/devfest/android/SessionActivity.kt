package com.gdgnantes.devfest.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gdgnantes.devfest.android.app.BaseActivity
import com.gdgnantes.devfest.android.app.PREFIX_EXTRA

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
            val sessionId: String = getSessionId()
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, SessionFragment.newInstance(sessionId), FRAGMENT_SESSION_DETAIL)
                    .commit()
        }
    }

    private fun getSessionId(): String {
        intent.data?.let {
            if (it.scheme in AppConfig.SCHEMES && it.authority in AppConfig.AUTHORITIES) {
                it.pathSegments?.let {
                    if (it.size == 2 && it[0] == AppConfig.PATH_SESSIONS) {
                        return it[1]
                    }
                }
            }
        }
        return intent.getStringExtra(EXTRA_SESSION_ID)
    }

}