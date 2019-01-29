package io.locally.engagesdk

import android.support.test.runner.AndroidJUnit4
import io.locally.engagesdk.datamodels.authentication.LoginRequest
import io.locally.engagesdk.datamodels.authentication.LoginResponse
import io.locally.engagesdk.datamodels.authentication.LoginResponse.*
import io.locally.engagesdk.datamodels.authentication.RefreshTokenRequest
import io.locally.engagesdk.network.services.authentication.AuthenticationAPI
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
class LoginTest {
    lateinit var authentication: BehaviorDelegate<AuthenticationAPI>
    val behavior = NetworkBehavior.create()
    lateinit var retrofit: Retrofit
    lateinit var mock: MockRetrofit

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE)
                .build()

        mock = MockRetrofit.Builder(retrofit).networkBehavior(behavior).build()
        authentication = mock.create(AuthenticationAPI::class.java)
    }

    @Test
    fun testLoginSucceed() {
        authentication.returningResponse(LoginResponse())
                .login(LoginRequest(username = "", password = "", deviceId = ""))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data != null }
    }

    @Test
    fun testLoginFailure() {
        authentication.returningResponse(LoginResponse())
                .login(LoginRequest(username = "", password = "", deviceId = ""))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data == null }
    }

    @Test
    fun refreshSucceed(){
        authentication.returningResponse(LoginResponse(data = Data("token", "refresh", "guid-number", "kontaktKey", AWS(pool = "poolId"))))
                .refresh(RefreshTokenRequest(refresh = "", deviceId = ""))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data != null }
    }

    @Test
    fun testRefreshFailure() {
        authentication.returningResponse(LoginResponse())
                .refresh(RefreshTokenRequest(refresh = "", deviceId = ""))
                .test()
                .assertTerminated()
                .assertComplete()
                .assertNoErrors()
                .assertValue { response -> response.data == null }
    }
}