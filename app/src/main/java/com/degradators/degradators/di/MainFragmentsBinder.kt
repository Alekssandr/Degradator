package com.degradators.degradators.di

import com.degradators.degradators.ui.account.signup.SignUpFragment
import com.intive.kobold.app.di.scopes.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBinder {

    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun bindSignUpFragment(): SignUpFragment

}