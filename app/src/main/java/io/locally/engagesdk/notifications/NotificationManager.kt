package io.locally.engagesdk.notifications

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.SubscribeRequest
import com.squareup.picasso.Picasso
import io.locally.engagesdk.R
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.managers.TokenManager
import io.locally.engagesdk.network.services.notifications.NotificationServices
import io.locally.engagesdk.notifications.NotificationManager.NOTIFICATION.E_RICH_PUSH_NOTIFICATION
import org.jetbrains.anko.doAsync

@SuppressLint("StaticFieldLeak")
object NotificationManager {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var client: AmazonSNSClient
    private lateinit var provider: CognitoCachingCredentialsProvider
    private const val channelId = "engage-notification"
    private const val channelName = "eng-notif-content"

    fun sendNotification(context: Context, content: NotificationContent, intent: Intent) {
        notify(context, content, intent)
    }

    fun sendPushNotification(context: Context, content: NotificationContent) {
        content.remoteContent?.let { remote ->
            remote.link?.let {
                try {
                    val data = Uri.parse(it)
                    val intent = Intent(Intent.ACTION_VIEW, data)

                    notify(context, content, intent)
                } catch(e: Exception) {
                    println("Error trying to send Notification: ${e.localizedMessage}")
                }
            } ?: run {
                val intent = Intent().apply {
                    component = ComponentName(context, Utils.appName)
                }

                notify(context, content, intent)
            }
        }
    }

    fun subscribe(activity: Activity, callback: ((Boolean) -> Unit)? = null) {
        provider = CognitoCachingCredentialsProvider(activity, Utils.poolId, Regions.US_WEST_2)
        client = AmazonSNSClient(provider)
        client.setRegion(Region.getRegion(Regions.US_WEST_2))


        doAsync {
            NotificationServices.subscribe()
                    .subscribe({ result ->
                        val request = SubscribeRequest().apply {
                            withTopicArn(Utils.sns)
                            withProtocol("application")
                            withEndpoint(result.data.endpoint)
                        }

                        client.subscribe(request)
                        callback?.invoke(true)
                    }, { error ->
                        callback?.invoke(false)
                        println("Error enabling push notifications: ${error.localizedMessage}")
                    })
        }
    }

    private fun notify(context: Context, notificationContent: NotificationContent, intent: Intent?) {
        val title = notificationContent.campaignContent?.headerTitle
                ?: notificationContent.remoteContent?.title
        val message = notificationContent.campaignContent?.notificationMessage
                ?: notificationContent.remoteContent?.link
        val requestCode = notificationContent.campaignContent?.id ?: 1
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, android.app.NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(context, "content-deliver")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)

        with(notificationManager) {
            val id = notificationContent.campaignContent?.id ?: 1
            if(TokenManager.isTokenValid) notify(id, notificationBuilder.build())
        }
    }

    fun sendCampaignNotification(context: Context, campaignContent: CampaignContent) {
        val requestCode = campaignContent.id
        var title = "";
        var desc = "";
        var bitmap: Bitmap;
        var uri: Uri? = null
        try {
            title = campaignContent.headerTitle
            desc = campaignContent.attributes.pushMessage
            uri = Uri.parse(campaignContent.attributes.link)
        } catch(e: Exception) {
            println("Error getting notification values: ${e.localizedMessage}")
        }

        doAsync {
            when(NOTIFICATION.valueOf(campaignContent.subLayout.toUpperCase())) {
                E_RICH_PUSH_NOTIFICATION -> {
                    try {
                        val bStyle = NotificationCompat.BigPictureStyle()
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        bitmap = Picasso.get().load(campaignContent.mediaImage.url).get()
                        bStyle.bigPicture(bitmap)
                        val mBuilder = NotificationCompat.Builder(context, "rich-notification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(desc)
                                .setStyle(bStyle)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                        val notificationId = campaignContent.id
                        val nManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                        nManager.notify(notificationId, mBuilder.build())
                    } catch(e: Exception) {
                        println("Error trying to send notification: ${e.localizedMessage}")
                    }
                }
                else -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        val mBuilder = NotificationCompat.Builder(context, "simple-notification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(desc)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                        val notificationId = campaignContent.id
                        val nManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                        nManager.notify(notificationId, mBuilder.build())
                    } catch(e: Exception) {
                        println("Error trying to send notification: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    private enum class NOTIFICATION {
        E_RICH_PUSH_NOTIFICATION,
        E_SIMPLE_PUSH_NOTIFICATION
    }
}