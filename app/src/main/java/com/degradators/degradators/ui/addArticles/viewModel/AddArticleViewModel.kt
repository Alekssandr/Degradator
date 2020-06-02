package com.degradators.degradators.ui.addArticles.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.model.Block
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.ui.addArticles.components.TYPE_IMAGE
import com.degradators.degradators.ui.addArticles.model.ArticleItem
import com.degradators.degradators.usecase.articles.AddImageUseCase
import com.degradators.degradators.usecase.articles.AddNewArticleUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddArticleViewModel @Inject constructor(
    private val addImageUseCase: AddImageUseCase,
    private val addNewArticleUseCase: AddNewArticleUseCase,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val closeScreen = MutableLiveData<Unit>()
    val progressBarVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    fun addArticle(
        articleHeader: String,
        articleList: MutableList<ArticleItem>
    ) {
        progressBarVisibility.value = View.VISIBLE
        val obs: Observable<List<ArticleItem>> = Observable.fromArray(articleList)
        disposables += obs
            .flatMapIterable {
                it
            }
            .filter { it.type == TYPE_IMAGE }
            .flatMapSingle {
                addImageUseCase.execute(
                    settingsPreferences.clientId,
                    it.imagePath
                )
            }
            .toList()
            .flatMap {
                getBlockContent(articleList, it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                putArticle(it.toList(), articleHeader)
            }, onError = {
                Log.e("Error", it.message ?: "")
            })
    }

    private fun putArticle(
        articleList: List<Block>,
        articleHeader: String
    ) {
        disposables += addNewArticleUseCase
            .execute(settingsPreferences.clientId, NewPost(articleHeader, articleList))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onComplete = {
                progressBarVisibility.value = View.GONE
                closeScreen.value = Unit
                Log.d("test111", "yup!")
            }, onError = {
                progressBarVisibility.value = View.GONE
                closeScreen.value = Unit
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }

    private fun getBlockContent(
        articleList: MutableList<ArticleItem>,
        it: List<String>
    ): Single<MutableList<Block>> {
        val articleContents: MutableList<Block> = mutableListOf()

        articleList.filter { it.type == TYPE_IMAGE }.forEachIndexed { index, articleItem ->
            articleItem.imagePath = it.get(index)
        }
        articleList.forEach {
            if (it.type == TYPE_IMAGE) {
                articleContents.add(Block(url = it.imagePath, type = "img"))
            } else {
                articleContents.add(Block(text = it.title, type = "text"))
            }
        }
        return Single.just(articleContents)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}