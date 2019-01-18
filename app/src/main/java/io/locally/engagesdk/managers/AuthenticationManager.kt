package io.locally.engagesdk.managers

import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.network.services.authentication.AuthenticationServices
import org.jetbrains.anko.doAsync

class AuthenticationManager {

    companion object {
        fun login(username: String, password: String, callback: ((Boolean) -> Unit)? = null) {
            if(TokenManager.isTokenValid) {
                callback?.invoke(true)
                return
            }

            val id = Utils.deviceId ?: run {
                callback?.invoke(false)
                return
            }

            doAsync {
                AuthenticationServices.login(username, password, id.replace("-", ""))
                        .subscribe({ response ->
                            response.data?.let {
                                TokenManager.accessToken(it)
                                callback?.invoke(true)
                            }
                        }, {
                            callback?.invoke(false)
                            println("Error trying to login: ${it.localizedMessage}")
                        })
            }
        }

        fun refresh(callback: ((Boolean) -> Unit)? = null) {
            if(TokenManager.isTokenValid) {
                callback?.invoke(true)
                return
            }

            val id = Utils.deviceId ?: run {
                callback?.invoke(false)
                return
            }
            val refresh = Utils.refresh ?: run {
                callback?.invoke(false)
                return
            }

            doAsync {
                AuthenticationServices.refresh(refresh, id.replace("-", ""))
                        .subscribe({ response ->
                            response.data?.let {
                                TokenManager.accessToken(it)
                                callback?.invoke(true)
                            }
                        }, {
                            callback?.invoke(false)
                            println("Error trying to refresh token: ${it.localizedMessage}")
                        })
            }
        }
    }
}