package com.gdgnantes.devfest.android.lifecycle

import android.content.Context
import com.gdgnantes.devfest.android.model.*
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.gdgnantes.devfest.android.viewmodel.SessionViewModel

class SessionLiveData(private val context: Context,
                      private val sessionId: String) : ProviderLiveData<SessionViewModel.Data>(context) {

    companion object {
        const val SELECTION = "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID} = ?"
        const val ORDER = "${ScheduleContract.SessionsSpeakers.SPEAKER_NAME} ASC"
    }

    override fun compute(): SessionViewModel.Data {
        val cursor = context.contentResolver.query(
                ScheduleContract.SessionsSpeakers.CONTENT_URI,
                null,
                SELECTION,
                arrayOf(sessionId),
                ORDER)

        var session: Session? = null
        var room: Room? = null
        val speakers = ArrayList<Speaker>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.position == 0) {
                    session = cursor.toSession()
                    room = cursor.toRoom()
                }
                speakers.add(cursor.toSpeaker())
            }
            cursor.close()
        }

        return SessionViewModel.Data(
                session!!,
                room!!,
                speakers)
    }

}
