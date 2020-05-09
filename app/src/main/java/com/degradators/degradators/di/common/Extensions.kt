package com.degradators.degradators.di.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM : ViewModel, F : ViewModelProvider.Factory> AppCompatActivity.viewModel(
    crossinline f: () -> F
): Lazy<VM> =
    lazy { ViewModelProviders.of(this, f.invoke()).get(VM::class.java) }

inline fun <reified VM : ViewModel, F : ViewModelProvider.Factory> Fragment.viewModel(crossinline f: () -> F): Lazy<VM> =
    lazy { ViewModelProviders.of(this, f.invoke()).get(VM::class.java) }