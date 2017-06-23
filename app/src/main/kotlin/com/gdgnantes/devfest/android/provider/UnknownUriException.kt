package com.gdgnantes.devfest.android.provider

import android.net.Uri

internal class UnknownUriException private constructor(text: String) : UnsupportedOperationException(text) {
    constructor(uri: Uri) : this("Unknown uri: " + uri)
}
