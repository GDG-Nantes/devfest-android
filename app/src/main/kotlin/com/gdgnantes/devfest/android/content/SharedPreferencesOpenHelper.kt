package com.gdgnantes.devfest.android.content

import android.content.Context
import android.content.SharedPreferences

abstract class SharedPreferencesOpenHelper(
        private val context: Context,
        private val name: String,
        private val newVersion: Int) {

    companion object {
        private val PREFERENCES_VERSION = "_preferencesVersion"
    }

    init {
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(newVersion >= 1) { "Version must be >= 1, was $newVersion" }
    }

    val sharedPreferences: SharedPreferences by lazy {
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val version = preferences.getInt(PREFERENCES_VERSION, 0)
        if (version != newVersion) {
            val editor = preferences.edit()
            try {
                if (version == 0) {
                    onCreate(editor)
                } else {
                    if (version < newVersion) {
                        onUpgrade(editor, version, newVersion)
                    } else {
                        onDowngrade(editor, version, newVersion)
                    }
                }
            } finally {
                editor.apply()
            }
            // Because the client may have cleared all of the values from the
            // Editor, we need to create another Editor just to update the version
            preferences.edit()
                    .putInt(PREFERENCES_VERSION, newVersion)
                    .apply()
        }
        preferences
    }

    open fun onCreate(editor: SharedPreferences.Editor) {}

    open fun onDowngrade(editor: SharedPreferences.Editor, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException(
                "Can't downgrade preference from version $oldVersion to $newVersion")
    }

    abstract fun onUpgrade(editor: SharedPreferences.Editor, oldVersion: Int, newVersion: Int)

}
