package com.degradators.degradators.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.degradators.data.degradators.database.user.api.ArticlesAPI

import com.degradators.data.degradators.database.user.api.UserAuthAPI
import com.degradators.data.degradators.database.user.network.NetworkProvider
import com.degradators.data.degradators.database.user.repo.ArticlesDataRepository
import com.degradators.data.degradators.database.user.repo.ImageDataRepository
import com.degradators.data.degradators.database.user.repo.UserAuthDataRepository
import com.degradators.degradators.BuildConfig
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.repo.UserAuthRepository

import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.repo.ArticlesRepository
import com.degradators.degradators.repo.ImageRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun providePreferences(sharedPreferences: SharedPreferences): SettingsPreferences =
        SettingsPreferences(sharedPreferences)

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
    fun provideArticlesAPI(retrofit: Retrofit): ArticlesAPI =
        retrofit.create(ArticlesAPI::class.java)

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

    @Singleton
    @Provides
    fun provideArticlesRepo(api: ArticlesAPI): ArticlesRepository =
        ArticlesDataRepository(api)

    @Singleton
    @Provides
    fun provideImageRepo(api: ArticlesAPI): ImageRepository =
        ImageDataRepository(api)
}