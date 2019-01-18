package io.locally.engagesdk.network.services.notifications

import io.locally.engagesdk.notifications.NotificationRequest
import io.locally.engagesdk.notifications.NotificationResponse
import io.reactivex.Observable

class NotificationServices {

    companion object {
        fun subscribe(): Observable<NotificationResponse> {
            return NotificationAPI.instance.subscribe(NotificationRequest())
        }
    }
}