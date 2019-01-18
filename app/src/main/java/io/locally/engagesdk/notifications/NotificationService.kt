package io.locally.engagesdk.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class NotificationService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.notification?.let {
            with(it){
                NotificationManager.sendPushNotification(NotificationContent(remoteContent = NotificationContent.RemoteContent(title, body)))
            }
        } ?: run {
            remoteMessage?.data?.isNotEmpty()?.let {
                if(it){
                    val data = remoteMessage.data["locally"]
                    val content = Gson().fromJson(data, NotificationContent.RemoteContent::class.java)

                    NotificationManager.sendPushNotification(NotificationContent(
                            remoteContent = NotificationContent.RemoteContent(title = content.title, link = content.link)))
                }
            }
        }
    }
}