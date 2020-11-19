package com.degradators.degradators.ui.userMenu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.articles.GetSubmissionsArticleUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MySubmissionsViewModel @Inject constructor(
    private val getSubmissionsArticleUseCase: GetSubmissionsArticleUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : BaseViewModel() {
    private val disposables = CompositeDisposable()

    val articleMessage: MutableLiveData<List<ArticleMessage>> = MutableLiveData()

    val text: MutableLiveData<String> = MutableLiveData<String>()

    fun getSubmissionsArticles(type: String) {
        disposables += getSubmissionsArticleUseCase
            .execute(settingsPreferences.token, settingsPreferences.clientId, 0, type)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                articleMessage.value = it.messageList
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }

}