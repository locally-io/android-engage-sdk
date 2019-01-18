package io.locally.engagesdk.network.services.campaign

import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.locally.engagesdk.datamodels.campaign.Campaign
import io.locally.engagesdk.network.RestClient
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CampaignAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/impressions/beacon.json")
    fun getCampaign(@Body impressionBeacon: ImpressionBeacon): Observable<Campaign>

    companion object {
        val instance: CampaignAPI by lazy {
            RestClient.instance.create(CampaignAPI::class.java)
        }
    }
}