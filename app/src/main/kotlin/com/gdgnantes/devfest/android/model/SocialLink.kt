package com.gdgnantes.devfest.android.model

import android.support.annotation.Keep

@Keep
class SocialLink(
        @JvmField val network: SocialNetwork? = null,
        @JvmField val url: String? = null)
