package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.gdgnantes.devfest.android.lifecycle.SessionsLiveData
import com.gdgnantes.devfest.android.model.Room
import com.gdgnantes.devfest.android.model.Session


class SessionsViewModel(application: Application) : AndroidViewModel(application) {

    data class Data(val session: Session, val room: Room)

    fun getSessions(date: String): LiveData<List<Data>> = SessionsLiveData(getApplication(), date)

}