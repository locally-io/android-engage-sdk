package io.locally.engagesdk

import android.annotation.SuppressLint
import android.app.Activity
import io.locally.engagesdk.beacons.BeaconsMonitor
import io.locally.engagesdk.campaigns.CampaignCoordinator
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.impression.Demographics
import io.locally.engagesdk.geofences.GeofenceMonitor
import io.locally.engagesdk.managers.AuthStatus
import io.locally.engagesdk.managers.AuthenticationManager
import io.locally.engagesdk.notifications.NotificationManager

@SuppressLint("StaticFieldLeak")
object EngageSDK {

    private lateinit var beaconsMonitor: BeaconsMonitor
    private lateinit var geofenceMonitor: GeofenceMonitor
    private lateinit var activity: Activity

    fun init(activity: Activity){
        this.activity = activity
        Utils.init(activity)
        Utils.demographics = Demographics(27, Demographics.Gender.MALE)
        CampaignCoordinator.init(activity)
        beaconsMonitor = BeaconsMonitor(activity)
        geofenceMonitor = GeofenceMonitor(activity)
    }

    fun login(username: String, password: String, callback: ((AuthStatus, String?) -> Unit)? = null){
        AuthenticationManager.login(username, password, callback)
    }

    fun logout(callback: ((Boolean) -> Unit)? = null) {
        AuthenticationManager.logout(callback).let {
            stopMonitoringBeacons()
            stopMonitoringGeofences()
        }
    }

    fun enablePushNotifications(token: String, callback: ((Boolean) -> Unit)? = null){
        Utils.deviceToken = token

        NotificationManager.subscribe(activity, callback)
    }

    fun setListener(campaignListener: CampaignListener){
      CampaignCoordinator.campaignListener = campaignListener
    }

    fun clearContent() = CampaignCoordinator.clear()

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

    interface CampaignListener {
        fun didCampaignArrived(campaign: CampaignContent?) {}
    }
}