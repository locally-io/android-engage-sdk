package io.locally.engagesdk.network

import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.network.support.BASE
import io.locally.engagesdk.network.support.RequestInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {

    companion object {
        val instance: Retrofit by lazy {
            val httpClient = OkHttpClient.Builder().addInterceptor(RequestInterceptor()).build()

             Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(Utils.gson))
                    .client(httpClient)
                    .baseUrl(BASE)
                    .build()
        }
    }
}