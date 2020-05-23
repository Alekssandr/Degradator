package com.degradators.degradators.di

import com.degradators.degradators.ui.account.AccountFragment
import com.degradators.degradators.ui.account.signup.SignUpFragment
import com.degradators.degradators.ui.main.PlaceholderFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBinder {

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindAccountFragment(): AccountFragment

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindPlaceholderFragment(): PlaceholderFragment

}