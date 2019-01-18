package io.locally.engagesdk

import android.support.test.InstrumentationRegistry
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.Campaign
import io.locally.engagesdk.datamodels.campaign.Campaign.Data
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.datamodels.impression.ImpressionType.GEOFENCE
import io.locally.engagesdk.datamodels.impression.Location
import io.locally.engagesdk.datamodels.impression.Proximity.FAR
import io.locally.engagesdk.network.services.geofences.GeofenceAPI
import io.locally.engagesdk.network.support.BASE
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

class GeofenceImpressionTest {

    lateinit var geofences: BehaviorDelegate<GeofenceAPI>
    val behavior = NetworkBehavior.create()
    lateinit var retrofit: Retrofit
    lateinit var mock: MockRetrofit

    lateinit var location: Location
    lateinit var impression: ImpressionGeofence
    lateinit var campaign: Campaign

    @Before
    fun setUp(){
        val context = InstrumentationRegistry.getTargetContext()
        Utils.init(context)

        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE)
                .build()

        mock = MockRetrofit.Builder(retrofit).networkBehavior(behavior).build()
        geofences = mock.create(GeofenceAPI::class.java)

        location = Location(27.4988, -109.938, 0.0, 0.0, 0.0, 0.0)
        impression = ImpressionGeofence(true, GEOFENCE, FAR, location)
        campaign = Campaign(Data(1, 1, 1, 1, null, "", 1))
    }

    @Test
    fun testImpressionSuccess(){
        geofences.returningResponse(campaign)
                .getGeofences(impression)
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { campaign -> campaign.data != null }
    }

    @Test
    fun testImpressionFailure(){
        geofences.returningResponse(Campaign(data = null))
                .getGeofences(impression)
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { campaign -> campaign.data == null }
    }
}