package io.locally.engagesdk

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.geofences.Geofence
import io.locally.engagesdk.datamodels.geofences.Geofence.CoverageType.*
import io.locally.engagesdk.datamodels.geofences.GeofenceInRange
import io.locally.engagesdk.network.services.geofences.GeofenceAPI
import io.locally.engagesdk.network.support.BASE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

@RunWith(AndroidJUnit4::class)
class GeofenceInRange {

    lateinit var geofences: BehaviorDelegate<GeofenceAPI>
    val behavior = NetworkBehavior.create()
    lateinit var retrofit: Retrofit
    lateinit var mock: MockRetrofit

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
    }

    @Test
    fun testGeofencesInRange() {
        val response = arrayListOf(Geofence(id = 1, type = RADIUS, center = Geofence.Center(27.498842, -109.938109, 0.00621371)))
        geofences.returningResponse(GeofenceInRange(data = response))
                .inRangeGeofences(lat = 27.498842, lng = -109.938109, radius = 500)
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { geofences ->  geofences.data.isNotEmpty()}
    }
}