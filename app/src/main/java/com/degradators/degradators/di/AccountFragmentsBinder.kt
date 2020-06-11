package com.degradators.degradators.di

import com.degradators.degradators.di.scopes.PerFragment
import com.degradators.degradators.ui.account.AccountFragment
import com.degradators.degradators.ui.account.signup.SignUpFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AccountFragmentsBinder {

    @ContributesAndroidInjector(modules = [AccountFragmentsModule::class])
    @PerFragment
    abstract fun bindSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector(modules = [AccountFragmentsModule::class])
    @PerFragment
    abstract fun bindAccountFragment(): AccountFragment

}