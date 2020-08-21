package com.degradators.degradators.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.usecase.RemoveArticlesUseCase
import com.degradators.degradators.usecase.SystemSettingsUseCase
import com.degradators.degradators.usecase.articles.ArticlesUseCase
import com.degradators.degradators.usecase.articles.LikeUseCase
import com.degradators.degradators.usecase.user.UserInfoUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val likeUseCase: LikeUseCase,
    private val removeArticlesUseCase: RemoveArticlesUseCase,
    private val userInfoUseCase: UserInfoUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val _index = MutableLiveData<Int>()

    val text: MutableLiveData<String> = MutableLiveData<String>()

    val articleMessage: MutableLiveData<Pair<List<ArticleMessage>, Boolean>> = MutableLiveData()

    private val disposables = CompositeDisposable()

    fun setIndex(index: Int) {
        _index.value = index
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
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
            .toObservable()
            .flatMapIterable {
                it.messageList
            }
            .flatMapSingle {
                changeRemovable(it)
            }
            .toList()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                articleMessage.value = Pair(it, skip == 0.toLong())
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }

    private fun changeRemovable(it: ArticleMessage): Single<ArticleMessage> {
        if (settingsPreferences.userId.isNotEmpty()) {
            if (it.userId == settingsPreferences.userId &&
                it.time + TimeUnit.DAYS.toMillis(1) > System.currentTimeMillis()
            ) {
                it.isRemovable = true
            }
        } else {
            if (it.clientId == settingsPreferences.clientId &&
                it.time + TimeUnit.MINUTES.toMillis(2) > System.currentTimeMillis()
            ) {
                it.isRemovable = true
            }
        }
        return Single.just(it)
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

    fun removeArticles(messageId: String) {
        disposables +=
            removeArticlesUseCase.execute(settingsPreferences.token, settingsPreferences.clientId, messageId)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    getArticles()
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

}