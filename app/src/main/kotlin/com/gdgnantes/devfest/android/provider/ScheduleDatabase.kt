package com.gdgnantes.devfest.android.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class ScheduleDatabase(private val context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "schedule.db"
        private const val DATABASE_VERSION = 1
    }

    object Tables {
        const val ROOMS = "Rooms"
        const val SESSIONS = "Sessions"
        const val SESSIONS_SPEAKERS = "SessionsSpeakers"
        const val SPEAKERS = "Speakers"

        const val SESSIONS_LJ_ROOMS = "Sessions " +
                "LEFT JOIN Rooms ON session_room_id = room_id"

        const val SESSIONS_SPEAKERS_J_SESSIONS_J_SPEAKERS_LJ_ROOMS = "SessionsSpeakers " +
                "JOIN Sessions ON session_speaker_session_id = session_id " +
                "JOIN Speakers ON session_speaker_speaker_id = speaker_id " +
                "LEFT JOIN Rooms ON session_room_id = room_id"
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE ${Tables.ROOMS} (" +
                "${ScheduleContract.Rooms.ROOM_ID} TEXT NOT NULL, " +
                "${ScheduleContract.Rooms.ROOM_NAME} TEXT, " +
                "UNIQUE (${ScheduleContract.Rooms.ROOM_ID}) ON CONFLICT REPLACE)")

        db.execSQL("CREATE TABLE ${Tables.SESSIONS} (" +
                "${ScheduleContract.Sessions.SESSION_ID} TEXT NOT NULL, " +
                "${ScheduleContract.Sessions.SESSION_DESCRIPTION} TEXT, " +
                "${ScheduleContract.Sessions.SESSION_END_TIMESTAMP} INTEGER, " +
                "${ScheduleContract.Sessions.SESSION_LANGUAGE} TEXT, " +
                "${ScheduleContract.Sessions.SESSION_ROOM_ID} TEXT, " +
                "${ScheduleContract.Sessions.SESSION_START_TIMESTAMP} INTEGER, " +
                "${ScheduleContract.Sessions.SESSION_TITLE} TEXT, " +
                "${ScheduleContract.Sessions.SESSION_TRACK} TEXT, " +
                "${ScheduleContract.Sessions.SESSION_TYPE} TEXT, " +
                "UNIQUE (${ScheduleContract.Sessions.SESSION_ID}) ON CONFLICT REPLACE)")

        db.execSQL("CREATE TABLE ${Tables.SESSIONS_SPEAKERS} (" +
                "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID} TEXT NOT NULL, " +
                "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SPEAKER_ID} TEXT NOT NULL, " +
                "UNIQUE (" +
                "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID}, " +
                "${ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SPEAKER_ID}) " +
                "ON CONFLICT REPLACE)")

        db.execSQL("CREATE TABLE ${Tables.SPEAKERS} (" +
                "${ScheduleContract.Speakers.SPEAKER_ID} TEXT NOT NULL, " +
                "${ScheduleContract.Speakers.SPEAKER_BIO} TEXT, " +
                "${ScheduleContract.Speakers.SPEAKER_COMPANY} TEXT, " +
                "${ScheduleContract.Speakers.SPEAKER_NAME} TEXT, " +
                "${ScheduleContract.Speakers.SPEAKER_PHOTO_URL} TEXT, " +
                "${ScheduleContract.Speakers.SPEAKER_SOCIAL_LINKS} TEXT, " +
                "UNIQUE (${ScheduleContract.Speakers.SPEAKER_ID}) ON CONFLICT REPLACE)")

        ScheduleSeed(context).seed(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Tables.ROOMS}")
        db.execSQL("DROP TABLE IF EXISTS ${Tables.SESSIONS}")
        db.execSQL("DROP TABLE IF EXISTS ${Tables.SESSIONS_SPEAKERS}")
        db.execSQL("DROP TABLE IF EXISTS ${Tables.SPEAKERS}")
        onCreate(db)
    }

}

