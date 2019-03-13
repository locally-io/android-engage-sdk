package io.locally.engagesdk.common

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.locally.engagesdk.datamodels.impression.Demographics
import org.jetbrains.anko.doAsync
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object Utils {

    private lateinit var preferences: SharedPreferences
    lateinit var context: Context

    val gson: Gson by lazy {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }

    fun init(context: Context) {
        Utils.context = context
        preferences = context.getSharedPreferences(FILENAME, MODE_PRIVATE)
        doAsync {
            try {
                deviceId = AdvertisingIdClient.getAdvertisingIdInfo(context).id
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @TargetApi(LOLLIPOP)
    private fun batteryLevel(context: Context): Int {
        return if(SDK_INT >= LOLLIPOP) {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else 0
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var demographics: Demographics?
        get() {
            val demographicsJSON = preferences.getString(DEMOGRAPHICS, null)
            return if(demographicsJSON != null) gson.fromJson(demographicsJSON, Demographics::class.java)
            else null
        }
        set(value) = preferences.edit { it.putString(DEMOGRAPHICS, gson.toJson(value)) }

    var token: String?
        get() = preferences.getString(TOKEN, "")
        set(value) = preferences.edit { it.putString(TOKEN, value) }

    var refresh: String?
        get() = preferences.getString(REFRESH, "")
        set(value) = preferences.edit { it.putString(REFRESH, value) }

    var refreshDate: Date?
        get() {
            val time = preferences.getLong(REFRESH_DATE, 0)
            return if(time > 0) Date(time)
            else null
        }
        set(value) {
            value?.let { date ->
                preferences.edit { it.putLong(REFRESH_DATE, date.time) }
            } ?: preferences.edit { it.putLong(REFRESH_DATE, 0) }
        }

    var sns: String?
        get() = preferences.getString(SNS, null)
        set(value) = preferences.edit { it.putString(SNS, value) }

    var arn: String?
        get() = preferences.getString(ARN, null)
        set(value) = preferences.edit { it.putString(ARN, value) }

    var deviceToken: String?
        get() = preferences.getString(DEVICE_TOKEN, null)
        set(value) = preferences.edit { it.putString(DEVICE_TOKEN, value) }

    var guid: String?
        get() = preferences.getString(GUID, null)
        set(value) = preferences.edit { it.putString(GUID, value) }

    var kontaktKey: String?
        get() = preferences.getString(KONTAKT_KEY, null)
        set(value) = preferences.edit { it.putString(KONTAKT_KEY, value) }

    var poolId: String?
        get() = preferences.getString(POOL_ID, null)
        set(value) = preferences.edit { it.putString(POOL_ID, value) }

    var deviceId: String? = ""

    var appName: String = ""
        get() = context.packageName

    var ipv6: String? = ""
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()

                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                            return inetAddress.getHostAddress().split("%".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

    var ipv4: String? = ""
        get() {
            try {
                val en = NetworkInterface
                        .getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

    var battery: Int = 0
        get() = batteryLevel(context)

    @SuppressLint("SimpleDateFormat")
    fun formatedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(Date())
    }

    var isBtConnected = false
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
            return mBluetoothAdapter.bondedDevices.size > 0
        }

    var bluetoothDevices: List<String> = arrayListOf()
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return if (bluetoothAdapter.isEnabled){
                val pairedDevices = bluetoothAdapter.bondedDevices.map { it -> it.name }
                pairedDevices
            } else arrayListOf()
        }

    var bluetooth = false
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
            return mBluetoothAdapter.isEnabled
        }

    var bluetoothName: String? = ""
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return ""
            return mBluetoothAdapter.name
        }

    var carrierName: String? = ""
        get() {
            val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return manager.networkOperatorName
        }

    var network: String? = ""
        get() {
            val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            var network = ""

            if (info != null && info.isConnected) {
                when (info.type) {
                    ConnectivityManager.TYPE_WIFI -> network = "Wifi"
                    ConnectivityManager.TYPE_MOBILE -> network = "Mobile"
                }
            }

            return network
        }

    var ssid: String? = ""
        get() {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            return info.ssid
        }

    var bssid: String? = ""
        get() {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            return info.bssid
        }

    var country: String? = ""
        get() = context.resources.configuration.locale.country

    var manufacturer: String? = Build.MANUFACTURER

    val deviceTimeStamp: Long = Date().time

    private enum class TYPE {
        IPV4, IPV6
    }

    fun logTime(): String {
        val current = Calendar.getInstance().apply { time = Date() }

        return "${current.get(Calendar.HOUR_OF_DAY)}:" +
                "${current.get(Calendar.MINUTE)}:" +
                "${current.get(Calendar.SECOND)} >> "
    }

    fun isForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any {
            it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
}