package com.gdgnantes.devfest.android

import java.util.*

object AppConfig {

    val SCHEMES = listOf("https")
    val AUTHORITIES = listOf("devfest.gdgnantes.com")

    val PATH_SESSIONS = "sessions"

    val ENDPOINT = "https://devfest2017.gdgnantes.com/api/v1/"

    val SEED_ETAG = "W/\"kIeLgl9nKEP3vltt67h29w==\""

    val EVENT_DATES = listOf("2017-10-19", "2017-10-20")
    val EVENT_TIMEZONE = TimeZone.getTimeZone("Europe/Paris")

}
