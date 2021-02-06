package com.degradators.degradators.ui.main

import com.degradators.degradators.di.common.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class ArticlesActivity<T : ArticlesViewModel> : DaggerAppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelFactory<T>

    abstract val viewModel: T
}