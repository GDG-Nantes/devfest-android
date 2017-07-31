package com.gdgnantes.devfest.android.app

object Build {

    enum class Versions(val versionCode: Int) {
        ANGERS(100),
        ANGERS_MR1(101)
    }

    val version = Versions.ANGERS_MR1

}