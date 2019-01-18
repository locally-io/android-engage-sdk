package io.locally.engagesdk.network.services.notifications

import io.locally.engagesdk.network.RestClient
import io.locally.engagesdk.notifications.NotificationRequest
import io.locally.engagesdk.notifications.NotificationResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/apps/push_subscribe.json")
    fun subscribe(@Body request: NotificationRequest): Observable<NotificationResponse>

    companion object {
        val instance: NotificationAPI by lazy {
            RestClient.instance.create(NotificationAPI::class.java)
        }
    }
}