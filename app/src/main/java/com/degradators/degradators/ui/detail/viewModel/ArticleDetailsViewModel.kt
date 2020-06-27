package com.degradators.degradators.ui.detail.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.Block
import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.articles.LikeUseCase
import com.degradators.degradators.usecase.comment.AddCommentUseCase
import com.degradators.degradators.usecase.comment.CommentUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ArticleDetailsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
    private val likeUseCase: LikeUseCase,
    private val commentUseCase: CommentUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val schedulers: RxSchedulers
) : BaseViewModel() {

    private val disposables = CompositeDisposable()
    val commentList: MutableLiveData<List<CommentList>> = MutableLiveData()
    val addCommentVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }

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
                    if(it.messageList.isEmpty()){
                        addCommentVisibility.value = View.VISIBLE
                    } else{
                        addCommentVisibility.value = View.GONE
                    }
                    commentList.value = it.messageList
                    Log.d("Test111", "onComplete")
                }, onError = {
                    Log.e("Test111", "error: ${it.message} ?: ")
                })
    }

    fun putComment(commentList: CommentList) {
        val block = Block(text = commentList.content[0].text, type = "text")
        disposables +=
            addCommentUseCase.execute(settingsPreferences.clientId,
                NewComment(commentList.ancestorId, commentList.parentPostId, listOf(block)))
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .subscribeBy(onComplete = {
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