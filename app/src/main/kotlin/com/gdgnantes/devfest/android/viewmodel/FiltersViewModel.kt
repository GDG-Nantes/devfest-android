package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.util.ArraySet
import com.gdgnantes.devfest.android.model.Session


class FiltersViewModel(application: Application) : AndroidViewModel(application) {

    private val _filters: MutableLiveData<Set<Session.Track>> = MutableLiveData()

    init {
        _filters.value = emptySet()
    }

    fun isFilter(track: Session.Track) = track in _filters.value ?: emptySet()

    fun toggleFilter(track: Session.Track) {
        val newFilters = ArraySet(_filters.value)
        if (isFilter(track)) {
            newFilters.remove(track)
        } else {
            newFilters.add(track)
        }
        _filters.value = newFilters
    }

    val filters: LiveData<Set<Session.Track>>
        get() = _filters

}