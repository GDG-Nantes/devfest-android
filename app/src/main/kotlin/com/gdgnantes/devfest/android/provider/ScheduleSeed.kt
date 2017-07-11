package com.gdgnantes.devfest.android.provider

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.http.JsonConverters
import com.gdgnantes.devfest.android.json.fromJson
import com.gdgnantes.devfest.android.model.Schedule
import com.gdgnantes.devfest.android.model.toContentValues
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader


internal class ScheduleSeed(val context: Context) {

    companion object {
        private const val TAG = "ScheduleSeed"
        private const val DEBUG_LOG_PERFORMANCE = true
    }

    fun seed(database: SQLiteDatabase) {
        var now: Long = 0
        if (DEBUG_LOG_PERFORMANCE) {
            now = System.currentTimeMillis()
        }
        val schedule = parse()
        if (DEBUG_LOG_PERFORMANCE) {
            Log.i(TAG, "Parsing took: ${System.currentTimeMillis() - now}ms")
            now = System.currentTimeMillis()
        }
        insert(database, schedule)
        if (DEBUG_LOG_PERFORMANCE) {
            Log.i(TAG, "Insertion took: ${System.currentTimeMillis() - now}ms")
        }
    }

    private fun insert(database: SQLiteDatabase, schedule: Schedule) {
        insertRooms(database, schedule)
        insertSpeakers(database, schedule)
        insertSessions(database, schedule)
    }

    private fun insertRooms(database: SQLiteDatabase, schedule: Schedule) {
        schedule.rooms.forEach {
            database.insert(ScheduleDatabase.Tables.ROOMS, null, it.toContentValues())
        }
    }

    private fun insertSpeakers(database: SQLiteDatabase, schedule: Schedule) {
        schedule.speakers.forEach {
            database.insert(ScheduleDatabase.Tables.SPEAKERS, null, it.toContentValues())
        }
    }

    private fun insertSessions(database: SQLiteDatabase, schedule: Schedule) {
        schedule.sessions.forEach { session ->
            database.insert(ScheduleDatabase.Tables.SESSIONS, null, session.toContentValues())

            session.speakersIds.forEach { speakerId ->
                val values = ContentValues().apply {
                    put(ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID, session.id)
                    put(ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SPEAKER_ID, speakerId)
                }
                database.insert(ScheduleDatabase.Tables.SESSIONS_SPEAKERS, null, values)
            }
        }
    }

    private fun parse(): Schedule {
        var reader: Reader? = null
        try {
            reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.seed)))
            return JsonConverters.main.fromJson(reader)
        } finally {
            reader?.close()
        }
    }

}
