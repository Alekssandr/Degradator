package com.degradators.degradators.ui.userMenu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.PostIds
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.main.ArticlesViewModel
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.articles.LikeListArticlesUseCase
import com.degradators.degradators.usecase.articles.LikeUseCase
import com.degradators.degradators.usecase.articles.ReportUseCase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MyListViewModel @Inject constructor(
    private val likeListArticlesUseCase: LikeListArticlesUseCase,
    private val likeUseCase: LikeUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers,
    private val reportUseCase: ReportUseCase
) : ArticlesViewModel(reportUseCase, settingsPreferences) {
    private val disposables = CompositeDisposable()

    val articleMessage: MutableLiveData<List<ArticleMessage>> = MutableLiveData()

    val text: MutableLiveData<String> = MutableLiveData<String>()

    fun setIndex(list: List<String>) {
        getArticlesBy(PostIds(list))
    }

    fun subscribeForItemClick(clickItemObserver: Observable<Pair<String, Int>>) {
        disposables +=
            clickItemObserver
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribe {
                    getLikes(it)
                }

    }

    private fun getLikes(it: Pair<String, Int>) {
        disposables +=
            likeUseCase.execute(settingsPreferences.token, it.first, it.second)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    text.value = "ddddd"
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

    private fun getArticlesBy(postIds: PostIds) {
        disposables += likeListArticlesUseCase
            .execute(postIds)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                articleMessage.value = it.messageList
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }

}