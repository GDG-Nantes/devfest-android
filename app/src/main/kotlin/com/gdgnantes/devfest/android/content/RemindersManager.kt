package com.gdgnantes.devfest.android.content

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.gdgnantes.devfest.android.BookmarkManager
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.SessionActivity
import com.gdgnantes.devfest.android.database.sqlIn
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.model.toRoom
import com.gdgnantes.devfest.android.model.toSession
import com.gdgnantes.devfest.android.provider.ScheduleContract
import java.util.*

class RemindersManager private constructor(private val context: Context) {

    // NOTE Cyril
    // The current implementation of the reminders consists on
    // setting on alarm (in the AlarmManager) that will be triggered
    // exactly REMINDER_ANTICIPATION milliseconds before the next
    // (in time) bookmarked event. The alarm's PendingIntent
    // contains all of the information of the next bookmarked events.
    // Note that it's event*s* because, although it should be quite
    // rare, there may be several bookmarked events at the same
    // time.
    //
    // Once triggered, the alarm will display a notification on a
    // per-event basis. In case multiple bookmarked event start at
    // the same time, the system will hence trigger several
    // notifications at the same time.

    companion object {
        const val CHANNEL_REMINDERS = "channel:reminders"

        private const val TAG = "RemindersManager"

        private const val KEY_ID = "key:id"
        private const val KEY_TITLE = "key:title"
        private const val KEY_TEXT = "key:text"

        private const val NOTIFICATION_ID = 666

        private const val REMINDER_ANTICIPATION = 5 * 60 * 1000L // 5 minutes

        private var instance: RemindersManager? = null

        fun from(context: Context): RemindersManager {
            synchronized(RemindersManager::class) {
                if (instance == null) {
                    instance = RemindersManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }

    fun updateAlarm() {
        val bookmarkIds = BookmarkManager.from(context).getLiveData().value ?: emptySet()

        val cursor = context.contentResolver.query(
                ScheduleContract.Sessions.CONTENT_URI,
                null,
                "${sqlIn(ScheduleContract.Sessions.SESSION_ID, bookmarkIds)} AND " +
                        "${ScheduleContract.Sessions.SESSION_START_TIMESTAMP} > strftime('%s', 'now') + ${REMINDER_ANTICIPATION / 1000}",
                null,
                "${ScheduleContract.Sessions.SESSION_START_TIMESTAMP} ASC")

        var nextAlarm: Date? = null

        val reminderInfos = mutableListOf<Bundle>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val session = cursor.toSession()
                val room = cursor.toRoom()

                if (nextAlarm != null && nextAlarm != session.startTimestamp) {
                    break
                }
                nextAlarm = session.startTimestamp

                val reminderInfo = Bundle().apply {
                    putString(KEY_ID, session.id)
                    putString(KEY_TITLE, session.title)
                    putString(KEY_TEXT, context.getString(R.string.reminders_text, DateTimeFormatter.formatHHmm(session.startTimestamp), room.name))
                }
                reminderInfos.add(reminderInfo)
            }
            cursor.close()
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0,
                RemindersReceiver.newShowRemindersIntent(context, reminderInfos), PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        if (nextAlarm != null) {
            val actualAlarm = Date(nextAlarm.time - REMINDER_ANTICIPATION)
            Log.d(TAG, "Scheduling alarm: ${DateTimeFormatter.formatEEEEMMMMd(actualAlarm)} ${DateTimeFormatter.formatHHmm(actualAlarm)}")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, actualAlarm.time, pendingIntent)
        } else {
            Log.d(TAG, "Removing alarms")
        }
    }

    fun showNotification(reminderInfos: List<Bundle>) {
        reminderInfos.forEach {
            val id = it.getString(KEY_ID)
            val title = it.getString(KEY_TITLE)
            val text = it.getString(KEY_TEXT)

            val pendingIntent = PendingIntent.getActivity(context, 0,
                    SessionActivity.newIntent(context, id), 0)

            val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(ContextCompat.getColor(context, R.color.candy))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setShowWhen(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_status_devfest)
                    .setTicker(title)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .build()

            NotificationManagerCompat.from(context)
                    .notify(id, NOTIFICATION_ID, notification)
        }
    }

}