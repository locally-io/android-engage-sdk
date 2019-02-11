package io.locally.engagesdk

import android.location.Location
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.BeaconCampaign
import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity
import io.locally.engagesdk.network.services.beacons.BeaconAPI
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
class BeaconImpressionTest {
    lateinit var authentication: BehaviorDelegate<BeaconAPI>
    val behavior = NetworkBehavior.create()
    lateinit var retrofit: Retrofit
    lateinit var mock: MockRetrofit

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        Utils.init(context)

        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE)
                .build()

        mock = MockRetrofit.Builder(retrofit).networkBehavior(behavior).build()
        authentication = mock.create(BeaconAPI::class.java)
    }

    @Test
    fun testImpressionSuccess() {
        authentication.returningResponse(BeaconCampaign(data = BeaconCampaign.Data()))
                .getCampaign(
                        ImpressionBeacon(
                                bluetooth = true,
                                impressionType = ImpressionType.BEACON,
                                impressionProximity = Proximity.TOUCH,
                                major = 2,
                                minorDec = 414,
                                location =
                                Location("GPS")))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data != null }
    }

    @Test
    fun testImpressionFail() {
        authentication.returningResponse(BeaconCampaign(data = null))
                .getCampaign(
                        ImpressionBeacon(
                                bluetooth = true,
                                impressionType = ImpressionType.BEACON,
                                impressionProximity = Proximity.TOUCH,
                                major = 2,
                                minorDec = 414,
                                location =
                                Location("GPS")))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data == null }
    }
}