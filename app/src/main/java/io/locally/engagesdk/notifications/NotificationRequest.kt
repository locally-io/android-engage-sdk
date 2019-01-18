package io.locally.engagesdk.notifications

import io.locally.engagesdk.common.OS
import io.locally.engagesdk.common.Utils
import com.google.gson.annotations.SerializedName

class NotificationRequest {
    @SerializedName("device_id") val deviceId = Utils.deviceId?.replace("-", "")
    val os = OS
    @SerializedName("device_token") val deviceToken = Utils.deviceToken
}