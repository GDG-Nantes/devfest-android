package com.gdgnantes.devfest.android.provider

import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.gdgnantes.devfest.android.content.SQLiteContentProvider
import com.gdgnantes.devfest.android.database.SelectionBuilder


class ScheduleProvider : SQLiteContentProvider() {

    companion object {

        private const val TAG = "ScheduleProvider"

        private const val LOG_METHOD_ENABLED = false
        private const val DEBUG_LOG_QUERIES = false

        private const val FIRST = 100

        private const val ROOMS = FIRST
        private const val ROOMS_ID = FIRST + 1

        private const val SESSIONS = ROOMS + 100
        private const val SESSIONS_ID = SESSIONS + 1

        private const val SESSIONS_SPEAKERS = SESSIONS + 100

        private const val SPEAKERS = SESSIONS_SPEAKERS + 100
        private const val SPEAKERS_ID = SPEAKERS + 1

        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            with(uriMatcher) {
                addURI(ScheduleContract.AUTHORITY, "rooms", ROOMS)
                addURI(ScheduleContract.AUTHORITY, "rooms/#", ROOMS_ID)

                addURI(ScheduleContract.AUTHORITY, "sessions", SESSIONS)
                addURI(ScheduleContract.AUTHORITY, "sessions/#", SESSIONS_ID)

                addURI(ScheduleContract.AUTHORITY, "sessions_speakers", SESSIONS_SPEAKERS)

                addURI(ScheduleContract.AUTHORITY, "speakers", SPEAKERS)
                addURI(ScheduleContract.AUTHORITY, "speakers/#", SPEAKERS_ID)
            }
        }
    }

    override fun createOpenHelper(context: Context): SQLiteOpenHelper {
        return ScheduleDatabase(context)
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor {
        if (LOG_METHOD_ENABLED) {
            Log.i(TAG, "query(uri=" + uri +
                    ", proj=" + projection?.contentToString() +
                    ", selection=" + selection +
                    ", selectionArgs=" + selectionArgs?.contentToString() +
                    ", sortOrder=" + sortOrder + ")")
        }

        val qb = SQLiteQueryBuilder()
        val db = openHelper.readableDatabase

        when (uriMatcher.match(uri)) {
            ROOMS -> {
                qb.tables = ScheduleDatabase.Tables.ROOMS
            }
            ROOMS_ID -> {
                qb.tables = ScheduleDatabase.Tables.ROOMS
                qb.appendWhere("${ScheduleContract.Rooms.ROOM_ID} = '${ScheduleContract.Rooms.getId(uri)}'")
            }
            SESSIONS -> {
                qb.tables = ScheduleDatabase.Tables.SESSIONS_V_ROOMS
            }
            SESSIONS_ID -> {
                qb.tables = ScheduleDatabase.Tables.SESSIONS_V_ROOMS
                qb.appendWhere("${ScheduleContract.Sessions.SESSION_ID} = '${ScheduleContract.Sessions.getId(uri)}'")
            }
            SESSIONS_SPEAKERS -> {
                qb.tables = ScheduleDatabase.Tables.SESSIONS_SPEAKERS_V_SESSIONS_V_SPEAKERS_V_ROOMS
            }
            SPEAKERS -> {
                qb.tables = ScheduleDatabase.Tables.SPEAKERS
            }
            SPEAKERS_ID -> {
                qb.tables = ScheduleDatabase.Tables.SPEAKERS
                qb.appendWhere("${ScheduleContract.Speakers.SPEAKER_ID} = '${ScheduleContract.Speakers.getId(uri)}'")
            }
            else -> throw UnknownUriException(uri)
        }

        return query(uri, db, qb, projection, selection, selectionArgs, sortOrder);
    }

    override fun insertInTransaction(uri: Uri, values: ContentValues, callerIsSyncAdapter: Boolean): Uri {
        if (LOG_METHOD_ENABLED) {
            Log.i(TAG, "insertInTransaction(uri=$uri, values=$values)")
        }

        val db = openHelper.writableDatabase

        return when (uriMatcher.match(uri)) {
            ROOMS -> {
                insert(uri, db, values, ScheduleDatabase.Tables.ROOMS)
                ScheduleContract.Rooms.buildUri(values.getAsString(ScheduleContract.Rooms.ROOM_ID))
            }
            SESSIONS -> {
                insert(uri, db, values, ScheduleDatabase.Tables.SESSIONS)
                ScheduleContract.Sessions.buildUri(values.getAsString(ScheduleContract.Sessions.SESSION_ID))
            }
            SESSIONS_SPEAKERS -> {
                TODO("Deal with SESSIONS_SPEAKERS insert")
            }
            SPEAKERS -> {
                insert(uri, db, values, ScheduleDatabase.Tables.SPEAKERS)
                ScheduleContract.Speakers.buildUri(values.getAsString(ScheduleContract.Speakers.SPEAKER_ID))
            }
            else -> throw UnknownUriException(uri)
        }

    }

    override fun updateInTransaction(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?, callerIsSyncAdapter: Boolean): Int {
        if (LOG_METHOD_ENABLED) {
            Log.i(TAG, "updateInTransaction(uri=$uri)")
        }
        return update(uri, openHelper.writableDatabase, values, selection, selectionArgs)
    }

    override fun deleteInTransaction(uri: Uri, selection: String?, selectionArgs: Array<String>?, callerIsSyncAdapter: Boolean): Int {
        if (LOG_METHOD_ENABLED) {
            Log.i(TAG, "deleteInTransaction(uri=$uri)")
        }
        return delete(uri, openHelper.writableDatabase, selection, selectionArgs)
    }

    override fun getType(uri: Uri): String = when (uriMatcher.match(uri)) {
        ROOMS -> ScheduleContract.Rooms.CONTENT_TYPE
        ROOMS_ID -> ScheduleContract.Rooms.CONTENT_ITEM_TYPE
        SESSIONS -> ScheduleContract.Sessions.CONTENT_TYPE
        SESSIONS_ID -> ScheduleContract.Sessions.CONTENT_ITEM_TYPE
        SESSIONS_SPEAKERS -> ScheduleContract.SessionsSpeakers.CONTENT_TYPE
        SPEAKERS -> ScheduleContract.Speakers.CONTENT_TYPE
        SPEAKERS_ID -> ScheduleContract.Speakers.CONTENT_ITEM_TYPE
        else -> throw UnknownUriException(uri)
    }

    private fun query(uri: Uri, db: SQLiteDatabase, qb: SQLiteQueryBuilder, projection: Array<String>?,
                      selection: String?, selectionArgs: Array<String>?, sortOrder: String?, groupBy: String? = null,
                      having: String? = null, limit: String? = null): Cursor {
        qb.setStrict(true)

        if (DEBUG_LOG_QUERIES) {
            Log.i(TAG, "${qb.buildQuery(projection, selection, groupBy, having, sortOrder, limit)} " +
                    "with args: ${selectionArgs?.contentToString()}")
        }

        val c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit)
        if (c != null) {
            val resolver = context?.contentResolver
            if (resolver != null) {
                c.setNotificationUri(resolver, ScheduleContract.CONTENT_URI)
            }
        }
        return c
    }


    private fun insert(uri: Uri, db: SQLiteDatabase, values: ContentValues, table: String): Long {
        try {
            val count = db.insertOrThrow(table, null, values)
            if (count > 0) {
                postNotifyChange()
            }
            return count
        } catch (cause: Exception) {
            val callContext = "uri= $uri, " +
                    ", values= $values, " +
                    ", table= $table"
            throw RuntimeException(callContext, cause)
        }
    }

    private fun update(uri: Uri, db: SQLiteDatabase, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        try {
            val count = buildSimpleSelection(uri)
                    .where(selection, *(selectionArgs ?: emptyArray()))
                    .update(db, values)
            if (count > 0) {
                postNotifyChange()
            }
            return count
        } catch (cause: Exception) {
            val callContext = "uri= $uri, " +
                    ", values= $values, " +
                    ", selection= $selection, " +
                    ", selectionArgs= ${selectionArgs?.contentToString()}"
            throw RuntimeException(callContext, cause)
        }
    }

    private fun delete(uri: Uri, db: SQLiteDatabase, selection: String?, selectionArgs: Array<String>?): Int {
        try {
            val count = buildSimpleSelection(uri)
                    .where(selection, *(selectionArgs ?: emptyArray()))
                    .delete(db)
            if (count > 0) {
                postNotifyChange()
            }
            return count
        } catch (cause: Exception) {
            val callContext = "uri= $uri, " +
                    ", selection= $selection, " +
                    ", selectionArgs= ${selectionArgs?.contentToString()}"
            throw RuntimeException(callContext, cause)
        }
    }

    private fun buildSimpleSelection(uri: Uri): SelectionBuilder {
        val builder = SelectionBuilder()
        return when (uriMatcher.match(uri)) {
            ROOMS -> builder.table(ScheduleDatabase.Tables.ROOMS)
            ROOMS_ID -> builder.table(ScheduleDatabase.Tables.ROOMS)
                    .where("${ScheduleContract.Rooms.ROOM_ID} = ?", ScheduleContract.Rooms.getId(uri))
            SESSIONS -> builder.table(ScheduleDatabase.Tables.SESSIONS)
            SESSIONS_ID -> builder.table(ScheduleDatabase.Tables.SESSIONS)
                    .where("${ScheduleContract.Sessions.SESSION_ID} = ?", ScheduleContract.Sessions.getId(uri))
            SPEAKERS -> builder.table(ScheduleDatabase.Tables.SPEAKERS)
            SPEAKERS_ID -> builder.table(ScheduleDatabase.Tables.SPEAKERS)
                    .where("${ScheduleContract.Speakers.SPEAKER_ID} = ?", ScheduleContract.Speakers.getId(uri))
            else -> throw UnknownUriException(uri)
        }
    }


    private fun postNotifyChange() {
        postNotifyChange(ScheduleContract.CONTENT_URI)
    }

}
