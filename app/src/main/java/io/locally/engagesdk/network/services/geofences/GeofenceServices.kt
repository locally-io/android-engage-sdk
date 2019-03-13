package io.locally.engagesdk.network.services.geofences

import com.google.gson.GsonBuilder
import io.locally.engagesdk.EventHandler
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.geofences.GeofenceInRange
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.geofences.GeofenceRequest
import io.reactivex.Observable

class GeofenceServices {

    companion object {
        private val imp = GsonBuilder().setPrettyPrinting().create()

        fun getGeofences(geofenceRequest: GeofenceRequest): Observable<GeofenceCampaign> {
            val impression =
                    ImpressionGeofence(bluetooth = true,
                            impressionType = geofenceRequest.type,
                            impressionProximity = geofenceRequest.proximity,
                            location = geofenceRequest.location)

            EventHandler.listener?.impressionUpdate("Impression Geofence - ${imp.toJson(impression)}", Utils.logTime())
            return GeofenceAPI.instance.getGeofences(impression)
        }

        fun inRange(latitude: Double, longitude: Double, radius: Int): Observable<GeofenceInRange> {
            return GeofenceAPI.instance.inRangeGeofences(latitude, longitude, radius)
        }
    }
}