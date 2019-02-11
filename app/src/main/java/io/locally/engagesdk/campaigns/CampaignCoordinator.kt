package io.locally.engagesdk.campaigns

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import io.locally.engagesdk.EngageSDK
import io.locally.engagesdk.beacons.BeaconsMonitor.BeaconListener
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity
import io.locally.engagesdk.geofences.GeofenceMonitor.LocationListener
import io.locally.engagesdk.geofences.GeofenceRequest
import io.locally.engagesdk.managers.AuthenticationManager
import io.locally.engagesdk.managers.LocationManager
import io.locally.engagesdk.managers.TokenManager
import io.locally.engagesdk.network.services.beacons.BeaconServices
import io.locally.engagesdk.network.services.geofences.GeofenceServices
import io.locally.engagesdk.widgets.WidgetsPresenter
import org.jetbrains.anko.doAsync

@SuppressLint("StaticFieldLeak")
object CampaignCoordinator : BeaconListener, LocationListener {
    private lateinit var context: Context
    private var displayed = mutableSetOf<String>()
    private lateinit var locationManager: LocationManager
    var campaignListener: EngageSDK.CampaignListener? = null

    fun init(context: Context) {
        CampaignCoordinator.context = context

        locationManager = LocationManager(context)
    }

    fun clear() = displayed.clear()

    private fun requestCampaigns(beacons: List<Beacon>) {
        locationManager.currentLocation { current ->
            current?.let {
                beacons.forEach { beacon -> requestCampaign(beacon, it) }
            } ?: return@currentLocation
        }
    }

    private fun requestCampaign(beacon: Beacon, location: Location) {
        doAsync {
            if(TokenManager.isTokenValid) {
                BeaconServices.getCampaign(bluetoothEnabled = true, location = location, beacon = beacon)
                        .subscribe({ campaign ->
                            campaign.data?.campaignContent?.let { content ->
                                val value = "${content.id}${beacon.proximity}"
                                displayed.any { it == value }.apply {
                                    if(this.not()) {
                                        displayed.add(value)
                                        campaignListener?.didCampaignArrived(content)
                                    }
                                }
                            }
                        }, { error ->
                            TokenManager.isTokenValid.let {
                                if(it) println("Error getting Beacon(${beacon.major}, ${beacon.minorDec}) Campaign: ${error.localizedMessage}")
                            }
                        })
            } else AuthenticationManager.refresh()
        }
    }

    private fun requestCampaign(currentLocation: android.location.Location) {
        val request = GeofenceRequest(type = ImpressionType.GEOFENCE, proximity = Proximity.ENTER, location = currentLocation)

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
                                                    displayed.add(campaign.id.toString())
                                                    campaignListener?.didCampaignArrived(campaign.campaignContent)
                                                } else println("${campaign.id} is already displayed")
                                            }
                                        }
                                    }
                                }
                            }
                        }, { error ->
                            TokenManager.isTokenValid.let {
                                if(it) println("Error getting Geofence(${currentLocation.latitude}, ${currentLocation.longitude}) Campaign: ${error.localizedMessage}")
                            }
                        })
            } else AuthenticationManager.refresh()
        }
    }

    override fun didBeaconUpdated(beacons: List<Beacon>) {
        requestCampaigns(beacons)
    }

    override fun didLocationUpdated(location: android.location.Location) {
        requestCampaign(location)
    }
}