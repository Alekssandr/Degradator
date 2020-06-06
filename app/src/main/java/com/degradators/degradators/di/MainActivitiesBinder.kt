package com.degradators.degradators.di

import com.degradators.degradators.MainActivity
import com.degradators.degradators.ui.addArticles.AddArticleActivity
import com.degradators.degradators.ui.detail.DetailActivity
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

    @ContributesAndroidInjector(modules = [MainActivitiesModule::class])
    @PerActivity
    abstract fun bindAddArticleActivity(): AddArticleActivity

    @ContributesAndroidInjector(modules = [MainActivitiesModule::class])
    @PerActivity
    abstract fun bindDetailActivity(): DetailActivity

}