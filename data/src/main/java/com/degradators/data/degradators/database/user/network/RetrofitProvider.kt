package com.degradators.data.degradators.database.user.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    fun provideRetrofit(url: String, okHttpClient: OkHttpClient): Retrofit =
        provideRetrofitWithURL(url, okHttpClient)

    private fun provideRetrofitWithURL(apiUrl: String, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}