package io.locally.engagesdk.datamodels.impression

import com.google.gson.annotations.SerializedName

open class Impression{
    @SerializedName("bluetooth_enabled") var bluetoothEnabled: Boolean = false
    lateinit var type: ImpressionType
    lateinit var proximity: Proximity
    @SerializedName("device_id") var deviceId: String? = null
    var os: String? = null
    @SerializedName("app_name") lateinit var appName: String
    var lat: Double = 0.0
    var lng: Double = 0.0
    @SerializedName("timestamp") lateinit var timeStamp: String
    @SerializedName("deviceinfo") var deviceInfo: DeviceInfo? = null
    var demographics: Demographics? = null
}