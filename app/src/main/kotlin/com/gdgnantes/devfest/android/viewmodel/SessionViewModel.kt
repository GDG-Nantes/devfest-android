package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.gdgnantes.devfest.android.lifecycle.SessionLiveData
import com.gdgnantes.devfest.android.model.Room
import com.gdgnantes.devfest.android.model.Session
import com.gdgnantes.devfest.android.model.Speaker


class SessionViewModel(application: Application) : AndroidViewModel(application) {

    data class Data(
            val session: Session,
            val room: Room?,
            val speakers: List<Speaker>)

    private var _sessionId: String? = null

    fun init(sessionId: String) {
        _sessionId = sessionId
    }

    val session by lazy {
        SessionLiveData(getApplication(), _sessionId ?: throw IllegalStateException("init must be called first"))
    }

    val sessionTitle: String
        get() = session.value!!.session.title

}