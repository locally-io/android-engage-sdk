package io.locally.engagesdk.datamodels.impression

import android.location.Location
import io.locally.engagesdk.common.OS
import io.locally.engagesdk.common.UUID
import io.locally.engagesdk.common.Utils
import com.google.gson.annotations.SerializedName

class ImpressionBeacon(bluetooth: Boolean,
                       impressionType: ImpressionType,
                       impressionProximity: Proximity,
                       val major: Int,
                       @SerializedName("minor_dec") val minorDec: Int,
                       location: Location): Impression() {

    val uuid = UUID

    init {
        os = OS
        deviceId = Utils.deviceId?.replace("-", "")
        appName = Utils.appName
        bluetoothEnabled = bluetooth
        type = impressionType
        proximity = impressionProximity
        lat = location.latitude
        lng = location.longitude
        demographics = Utils.demographics
        timeStamp = Utils.formatedDate()
        deviceInfo = DeviceInfo(location)
    }
}