package io.locally.engagesdk.network.services.authentication

import io.locally.engagesdk.datamodels.authentication.LoginRequest
import io.locally.engagesdk.datamodels.authentication.LoginResponse
import io.locally.engagesdk.datamodels.authentication.RefreshTokenRequest
import io.locally.engagesdk.network.RestClient
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthenticationAPI {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/apps/login.json")
    fun login(@Body loginRequest: LoginRequest): Observable<LoginResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/apps/login.json")
    fun refresh(@Body refreshTokenRequest: RefreshTokenRequest): Observable<LoginResponse>

    companion object {
        val instance: AuthenticationAPI by lazy {
            RestClient.instance.create(AuthenticationAPI::class.java)
        }
    }
}