package io.locally.engagesdk.campaigns

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import io.locally.engagesdk.beacons.BeaconsMonitor.BeaconListener
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity
import io.locally.engagesdk.geofences.GeofenceMonitor.LocationListener
import io.locally.engagesdk.geofences.GeofenceRequest
import io.locally.engagesdk.managers.LocationManager
import io.locally.engagesdk.network.services.campaign.CampaignServices
import io.locally.engagesdk.network.services.geofences.GeofenceServices
import io.locally.engagesdk.widgets.WidgetsPresenter
import org.jetbrains.anko.doAsync

@SuppressLint("StaticFieldLeak")
object CampaignCoordinator: BeaconListener, LocationListener {

    private lateinit var context: Context
    private var campaigns = mutableSetOf<String>()
    private lateinit var locationManager: LocationManager

    fun init(context: Context) {
        CampaignCoordinator.context = context

        locationManager = LocationManager(context)
    }

    fun clear() = campaigns.clear()

    private fun requestCampaigns(beacons: List<Beacon>){
        locationManager.currentLocation { current ->
            current?.let {
                beacons.forEach { beacon -> requestCampaign(beacon, it) }
            } ?: return@currentLocation
        }
    }

    private fun requestCampaign(beacon: Beacon, location: Location){
        doAsync {
            CampaignServices.getCampaign(bluetoothEnabled = true, location = location, beacon = beacon)
                    .subscribe({ campaign ->
                        campaign.data?.campaignContent?.let { content ->
                            val value = "${content.id}${beacon.proximity}"
                            campaigns.any { it == value }.apply {
                                if(!this) {
                                    campaigns.add(value)
                                    WidgetsPresenter.presentWidget(context, content)
                                } else println("${content.id} is already displayed")
                            }
                        }
                    }, { error ->
                        println("Error getting Beacon(${beacon.major}, ${beacon.minorDec}) Campaign: ${error.localizedMessage}")
                    })
        }
    }

    private fun requestCampaign(currentLocation: android.location.Location){
        val request = GeofenceRequest(type = ImpressionType.GEOFENCE, proximity = Proximity.ENTER, location = currentLocation)

        doAsync {
            GeofenceServices.getGeofences(request)
                    .subscribe({ campaign ->
                        campaign.data?.campaignContent?.let { content ->
                            campaigns.any { content.id.toString() == it }.apply {
                                if(!this) {
                                    campaigns.add(content.id.toString())
                                    WidgetsPresenter.presentWidget(context, content)
                                } else println("${content.id} is already displayed")
                            }
                        }
                    }, { error ->
                        println("Error getting Geofence(${currentLocation.latitude}, ${currentLocation.longitude}) Campaign: ${error.localizedMessage}")
                    })
        }
    }

    override fun didBeaconUpdated(beacons: List<Beacon>) {
        requestCampaigns(beacons)
    }

    override fun didLocationUpdated(location: android.location.Location) {
        requestCampaign(location)
    }
}