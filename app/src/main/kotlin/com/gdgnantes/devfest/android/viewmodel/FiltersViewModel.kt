package com.gdgnantes.devfest.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.util.ArraySet


class FiltersViewModel(application: Application) : AndroidViewModel(application) {

    private val _filters: MutableLiveData<Set<Filter>> = MutableLiveData()

    fun isFilter(filter: Filter) = filter in _filters.value ?: emptySet()

    fun hasFilters() = !(_filters.value?.isEmpty() ?: true)

    fun toggleFilter(filter: Filter) {
        val newFilters = ArraySet(_filters.value)
        if (isFilter(filter)) {
            newFilters.remove(filter)
        } else {
            newFilters.add(filter)
        }
        _filters.value = newFilters
    }

    fun clear() {
        _filters.value = emptySet()
    }

    val filters: LiveData<Set<Filter>>
        get() = _filters

}