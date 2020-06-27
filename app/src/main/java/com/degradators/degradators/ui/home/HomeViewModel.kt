package com.degradators.degradators.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.usecase.SystemSettingsUseCase
import com.degradators.degradators.usecase.articles.ArticlesUseCase
import com.degradators.degradators.usecase.articles.LikeUseCase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val likeUseCase: LikeUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val _index = MutableLiveData<Int>()

    val text: MutableLiveData<String> = MutableLiveData<String>()

    val articleMessage: MutableLiveData<List<ArticleMessage>> = MutableLiveData()

    private val disposables = CompositeDisposable()

    fun setIndex(index: Int) {
        _index.value = index
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (settingsPreferences.clientId.isEmpty()) getSystemSetting()
        getArticles()
    }

    private fun getSystemSetting() {
        disposables += systemSettingsUseCase
            .execute()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.clientId = it
                Log.d("Test111", "systemSettings: $it")
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }

    private fun getTabName(): String =
        when (_index.value) {
            1 -> "top"
            2 -> "new"
            else -> {
                "monthly"
            }
        }


    private fun getArticles() {
        disposables += articlesUseCase
            .execute(settingsPreferences.clientId, getTabName(), 0)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                articleMessage.value = it.messageList
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
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
            likeUseCase.execute(settingsPreferences.clientId, it.first, it.second)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    text.value = "ddddd"
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

}