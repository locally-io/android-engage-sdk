package io.locally.engagesdk.network.services.geofences

import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.network.RestClient
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeofenceAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/impressions/multi-geofence.json")
    fun getGeofences(@Body impression: ImpressionGeofence): Observable<GeofenceCampaign>

    companion object {
        val instance: GeofenceAPI by lazy {
            RestClient.instance.create(GeofenceAPI::class.java)
        }
    }
}