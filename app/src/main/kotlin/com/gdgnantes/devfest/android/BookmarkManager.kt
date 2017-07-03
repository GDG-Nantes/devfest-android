package com.gdgnantes.devfest.android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.MainThread
import com.gdgnantes.devfest.android.app.PreferencesManager

@MainThread
class BookmarkManager private constructor(context: Context) {

    private val preferencesManager = PreferencesManager.from(context)
    private val bookmarks = MutableLiveData<Set<String>>()

    init {
        bookmarks.value = preferencesManager.bookmarks
    }

    companion object {

        private var instance: BookmarkManager? = null

        fun from(context: Context): BookmarkManager {
            if (instance == null) {
                instance = BookmarkManager(context.applicationContext)
            }
            return instance!!
        }
    }

    fun getLiveData(): LiveData<Set<String>> {
        return bookmarks
    }

    fun isBookmarked(sessionId: String): Boolean {
        return bookmarks.value!!.any { sessionId == it }
    }

    fun bookmark(sessionId: String) {
        val bookmarks = HashSet(preferencesManager.bookmarks)
        bookmarks.add(sessionId)
        preferencesManager.bookmarks = bookmarks
        this.bookmarks.value = bookmarks
    }

    fun unbookmark(sessionId: String) {
        val bookmarks = HashSet(preferencesManager.bookmarks)
        bookmarks.remove(sessionId)
        preferencesManager.bookmarks = bookmarks
        this.bookmarks.value = bookmarks
    }

}