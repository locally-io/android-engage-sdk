package io.locally.engagesdk

import android.annotation.SuppressLint
import android.app.Activity
import io.locally.engagesdk.beacons.BeaconsMonitor
import io.locally.engagesdk.campaigns.CampaignCoordinator
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.impression.Demographics
import io.locally.engagesdk.geofences.GeofenceMonitor
import io.locally.engagesdk.managers.AuthenticationManager
import io.locally.engagesdk.notifications.NotificationManager

@SuppressLint("StaticFieldLeak")
object EngageSDK {

    private lateinit var beaconsMonitor: BeaconsMonitor
    private lateinit var geofenceMonitor: GeofenceMonitor

    fun init(activity: Activity){
        Utils.init(activity)
        Utils.demographics = Demographics(27, Demographics.Gender.MALE)
        NotificationManager.init(activity)
        CampaignCoordinator.init(activity)
        beaconsMonitor = BeaconsMonitor(activity)
        geofenceMonitor = GeofenceMonitor(activity)
    }

    fun login(username: String, password: String, callback: ((Boolean) -> Unit)? = null){
        AuthenticationManager.login(username, password, callback)
    }

    fun enablePushNotifications(token: String, callback: ((Boolean) -> Unit)? = null){
        Utils.deviceToken = token

        NotificationManager.subscribe(callback)
    }

    fun startMonitoringBeacons(){
        beaconsMonitor.subscribe(CampaignCoordinator)
        beaconsMonitor.startMonitoring()
    }

    fun stopMonitoringBeacons() = beaconsMonitor.stopMonitoring()

    fun startMonitoringGeofences(){
        geofenceMonitor.subscribe(CampaignCoordinator)
        geofenceMonitor.startMonitoring()
    }

    fun stopMonitoringGeofences() = geofenceMonitor.stopMonitoring()
}