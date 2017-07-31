package com.gdgnantes.devfest.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.widget.TextSwitcher
import com.gdgnantes.devfest.android.app.BaseActivity


class SessionActivity : BaseActivity() {

    companion object {
        private val FRAGMENT_SESSION_DETAIL = "fragment:sessionDetails"

        fun newIntent(context: Context, sessionId: String): Intent = Intent(context, SessionActivity::class.java)
                .setData(Uri.parse("content://sessions/$sessionId"))
    }

    private lateinit var switcher: TextSwitcher

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)

        supportActionBar?.apply {
            setCustomView(R.layout.activity_session_toolbar_switcher)
            switcher = customView as TextSwitcher

            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_HOME_AS_UP
        }

        if (supportFragmentManager.findFragmentByTag(FRAGMENT_SESSION_DETAIL) == null) {
            val sessionId: String = getSessionId()
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, SessionFragment.newInstance(sessionId), FRAGMENT_SESSION_DETAIL)
                    .commit()
        }

        switcher.setCurrentText(title)
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        switcher.setText(title)
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
        return intent.data.lastPathSegment
    }

}