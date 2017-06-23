package com.gdgnantes.devfest.android.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.http.JsonConverters
import com.gdgnantes.devfest.android.json.fromJson
import com.gdgnantes.devfest.android.model.Schedule
import com.gdgnantes.devfest.android.util.timeAsSeconds
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader


internal class ScheduleSeed(val context: Context) {

    companion object Inserts {

        private const val TAG = "ScheduleSeed"
        private const val DEBUG_LOG_PERFORMANCE = true

        private interface Room {
            companion object {
                const val STATEMENT = "INSERT INTO ${ScheduleDatabase.Tables.ROOMS} (" +
                        "${ScheduleContract.Rooms.ROOM_ID}, " +
                        "${ScheduleContract.Rooms.ROOM_NAME}) " +
                        "VALUES (?, ?)"

                const val ID = 1
                const val NAME = 2
            }
        }

        private interface Speaker {
            companion object {
                const val STATEMENT = "INSERT INTO ${ScheduleDatabase.Tables.SPEAKERS} (" +
                        "${ScheduleContract.Speakers.SPEAKER_ID}, " +
                        "${ScheduleContract.Speakers.SPEAKER_BIO}, " +
                        "${ScheduleContract.Speakers.SPEAKER_COMPANY}, " +
                        "${ScheduleContract.Speakers.SPEAKER_COUNTRY}, " +
                        "${ScheduleContract.Speakers.SPEAKER_NAME}, " +
                        "${ScheduleContract.Speakers.SPEAKER_PHOTO_URL}, " +
                        "${ScheduleContract.Speakers.SPEAKER_TAGS}) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"

                const val ID = 1
                const val BIO = 2
                const val COMPANY = 3
                const val COUNTRY = 4
                const val NAME = 5
                const val PHOTO_URL = 6
                const val TAGS = 7
            }
        }

        private interface Session {
            companion object {
                const val STATEMENT = "INSERT INTO ${ScheduleDatabase.Tables.SESSIONS} (" +
                        "${ScheduleContract.Sessions.SESSION_ID}, " +
                        "${ScheduleContract.Sessions.SESSION_DESCRIPTION}, " +
                        "${ScheduleContract.Sessions.SESSION_END_TIMESTAMP}, " +
                        "${ScheduleContract.Sessions.SESSION_ROOM_ID}, " +
                        "${ScheduleContract.Sessions.SESSION_START_TIMESTAMP}, " +
                        "${ScheduleContract.Sessions.SESSION_TITLE}) " +
                        "VALUES (?, ?, ?, ?, ?, ?)"

                const val ID = 1
                const val DESCRIPTION = 2
                const val END_TIMESTAMP = 3
                const val ROOM_ID = 4
                const val START_TIMESTAMP = 5
                const val TITLE = 6
            }
        }

        private interface SessionSpeaker {
            companion object {
                const val STATEMENT = "INSERT INTO ${ScheduleDatabase.Tables.SESSIONS_SPEAKERS} (" +
                        "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID}, " +
                        "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SPEAKER_ID}) " +
                        "VALUES (?, ?)"

                const val SESSION_ID = 1
                const val SPEAKER_ID = 2
            }
        }
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
        val statement = database.compileStatement(Inserts.Room.STATEMENT)
        schedule.rooms?.forEach { room ->
            statement.safeBindString(Inserts.Room.ID, room.id)
            statement.safeBindString(Inserts.Room.NAME, room.name)
            statement.executeInsert()
        }
    }

    private fun insertSpeakers(database: SQLiteDatabase, schedule: Schedule) {
        val statement = database.compileStatement(Inserts.Speaker.STATEMENT)
        schedule.speakers?.forEach { speaker ->
            statement.safeBindString(Inserts.Speaker.ID, speaker.id)
            statement.safeBindString(Inserts.Speaker.BIO, speaker.bio)
            statement.safeBindString(Inserts.Speaker.COMPANY, speaker.company)
            statement.safeBindString(Inserts.Speaker.COUNTRY, speaker.country)
            statement.safeBindString(Inserts.Speaker.NAME, speaker.name)
            statement.safeBindString(Inserts.Speaker.PHOTO_URL, speaker.photoUrl)
            statement.safeBindString(Inserts.Speaker.TAGS, JsonConverters.main.toJson(speaker.tags))
            statement.executeInsert()
        }
    }

    private fun insertSessions(database: SQLiteDatabase, schedule: Schedule) {
        val statement = database.compileStatement(Inserts.Session.STATEMENT)
        val statementRelationship = database.compileStatement(Inserts.SessionSpeaker.STATEMENT)
        schedule.sessions?.forEach { session ->
            statement.safeBindString(Inserts.Session.ID, session.id)
            statement.safeBindString(Inserts.Session.DESCRIPTION, session.description)
            statement.safeBindLong(Inserts.Session.END_TIMESTAMP, session.endTimestamp.timeAsSeconds)
            statement.safeBindString(Inserts.Session.ROOM_ID, session.room)
            statement.safeBindLong(Inserts.Session.START_TIMESTAMP, session.startTimestamp.timeAsSeconds)
            statement.safeBindString(Inserts.Session.TITLE, session.title)
            statement.executeInsert()

            session.speakers.forEach {
                statementRelationship.safeBindString(Inserts.SessionSpeaker.SESSION_ID, session.id)
                statementRelationship.safeBindString(Inserts.SessionSpeaker.SPEAKER_ID, it)
                statementRelationship.executeInsert()
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

    private fun SQLiteStatement.safeBindString(index: Int, value: String?) {
        if (value != null) {
            bindString(index, value)
        } else {
            bindNull(index)
        }
    }

    private fun SQLiteStatement.safeBindLong(index: Int, value: Long?) {
        if (value != null) {
            bindLong(index, value)
        } else {
            bindNull(index)
        }
    }

}
