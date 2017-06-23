package com.gdgnantes.devfest.android.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

inline fun <reified T : Any> typeToken(): Type
        = object : TypeToken<T>() {}.type

inline fun <reified T : Any> Gson.getAdapter(): TypeAdapter<T>
        = getAdapter(object : TypeToken<T>() {})

inline fun <reified T : Any> Gson.fromJson(json: String): T
        = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJson(json: Reader): T
        = fromJson(json, typeToken<T>())

//inline fun <reified T : Any> Gson.fromJson(json: JsonReader): T
//        = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJson(json: JsonElement): T
        = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.toJson(src: Any): String
        = toJson(src, typeToken<T>())

inline fun <reified T : Any> Gson.toJson(src: Any, writer: Appendable): Unit
        = toJson(src, typeToken<T>(), writer)

//inline fun <reified T : Any> Gson.toJson(src: Any, writer: JsonWriter): Unit
//        = toJson(src, typeToken<T>(), writer)