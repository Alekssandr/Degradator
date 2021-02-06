package com.degradators.degradators.ui.main

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.usecase.articles.ReportUseCase

open class ArticlesViewModel(
    private val reportUseCase: ReportUseCase,
    private val  settingsPreferences: SettingsPreferences
) : ViewModel(), LifecycleObserver {
    fun hideArticles(articleId: String, reports: String) {
        reportUseCase.execute(settingsPreferences.token, articleId, reports)
    }
}