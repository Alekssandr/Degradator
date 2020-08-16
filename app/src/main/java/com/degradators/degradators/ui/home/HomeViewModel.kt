package com.degradators.degradators.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.PostIds
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.usecase.RemoveArticlesUseCase
import com.degradators.degradators.usecase.SystemSettingsUseCase
import com.degradators.degradators.usecase.articles.ArticlesUseCase
import com.degradators.degradators.usecase.articles.LikeListArticlesUseCase
import com.degradators.degradators.usecase.articles.LikeUseCase
import com.degradators.degradators.usecase.user.UserInfoUseCase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val likeUseCase: LikeUseCase,
    private val removeArticlesUseCase: RemoveArticlesUseCase,
    private val userInfoUseCase: UserInfoUseCase,
    private val likeListArticlesUseCase: LikeListArticlesUseCase,
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
//        if (settingsPreferences.token.isNotEmpty()) getUser()
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


    fun getArticles(skip: Long = 0) {
        disposables += articlesUseCase
            .execute(settingsPreferences.clientId, getTabName(), skip)//20
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
            likeUseCase.execute(settingsPreferences.token, it.first, it.second)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    text.value = "ddddd"
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

    //TODO pass list of user's likes
    fun getArticlesBy(list: MutableList<String>) {
//        val list = mutableListOf<String>()
        list.add("5f392b16cf0df008aaa40534")
        disposables += likeListArticlesUseCase
            .execute(PostIds(idsList = list))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                articleMessage.value = it.messageList
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }

    private fun removeArticles(messageId: String) {
        disposables +=
            removeArticlesUseCase.execute(settingsPreferences.clientId, messageId)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    text.value = "removed"
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

}