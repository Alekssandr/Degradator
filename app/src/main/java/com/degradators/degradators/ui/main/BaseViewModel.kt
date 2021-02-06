package com.degradators.degradators.ui.main

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.usecase.articles.ReportUseCase

open class BaseViewModel(
) : ViewModel(), LifecycleObserver