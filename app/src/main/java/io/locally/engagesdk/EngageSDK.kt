package io.locally.engagesdk

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import io.locally.engagesdk.beacons.BeaconsMonitor
import io.locally.engagesdk.campaigns.CampaignCoordinator
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.impression.Beacon
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

    fun login(username: String, password: String, callback: ((AuthStatus, String?) -> Unit)? = null) =
        AuthenticationManager.login(username, password, callback)

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

    fun setListener(campaignListener: CampaignListener) { CampaignCoordinator.campaignListener = campaignListener }

    fun clearContent() = CampaignCoordinator.clear()

    fun startMonitoringBeacons() = beaconsMonitor.startMonitoring()

    fun stopMonitoringBeacons() = beaconsMonitor.stopMonitoring()

    fun startMonitoringGeofences(radius: Int = 500, refresh: Long = 600000) = geofenceMonitor.startMonitoring(radius, refresh)

    fun stopMonitoringGeofences() = geofenceMonitor.stopMonitoring()

    fun setEventListener(listener: EventListener?) { EventHandler.listener = listener}

    interface CampaignListener {
        fun didCampaignArrived(campaign: CampaignContent?) {}
    }

    interface EventListener {
        fun locationUpdate(location: Location, time: String) {}
        fun beaconUpdate(beacon: Beacon, time: String) {}
        fun impressionUpdate(message: String, time: String) {}
        fun beaconCampaignUpdate(campaignContent: CampaignContent, time: String) {}
        fun geofenceCampaignUpdate(campaignContent: GeofenceCampaign.Campaign, time: String) {}
        fun error(message: String, time: String) {}
    }
}