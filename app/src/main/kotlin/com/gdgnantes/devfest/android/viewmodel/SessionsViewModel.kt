package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.gdgnantes.devfest.android.lifecycle.SessionsLiveData
import com.gdgnantes.devfest.android.model.Room
import com.gdgnantes.devfest.android.model.Session


class SessionsViewModel(application: Application) : AndroidViewModel(application) {

    data class Data(val session: Session, val room: Room?)

    private var _date: String? = null

    fun init(date: String) {
        _date = date
    }

    val sessions by lazy {
        SessionsLiveData(getApplication(), _date ?: throw IllegalStateException("init must be called first"))
    }

}