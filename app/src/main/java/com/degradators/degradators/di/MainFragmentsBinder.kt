package com.degradators.degradators.di

import com.degradators.degradators.ui.main.PlaceholderFragment
import com.degradators.degradators.ui.userMenu.MyListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBinder {

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindPlaceholderFragment(): PlaceholderFragment

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindMyListFragment(): MyListFragment


}