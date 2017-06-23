package com.gdgnantes.devfest.android.model

import com.google.gson.annotations.SerializedName

data class SocialLink(
        @SerializedName("social_network") val socialNetwork: SocialNetwork,
        @SerializedName("link") val link: String)
