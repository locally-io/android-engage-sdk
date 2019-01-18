package io.locally.engagesdk.network.support

import io.locally.engagesdk.common.Utils
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${Utils.token}")
                .build()

        return chain.proceed(request)
    }
}