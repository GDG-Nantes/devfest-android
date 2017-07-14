package com.gdgnantes.devfest.android.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.support.annotation.Keep
import android.support.v4.graphics.ColorUtils
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.database.getDateOrThrow
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.gdgnantes.devfest.android.util.timeAsSeconds
import java.util.*

@Keep
data class Session(
        val id: String,
        val description: String?,
        val endTimestamp: Date,
        val language: String?,
        val roomId: String?,
        val startTimestamp: Date,
        val title: String,
        val track: Track?,
        val type: Type?,

        val speakersIds: List<String>? = null) {


    enum class Type(val apiValue: String,
                    private val typeName: Int) {

        Break("break", R.string.type_break),
        Codelab("codelab", R.string.type_codelab),
        Keynote("keynote", R.string.type_keynote),
        Quicky("quicky", R.string.type_quicky),
        Talk("talk", R.string.type_talk);

        fun getName(context: Context) = context.getString(typeName)

        companion object {
            fun get(apiValue: String?) = values().firstOrNull { apiValue == it.apiValue }
        }

    }

    enum class Track(val apiValue: String,
                     val backgroundColor: Int,
                     private val trackName: Int) {

        Cloud("cloud",
                0xff4285f4.toInt(),
                R.string.track_cloud),

        Discovery("discovery",
                0xffea4335.toInt(),
                R.string.track_discovery),

        Mobile("mobile",
                0xff34a853.toInt(),
                R.string.track_mobile),

        Web("web",
                0xfffbbc05.toInt(),
                R.string.track_web);

        val foregroundColor: Int
            get() = if (ColorUtils.calculateLuminance(backgroundColor) >= LUMINANCE_THRESHOLD) Color.BLACK else Color.WHITE

        fun getName(context: Context) = context.getString(trackName)

        companion object {
            private const val LUMINANCE_THRESHOLD = 0.85

            fun get(apiValue: String?) = values().firstOrNull { apiValue == it.apiValue }
        }

    }

}

fun Session.toContentValues() = ContentValues().apply {
    put(ScheduleContract.Sessions.SESSION_ID, id!!)
    put(ScheduleContract.Sessions.SESSION_DESCRIPTION, description)
    put(ScheduleContract.Sessions.SESSION_END_TIMESTAMP, endTimestamp!!.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_LANGUAGE, language)
    put(ScheduleContract.Sessions.SESSION_ROOM_ID, roomId)
    put(ScheduleContract.Sessions.SESSION_START_TIMESTAMP, startTimestamp!!.timeAsSeconds)
    put(ScheduleContract.Sessions.SESSION_TITLE, title!!)
    put(ScheduleContract.Sessions.SESSION_TRACK, track?.apiValue)
    put(ScheduleContract.Sessions.SESSION_TYPE, type?.apiValue)
}

fun Cursor.toSession() = Session(
        id = getStringOrThrow(ScheduleContract.Sessions.SESSION_ID)!!,
        description = getStringOrThrow(ScheduleContract.Sessions.SESSION_DESCRIPTION),
        endTimestamp = getDateOrThrow(ScheduleContract.Sessions.SESSION_END_TIMESTAMP)!!,
        language = getStringOrThrow(ScheduleContract.Sessions.SESSION_LANGUAGE),
        roomId = getStringOrThrow(ScheduleContract.Sessions.SESSION_ROOM_ID),
        startTimestamp = getDateOrThrow(ScheduleContract.Sessions.SESSION_START_TIMESTAMP)!!,
        title = getStringOrThrow(ScheduleContract.Sessions.SESSION_TITLE)!!,
        track = Session.Track.get(getStringOrThrow(ScheduleContract.Sessions.SESSION_TRACK)),
        type = Session.Type.get(getStringOrThrow(ScheduleContract.Sessions.SESSION_TYPE))
)
