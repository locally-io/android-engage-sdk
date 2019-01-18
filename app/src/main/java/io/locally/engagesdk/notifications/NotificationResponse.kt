package io.locally.engagesdk.notifications

import com.google.gson.annotations.SerializedName

class NotificationResponse(val data: Data) {
    class Data(@SerializedName("EndpointArn") val endpoint: String = "")
}