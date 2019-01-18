package io.locally.engagesdk.network.services.authentication

import io.locally.engagesdk.datamodels.authentication.LoginRequest
import io.locally.engagesdk.datamodels.authentication.LoginResponse
import io.locally.engagesdk.datamodels.authentication.RefreshTokenRequest
import io.reactivex.Observable

class AuthenticationServices {

    companion object {

        fun login(username: String, password: String, deviceId: String): Observable<LoginResponse> {
            val loginRequest = LoginRequest(username, password, deviceId)

            return AuthenticationAPI.instance.login(loginRequest)
        }

        fun refresh(refreshToken: String, deviceId: String): Observable<LoginResponse>{
            val refreshTokenRequest = RefreshTokenRequest(refresh = refreshToken, deviceId = deviceId)

            return AuthenticationAPI.instance.refresh(refreshTokenRequest)
        }
    }
}