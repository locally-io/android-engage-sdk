package io.locally.engagesdk.managers

import android.os.Looper
import com.google.gson.Gson
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.managers.AuthStatus.*
import io.locally.engagesdk.network.services.authentication.AuthenticationServices
import org.jetbrains.anko.doAsync
import retrofit2.HttpException
import java.net.UnknownHostException

class AuthenticationManager {

    companion object {
        fun login(username: String, password: String, callback: ((AuthStatus, String?) -> Unit)? = null) {
            if(TokenManager.isTokenValid) {
                callback?.invoke(AuthStatus.SUCCESS, "Previous Session")
                return
            }

            val id = Utils.deviceId ?: run {
                callback?.invoke(MISSING_DEVICE_ID, "Missing device id")
                return
            }

            doAsync {
                Looper.prepare()
                AuthenticationServices.login(username, password, id.replace("-", ""))
                        .subscribe({ response ->
                            response.data?.let {
                                TokenManager.accessToken(it)
                                callback?.invoke(SUCCESS, "Success login")
                            }
                        }, {
                            if(it is HttpException){
                                when(it.code()){
                                    401 -> callback?.invoke(UNAUTHORIZED, it.message())
                                }
                            } else {
                                if(it is UnknownHostException) callback?.invoke(CONNECTION_ERROR, it.message)
                                    else callback?.invoke(UNKNOWN_ERROR, it.localizedMessage)
                            }
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
                Looper.prepare()
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

        fun logout(callback: ((Boolean) -> Unit)? = null){
            TokenManager.invalidateToken().let {
                callback?.invoke(true)
            }
        }
    }
}