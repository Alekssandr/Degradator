package com.degradators.degradators.di

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class DegradatorApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        daggerInit()
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    private fun daggerInit() {
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}