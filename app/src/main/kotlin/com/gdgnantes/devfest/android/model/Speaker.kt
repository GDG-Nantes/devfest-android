package com.gdgnantes.devfest.android.model

import android.database.Cursor
import android.support.annotation.Keep
import com.gdgnantes.devfest.android.database.getStringOrThrow
import com.gdgnantes.devfest.android.http.JsonConverters
import com.gdgnantes.devfest.android.json.fromJson
import com.gdgnantes.devfest.android.provider.ScheduleContract

@Keep
data class Speaker(
        val id: String,
        val bio: String?,
        val company: String?,
        val country: String?,
        val name: String?,
        val photoUrl: String?,
        val tags: List<String>?)

fun Cursor.toSpeaker(): Speaker {
    return Speaker(
            id = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_ID),
            bio = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_BIO),
            company = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_COMPANY),
            country = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_COUNTRY),
            name = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_NAME),
            photoUrl = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_PHOTO_URL),
            tags = JsonConverters.main.fromJson(getStringOrThrow(ScheduleContract.Speakers.SPEAKER_TAGS))
    )
}
