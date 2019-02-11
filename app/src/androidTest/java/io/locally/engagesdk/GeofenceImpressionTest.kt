package io.locally.engagesdk

import android.location.Location
import android.support.test.InstrumentationRegistry
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.GeofenceCampaign
import io.locally.engagesdk.datamodels.impression.ImpressionGeofence
import io.locally.engagesdk.datamodels.impression.ImpressionType.GEOFENCE
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

    lateinit var impression: ImpressionGeofence

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

        impression = ImpressionGeofence(true, GEOFENCE, FAR, Location("GPS"))
    }

    @Test
    fun testImpressionSuccess(){
        geofences.returningResponse(GeofenceCampaign(data = GeofenceCampaign.Data()))
                .getGeofences(impression)
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { campaign -> campaign.data != null }
    }

    @Test
    fun testImpressionFailure(){
        geofences.returningResponse(GeofenceCampaign(data = null))
                .getGeofences(impression)
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { campaign -> campaign.data == null }
    }
}