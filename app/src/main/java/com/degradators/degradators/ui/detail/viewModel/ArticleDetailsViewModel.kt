package com.degradators.degradators.ui.detail.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.usecase.articles.LikeUseCase
import com.degradators.degradators.usecase.comment.CommentUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ArticleDetailsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
    private val likeUseCase: LikeUseCase,
    private val commentUseCase: CommentUseCase,
    private val schedulers: RxSchedulers
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val closeScreen = MutableLiveData<Unit>()

    fun putLikes(it: Pair<String, Int>) {
        disposables +=
            likeUseCase.execute(settingsPreferences.clientId, it.first, it.second)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
                    Log.d("Test111", "onComplete")
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

    fun getComment(id: String) {
        disposables +=
            commentUseCase.execute(settingsPreferences.clientId, id)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onSuccess = {
                    val a = it
                    Log.d("Test111", "onComplete")
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}