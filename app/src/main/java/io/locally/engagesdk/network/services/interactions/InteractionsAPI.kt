package io.locally.engagesdk.network.services.interactions

import io.locally.engagesdk.datamodels.campaign.Interaction
import io.locally.engagesdk.network.RestClient
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InteractionsAPI {

    @POST("/interactions/register.json")
    fun registerInteraction(@Body interaction: Interaction): Call<Void>

    companion object {
        val instance: InteractionsAPI by lazy {
            RestClient.instance.create(InteractionsAPI::class.java)
        }
    }
}