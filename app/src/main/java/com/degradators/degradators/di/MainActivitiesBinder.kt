package com.degradators.degradators.di

import com.degradators.degradators.MainActivity
import com.szczecin.englishtamagotchi.app.di.scopes.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivitiesBinder {
    @ContributesAndroidInjector(
        modules = [
            MainFragmentsBinder::class
        ]
    )

    @PerActivity
    abstract fun bindMainActivity(): MainActivity


}