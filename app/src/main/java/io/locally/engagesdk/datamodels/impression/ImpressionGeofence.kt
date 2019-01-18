package io.locally.engagesdk.datamodels.impression

import android.location.Location
import io.locally.engagesdk.common.OS
import io.locally.engagesdk.common.Utils

class ImpressionGeofence(bluetooth: Boolean,
                         impressionType: ImpressionType,
                         impressionProximity: Proximity,
                         location: Location): Impression() {
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