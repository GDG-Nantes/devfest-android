package com.gdgnantes.devfest.android.lifecycle

import android.content.Context
import com.gdgnantes.devfest.android.model.Room
import com.gdgnantes.devfest.android.model.toRoom
import com.gdgnantes.devfest.android.model.toSession
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.gdgnantes.devfest.android.viewmodel.SessionsViewModel

class SessionsLiveData(private val context: Context,
                       private val date: String) : ProviderLiveData<List<SessionsViewModel.Data>>(context) {

    companion object {
        // TODO Cyril
        // Account for the event timezone or express everything in the users timezone ?
        const val SELECTION = "date(${ScheduleContract.Sessions.SESSION_START_TIMESTAMP}, 'unixepoch') = ?"
        const val ORDER = "${ScheduleContract.Sessions.SESSION_START_TIMESTAMP} ASC, " +
                "${ScheduleContract.Sessions.SESSION_END_TIMESTAMP} ASC, " +
                "${ScheduleContract.Sessions.SESSION_ROOM_ID} ASC"
    }

    override fun compute(): List<SessionsViewModel.Data> {
        val cursor = context.contentResolver.query(
                ScheduleContract.Sessions.CONTENT_URI,
                null,
                SELECTION,
                arrayOf(date),
                ORDER)

        val result = ArrayList<SessionsViewModel.Data>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val session = cursor.toSession()
                var room: Room? = null
                if (session.roomId != null) {
                    room = cursor.toRoom()
                }
                result.add(SessionsViewModel.Data(session, room))
            }
            cursor.close()
        }

        return result
    }

}
