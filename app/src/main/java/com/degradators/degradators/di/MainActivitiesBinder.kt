package com.degradators.degradators.di

import com.degradators.degradators.MainActivity
import com.degradators.degradators.ui.addArticles.AddArticleActivity
import com.degradators.degradators.ui.detail.DetailActivity
import com.degradators.degradators.ui.login.LoginActivity
import com.degradators.degradators.di.scopes.PerActivity
import com.degradators.degradators.ui.login.RegisterActivity
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

    @ContributesAndroidInjector(modules = [MainActivitiesModule::class])
    @PerActivity
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [MainActivitiesModule::class])
    @PerActivity
    abstract fun bindRegisterActivity(): RegisterActivity


}