@file:JvmName("NotifierUtils")

package com.chiclaim.android8.change

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import java.io.File
import kotlin.random.Random


/**
 *
 * Created by kumu@2dfire.com on 2022/11/9.
 */
class NotifierUtils private constructor() {

    companion object {
        private const val CHANNEL_ID = "my_channel_id"
        private const val CHANNEL_GROUP_A = "channel_group A"


        private fun getNotificationManager(context: Context): NotificationManager? {
            return context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as? NotificationManager
        }

        fun sendNotification(context: Context) {

            val notificationManager = getNotificationManager(context) ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // 根据需要创建 Channel 分组（可选）
                val groupA = NotificationChannelGroup(CHANNEL_GROUP_A, "分组A")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    groupA.description = "channel_group A description"
                }

                notificationManager.createNotificationChannelGroup(groupA)

                // 创建 Channel（必选）
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "my_channel_name",
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.group = CHANNEL_GROUP_A
                channel.description = "my channel description"
                channel.setShowBadge(true) // 是否显示角标
                notificationManager.createNotificationChannel(channel)
            }


            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Name")
                .setContentText("context text")
//                .setAutoCancel(false) // slide cancel
//                .setOngoing(true)
//                .setNumber(2)  // messageCount
                .setSubText("sub text")
                .setContentInfo("content info")// don't use setContentInfo(deprecated in API level 24)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
//                .setGroup("my_notification_group")
//                .setStyle(NotificationCompat.BigTextStyle().setSummaryText("setSummaryText"))
//                .setGroupSummary(true)

            notificationManager.notify(
                1111,
                builder.build()
            )
        }

    }


}