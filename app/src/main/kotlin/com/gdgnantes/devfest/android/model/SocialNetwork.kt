package com.gdgnantes.devfest.android.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.gdgnantes.devfest.android.R

enum class SocialNetwork(
        val apiValue: String,
        private val icon: Int,
        private val networkName: Int) {

    Twitter("twitter", R.drawable.notification_icon_background, R.string.app_name),
    GooglePlus("gplus", R.drawable.notification_icon_background, R.string.app_name),
    Website("website", R.drawable.notification_icon_background, R.string.app_name),
    GitHub("github", R.drawable.notification_icon_background, R.string.app_name);

    fun getIcon(context: Context): Drawable = ContextCompat.getDrawable(context, icon)

    fun getNetworkName(context: Context): String = context.getString(networkName)
}