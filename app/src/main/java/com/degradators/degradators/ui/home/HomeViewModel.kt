package com.degradators.degradators.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.ArticleMessage
import com.degradators.degradators.model.User
import com.degradators.degradators.usecase.AuthUserUseCase
import com.degradators.degradators.usecase.SystemSettingsUseCase
import com.degradators.degradators.usecase.articles.ArticlesUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

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

    private fun getArticles() {
        disposables += articlesUseCase
            .execute(settingsPreferences.clientId, if(_index.value == 1) "top" else "new", 0)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
//                text.value = "ddddd"
//                text.postValue("aaaaa")
                articleMessage.value = it.messageList
//                articleMessage.value = it.messageList
                Log.d("Test111", "Articles: ${it.messageList.toString()}")
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }


}