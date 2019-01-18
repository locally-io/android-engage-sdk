package io.locally.engagesdk

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.Campaign
import io.locally.engagesdk.datamodels.impression.*
import io.locally.engagesdk.network.services.campaign.CampaignAPI
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
    lateinit var authentication: BehaviorDelegate<CampaignAPI>
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
        authentication = mock.create(CampaignAPI::class.java)
    }

    @Test
    fun testImpressionSuccess() {
        authentication.returningResponse(Campaign(data = Campaign.Data(1,
                1, 1, 1, null, "test", 1)))
                .getCampaign(
                        ImpressionBeacon(
                                bluetooth = true,
                                impressionType = ImpressionType.BEACON,
                                impressionProximity = Proximity.TOUCH,
                                major = 2,
                                minorDec = 414,
                                location =
                                Location(longitude = -118.3165097809,
                                        latitude = 33.8806158172,
                                        altitude = 3.0,
                                        speed = -1.0,
                                        horizontal = 8.0,
                                        vertical = 3.0)))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data != null }
    }

    @Test
    fun testImpressionFail() {
        authentication.returningResponse(Campaign(data = null))
                .getCampaign(
                        ImpressionBeacon(
                                bluetooth = true,
                                impressionType = ImpressionType.BEACON,
                                impressionProximity = Proximity.TOUCH,
                                major = 2,
                                minorDec = 414,
                                location =
                                Location(longitude = -118.3165097809,
                                        latitude = 33.8806158172,
                                        altitude = 3.0,
                                        speed = -1.0,
                                        horizontal = 8.0,
                                        vertical = 3.0)))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data == null }
    }
}