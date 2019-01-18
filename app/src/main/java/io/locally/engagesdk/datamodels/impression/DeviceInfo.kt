package io.locally.engagesdk.datamodels.impression

import android.location.Location
import android.os.Build
import io.locally.engagesdk.common.Utils
import com.google.gson.annotations.SerializedName
import java.text.DateFormat

class DeviceInfo(location: Location) {

    //Location
    val altitude = location.altitude
    val latitude = location.latitude
    val longitude = location.longitude
    val heading = location.bearing
    val speed = location.speed
    var mock: Boolean = false
    @SerializedName("vertical_accuracy") var verticalAccuracy: Float = 0.0F
    @SerializedName("heading_accuracy") var headingAccuracy: Float = 0.0F
    @SerializedName("horizontal_accuracy") val horizontalAccuracy = location.accuracy

    //Connectivity
    @SerializedName("bluetooth_connected") val btConnected = Utils.isBtConnected
    @SerializedName("bluetooth_devices") val btDevices = Utils.bluetoothDevices
    @SerializedName("bluetooth_enabled") val bluetooth = Utils.bluetooth
    @SerializedName("bluetooth_name") val btName = Utils.bluetoothName
    @SerializedName("carrier_name") val carrierName = Utils.carrierName
    @SerializedName("connection_type") val connection = Utils.network
    @SerializedName("ipv4") val ipAddress4 = Utils.ipv4
    @SerializedName("ipv6") val ipAddress6 = Utils.ipv6
    @SerializedName("wifi_ssid") val ssid = Utils.ssid
    @SerializedName("wifi_bssid") val bSsid = Utils.bssid

    //Device
    @SerializedName("advertiser_id") var advId = Utils.deviceId
    @SerializedName("batt_level") var batteryLevel = Utils.battery
    @SerializedName("country") var country = Utils.country
    @SerializedName("device_model") var deviceModel = Build.MANUFACTURER + " : " + Build.MODEL
    @SerializedName("device_os") var deviceOs = android.os.Build.VERSION.CODENAME
    @SerializedName("device_version") var deviceVersion = android.os.Build.VERSION.SDK_INT
    @SerializedName("manufacturer") var manufacturer = Utils.manufacturer

    //Application
    @SerializedName("app_name") var appName = Utils.appName
    @SerializedName("background") var background = !Utils.isForeground(Utils.context)

    //Time
    @SerializedName("device_timestamp") var deviceTime = Utils.deviceTimeStamp
    @SerializedName("local_timestamp") var localTime = DateFormat.getDateTimeInstance().format(location.time)
    @SerializedName("utc_timestamp") var utcTime = Utils.formatedDate()

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verticalAccuracy = location.verticalAccuracyMeters
            headingAccuracy = location.bearingAccuracyDegrees
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) mock = location.isFromMockProvider
    }
}