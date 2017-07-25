package com.gdgnantes.devfest.android.content

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import com.gdgnantes.devfest.android.app.PREFIX_ACTION
import com.gdgnantes.devfest.android.app.PREFIX_EXTRA
import com.gdgnantes.devfest.android.util.asArrayList

class RemindersReceiver : BroadcastReceiver() {

    companion object {
        private const val ACTION_SHOW_REMINDERS = "${PREFIX_ACTION}SHOW_REMINDERS"

        private const val EXTRA_REMINDER_INFOS = "${PREFIX_EXTRA}REMINDER_INFOS"

        private const val WAKE_LOCK_LIFE_TIME = 30 * 1000L

        fun newShowRemindersIntent(context: Context, reminderInfos: List<Bundle>): Intent
                = Intent(context, RemindersReceiver::class.java)
                .setAction(ACTION_SHOW_REMINDERS)
                .putParcelableArrayListExtra(EXTRA_REMINDER_INFOS, reminderInfos.asArrayList())
    }

    override fun onReceive(context: Context, intent: Intent) {
        val wakeLock = acquireWakeLock(context)
        val result = goAsync()
        Thread({
            val manager = RemindersManager.from(context)
            when (intent.action) {
                ACTION_SHOW_REMINDERS -> {
                    manager.showNotification(intent.getParcelableArrayListExtra<Bundle>(EXTRA_REMINDER_INFOS))
                }
            }
            manager.updateAlarm()

            wakeLock.release()
            result.finish()
        }).start()
    }

    private fun acquireWakeLock(context: Context): PowerManager.WakeLock {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "devfest-wake-lock").apply {
            setReferenceCounted(false)
            acquire(WAKE_LOCK_LIFE_TIME)
        }
    }

}