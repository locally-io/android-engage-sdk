package io.locally.engagesdk.network.services.beacons

import io.locally.engagesdk.datamodels.campaign.BeaconCampaign
import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.locally.engagesdk.network.RestClient
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BeaconAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/impressions/beacon.json")
    fun getCampaign(@Body impressionBeacon: ImpressionBeacon): Observable<BeaconCampaign>

    companion object {
        val instance: BeaconAPI by lazy {
            RestClient.instance.create(BeaconAPI::class.java)
        }
    }
}