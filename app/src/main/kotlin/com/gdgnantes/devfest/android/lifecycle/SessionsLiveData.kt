package com.gdgnantes.devfest.android.lifecycle

import android.content.Context
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
    }

    override fun compute(): List<SessionsViewModel.Data> {
        val cursor = context.contentResolver.query(
                ScheduleContract.Sessions.CONTENT_URI,
                null,
                SELECTION,
                arrayOf(date),
                null)

        val result = ArrayList<SessionsViewModel.Data>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(SessionsViewModel.Data(
                        cursor.toSession(),
                        cursor.toRoom()))
            }
            cursor.close()
        }

        return result
    }

}
