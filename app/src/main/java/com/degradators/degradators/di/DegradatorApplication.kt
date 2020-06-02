package com.degradators.degradators.di

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DegradatorApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        daggerInit()
    }

    override fun androidInjector(): AndroidInjector<Any>  = androidInjector

    private fun daggerInit() {
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}