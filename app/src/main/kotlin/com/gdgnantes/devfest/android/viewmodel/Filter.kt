package com.gdgnantes.devfest.android.viewmodel

import android.content.Context
import com.gdgnantes.devfest.android.BookmarkManager
import com.gdgnantes.devfest.android.model.Session

interface Filter {
    fun accept(context: Context, session: Session): Boolean
}

class TrackFilter private constructor(val track: Session.Track) : Filter {
    companion object {
        private val filters: MutableMap<Session.Track, TrackFilter> = HashMap()

        fun get(track: Session.Track): TrackFilter {
            synchronized(filters) {
                var filter = filters[track]
                if (filter == null) {
                    filter = TrackFilter(track)
                    filters.put(track, filter)
                }
                return filter
            }
        }
    }

    override fun accept(context: Context, session: Session): Boolean {
        return track == session.track
    }

    override fun toString(): String {
        return "TrackFilter($track)"
    }
}

object BookmarkFilter : Filter {
    override fun accept(context: Context, session: Session): Boolean {
        return BookmarkManager.from(context).isBookmarked(session.id)
    }

    override fun toString(): String {
        return "BookmarkFilter"
    }
}