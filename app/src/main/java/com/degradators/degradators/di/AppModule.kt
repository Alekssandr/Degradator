package com.degradators.degradators.di

import android.app.Application
import android.content.Context

import com.degradators.data.degradators.database.user.api.UserAuthAPI
import com.degradators.data.degradators.database.user.network.NetworkProvider
import com.degradators.data.degradators.database.user.repo.UserAuthDataRepository
import com.degradators.degradators.BuildConfig
import com.degradators.degradators.repo.UserAuthRepository

import com.degradators.degradators.di.common.rx.RxSchedulers
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideCommonApiRetrofit(
        networkProvider: NetworkProvider
    ): Retrofit = networkProvider.provideCommonApiRetrofit(BuildConfig.API_URL)


    @Singleton
    @Provides
    fun provideUserAuthAPI(retrofit: Retrofit): UserAuthAPI =
        retrofit.create(UserAuthAPI::class.java)

    @Singleton
    @Provides
    fun provideNetworkProvider() = NetworkProvider()


    @Singleton
    @Provides
    fun provideSchedulers(): RxSchedulers = RxSchedulers()

    @Singleton
    @Provides
    fun provideUserAuthRepo(api: UserAuthAPI): UserAuthRepository =
        UserAuthDataRepository(api)
}