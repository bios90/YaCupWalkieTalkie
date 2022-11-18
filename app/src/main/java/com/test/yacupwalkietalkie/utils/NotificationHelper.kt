package com.test.yacupwalkietalkie.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.test.yacupwalkietalkie.R
import com.test.yacupwalkietalkie.base.App

object NotificationHelper {
    const val SOCKET_NOTIFICATION_ID = 101
    private const val CHANNEL_ID = "yacup_walkie_talkie"
    private const val CHANNEL_NAME = "YaCup Walkie Talkie Channel"

    fun getNotificationChanelId() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = App.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            CHANNEL_ID
        } else {
            ""
        }

    fun showSocketListeningNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(App.app, getNotificationChanelId())
        return notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_cup_white)
            .setContentTitle(App.app.getString(R.string.notification_title))
            .setContentText(App.app.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }
}
