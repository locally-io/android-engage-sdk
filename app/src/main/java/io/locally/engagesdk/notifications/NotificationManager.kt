package io.locally.engagesdk.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.SubscribeRequest
import io.locally.engagesdk.R
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.network.services.notifications.NotificationServices
import org.jetbrains.anko.doAsync

@SuppressLint("StaticFieldLeak")
object NotificationManager {
    private lateinit var context: Context
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var client: AmazonSNSClient
    private lateinit var provider: CognitoCachingCredentialsProvider
    private const val channelId = "engage-notification"

    fun init(context: Context) {
        NotificationManager.context = context
    }

    fun sendNotification(content: NotificationContent, intent: Intent) {
        if(::context.isInitialized) notify(content, intent)
    }

    fun sendPushNotification(content: NotificationContent){
        if(::context.isInitialized){
            content.remoteContent?.let { remote ->
                remote.link?.let {
                    try {
                        val data = Uri.parse(it)
                        val intent = Intent(Intent.ACTION_VIEW, data)

                        notify(content, intent)
                    }catch(e: Exception) { println("Error trying to send Notification: ${e.localizedMessage}") }
                } ?: run {
                    val intent = Intent().apply {
                        component = ComponentName(context, Utils.appName)
                    }

                    notify(content, intent)
                }
            }
        }
    }

    fun subscribe(callback: ((Boolean) -> Unit)? = null) {
        provider = CognitoCachingCredentialsProvider(context, Utils.poolId, Regions.US_WEST_2)
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

    private fun notify(notificationContent: NotificationContent, intent: Intent?) {
        val title = notificationContent.campaignContent?.headerTitle ?: notificationContent.remoteContent?.title
        val message = notificationContent.campaignContent?.notificationMessage ?: notificationContent.remoteContent?.link

        if(notificationContent.remoteContent?.link.isNullOrEmpty())
            intent?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            val id = notificationContent.campaignContent?.id ?: 1
            notify(id, notificationBuilder.build())
        }
    }
}