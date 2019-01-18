package io.locally.engagesdk.campaigns

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import io.locally.engagesdk.datamodels.campaign.CampaignContent

@SuppressLint("StaticFieldLeak")
object CampaignNotifier {

    private lateinit var context: Context
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private const val channelId = "campaign"

    fun init(context: Context) {
        CampaignNotifier.context = context
    }

    fun notify(content: CampaignContent, intent: Intent) {
        intent.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) //TODO("default notification icon")
                .setContentTitle(content.headerTitle)
                .setContentText(content.notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)){
            notify(content.id, notificationBuilder.build())
        }
    }
}