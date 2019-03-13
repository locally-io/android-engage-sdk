package io.locally.engagesdk.campaigns

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.locally.engagesdk.EngageSDK
import io.locally.engagesdk.EventHandler
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity
import io.locally.engagesdk.geofences.GeofenceRequest
import io.locally.engagesdk.managers.AuthenticationManager
import io.locally.engagesdk.managers.LocationManager
import io.locally.engagesdk.managers.TokenManager
import io.locally.engagesdk.network.services.beacons.BeaconServices
import io.locally.engagesdk.network.services.geofences.GeofenceServices
import org.jetbrains.anko.doAsync

@SuppressLint("StaticFieldLeak")
object CampaignCoordinator {
    private lateinit var context: Context
    private var displayed = mutableSetOf<String>()
    private lateinit var locationManager: LocationManager
    var campaignListener: EngageSDK.CampaignListener? = null

    fun init(context: Context) {
        CampaignCoordinator.context = context

        locationManager = LocationManager(context)
    }

    fun clear() = displayed.clear()

    fun requestBeaconCampaigns(beacons: List<Beacon>) {
        locationManager.currentLocation { current ->
            current?.let {
                beacons.forEach { beacon -> requestCampaign(beacon, it) }
            } ?: return@currentLocation
        }
    }

    private fun requestCampaign(beacon: Beacon, location: Location) {
        EventHandler.listener?.impressionUpdate("Impression Beacon(${beacon.major}, ${beacon.minorDec}) at ${beacon.proximity}", Utils.logTime())
        doAsync {
            if(TokenManager.isTokenValid) {
                BeaconServices.getCampaign(bluetoothEnabled = true, location = location, beacon = beacon)
                        .subscribe({ campaign ->
                            campaign.data?.campaignContent?.let { content ->
                                val value = "${content.id}${beacon.proximity}"
                                displayed.any { it == value }.apply {
                                    if(this.not()) {
                                        content.campaign = campaign.data.id.toString()
                                        content.impression = campaign.data.impressionId.toString()

                                        EventHandler.listener?.beaconCampaignUpdate(content, Utils.logTime())
                                        displayed.add(value)
                                        campaignListener?.didCampaignArrived(content)
                                    }
                                }
                            }
                        }, { error ->
                            TokenManager.isTokenValid.let {
                                if(it) Log.e(javaClass.name, "Error getting Beacon(${beacon.major}, ${beacon.minorDec}) Campaign: ${error.localizedMessage}")
                                EventHandler.listener?.error("Error getting  Beacon(${beacon.major}, ${beacon.minorDec}) Campaign: ${error.localizedMessage}", Utils.logTime())

                            }
                        })
            } else AuthenticationManager.refresh()
        }
    }

    fun requestGeofenceCampaign(proximity: Proximity) {
        locationManager.currentLocation { current ->
            current?.let { location ->
            val request = GeofenceRequest(type = ImpressionType.GEOFENCE, proximity = proximity, location = location)
                doAsync {
                    if(TokenManager.isTokenValid) {
                        GeofenceServices.getGeofences(request)
                                .subscribe({ response ->
                                    response.data?.campaigns?.let { campaigns ->
                                        campaigns.isNotEmpty().apply {
                                            if(this) {
                                                campaigns.forEach { campaign ->
                                                    displayed.any { campaign.id.toString() == it }.apply {
                                                        if(this.not()) {
                                                            campaign.campaignContent?.campaign = campaign.id.toString()
                                                            campaign.campaignContent?.impression = campaign.impressionId.toString()

                                                            EventHandler.listener?.geofenceCampaignUpdate(campaign, Utils.logTime())
                                                            displayed.add(campaign.id.toString())
                                                            campaignListener?.didCampaignArrived(campaign.campaignContent)
                                                        } else Log.i(javaClass.name, "${campaign.id} is already displayed")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }, { error ->
                                    TokenManager.isTokenValid.let {
                                        EventHandler.listener?.error("Error getting Geofence(${location.latitude}, ${location.longitude}) Campaign: ${error.localizedMessage}", Utils.logTime())
                                        if(it) Log.e(javaClass.name, "Error getting Geofence(${location.latitude}, ${location.longitude}) Campaign: ${error.localizedMessage}")
                                    }
                                })
                    } else AuthenticationManager.refresh()
                }
            } ?: return@currentLocation
        }
    }
}