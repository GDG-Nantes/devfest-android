package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.gdgnantes.devfest.android.lifecycle.SessionLiveData
import com.gdgnantes.devfest.android.lifecycle.SessionsLiveData
import com.gdgnantes.devfest.android.model.Room
import com.gdgnantes.devfest.android.model.Session
import com.gdgnantes.devfest.android.model.Speaker


class SessionViewModel(application: Application) : AndroidViewModel(application) {

    data class Data(
            val session: Session,
            val room: Room,
            val speakers: List<Speaker>)

    fun getSession(sessionId: String): LiveData<Data> = SessionLiveData(getApplication(), sessionId)

}