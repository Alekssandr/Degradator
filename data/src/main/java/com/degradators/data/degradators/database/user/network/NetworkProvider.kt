package com.degradators.data.degradators.database.user.network

import com.degradators.data.degradators.database.user.network.RetrofitProvider.provideRetrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class NetworkProvider {

    fun provideCommonApiRetrofit(url: String): Retrofit = provideRetrofit(
        url,
        provideOkHttpClient()
    )

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .build()

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = loggingLevel
        return loggingInterceptor
    }

    private val loggingLevel = getLoggingLevel(false)

    private fun getLoggingLevel(isRelease: Boolean): HttpLoggingInterceptor.Level {
        return if (isRelease) {
            HttpLoggingInterceptor.Level.NONE
        } else {
            HttpLoggingInterceptor.Level.HEADERS
        }
    }
}