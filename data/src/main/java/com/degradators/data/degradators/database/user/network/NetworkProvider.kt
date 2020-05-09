package com.degradators.data.degradators.database.user.network

import com.degradators.data.degradators.database.user.network.RetrofitProvider.provideRetrofit
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class NetworkProvider {

    fun provideCommonApiRetrofit(url: String): Retrofit = provideRetrofit(
        url,
        provideOkHttpClient()
    )

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()
}