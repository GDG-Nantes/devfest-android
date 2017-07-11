package com.gdgnantes.devfest.android.http

import com.gdgnantes.devfest.android.model.Session
import com.gdgnantes.devfest.android.model.SocialNetwork
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object JsonConverters {

    val main: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(Session.Track::class.java, SessionTrackTypeAdapter().nullSafe())
            .registerTypeAdapter(Session.Type::class.java, SessionTypeTypeAdapter().nullSafe())
            .registerTypeAdapter(SocialNetwork::class.java, SocialNetworkTypeAdapter().nullSafe())
            .create()

    private class SessionTrackTypeAdapter : TypeAdapter<Session.Track>() {
        override fun write(writer: JsonWriter, sessionTrack: Session.Track) {
            writer.value(sessionTrack.apiValue)
        }

        override fun read(reader: JsonReader): Session.Track? {
            return Session.Track.get(reader.nextString())
        }
    }

    private class SessionTypeTypeAdapter : TypeAdapter<Session.Type>() {
        override fun write(writer: JsonWriter, sessionType: Session.Type) {
            writer.value(sessionType.apiValue)
        }

        override fun read(reader: JsonReader): Session.Type? {
            return Session.Type.get(reader.nextString())
        }
    }

    private class SocialNetworkTypeAdapter : TypeAdapter<SocialNetwork>() {
        override fun write(writer: JsonWriter, socialNetwork: SocialNetwork) {
            writer.value(socialNetwork.apiValue)
        }

        override fun read(reader: JsonReader): SocialNetwork? {
            return SocialNetwork.get(reader.nextString())
        }
    }

}