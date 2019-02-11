package io.locally.engagesdk.network.services.geofences

import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.geofences.GeofenceRequest
import io.reactivex.Observable

class GeofenceServices {

    companion object {
        fun getGeofences(geofenceRequest: GeofenceRequest): Observable<GeofenceCampaign> {
            val impression =
                    ImpressionGeofence(bluetooth = true,
                            impressionType = geofenceRequest.type,
                            impressionProximity = geofenceRequest.proximity,
                            location = geofenceRequest.location)

            return GeofenceAPI.instance.getGeofences(impression)
        }
    }
}