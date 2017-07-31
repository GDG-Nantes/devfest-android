package com.gdgnantes.devfest.android.model

import android.content.ContentValues
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
        val name: String?,
        val photoUrl: String?,
        val socialLinks: List<SocialLink>)

fun Speaker.toContentValues() = ContentValues().apply {
    put(ScheduleContract.Speakers.SPEAKER_ID, id!!)
    put(ScheduleContract.Speakers.SPEAKER_BIO, bio)
    put(ScheduleContract.Speakers.SPEAKER_COMPANY, company)
    put(ScheduleContract.Speakers.SPEAKER_NAME, name)
    put(ScheduleContract.Speakers.SPEAKER_PHOTO_URL, photoUrl)
    put(ScheduleContract.Speakers.SPEAKER_SOCIAL_LINKS, JsonConverters.main.toJson(socialLinks!!))
}

fun Cursor.toSpeaker() = Speaker(
        id = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_ID)!!,
        bio = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_BIO),
        company = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_COMPANY),
        name = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_NAME),
        photoUrl = getStringOrThrow(ScheduleContract.Speakers.SPEAKER_PHOTO_URL),
        socialLinks = JsonConverters.main.fromJson(getStringOrThrow(ScheduleContract.Speakers.SPEAKER_SOCIAL_LINKS))
)
