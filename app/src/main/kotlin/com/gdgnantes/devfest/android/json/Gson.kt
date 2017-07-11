package com.gdgnantes.devfest.android.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

inline fun <reified T : Any> typeToken(): Type
        = object : TypeToken<T>() {}.type

inline fun <reified T : Any> Gson.fromJson(json: String?): T
        = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJson(json: Reader): T
        = fromJson(json, typeToken<T>())
