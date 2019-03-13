package io.locally.engagesdk.network.services.geofences

import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.geofences.Geofence
import io.locally.engagesdk.datamodels.geofences.GeofenceInRange
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.network.RestClient
import io.reactivex.Observable
import retrofit2.http.*

interface GeofenceAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/impressions/multi-geofence.json")
    fun getGeofences(@Body impression: ImpressionGeofence): Observable<GeofenceCampaign>

    @GET("/geofences/inRange.json")
    fun inRangeGeofences(@Query("lat") lat: Double, @Query("lng") lng: Double, @Query("radius") radius: Int): Observable<GeofenceInRange>

    companion object {
        val instance: GeofenceAPI by lazy {
            RestClient.instance.create(GeofenceAPI::class.java)
        }
    }
}