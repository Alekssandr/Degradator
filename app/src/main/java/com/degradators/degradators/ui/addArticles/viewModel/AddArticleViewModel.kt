package com.degradators.degradators.ui.addArticles.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.model.Block
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.ui.addArticles.components.TYPE_IMAGE
import com.degradators.degradators.ui.addArticles.model.ArticleItem
import com.degradators.degradators.usecase.articles.AddImageUseCase
import com.degradators.degradators.usecase.articles.AddNewArticleUseCase
import io.reactivex.Observable
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

    val articleContents: MutableList<Block> = mutableListOf()
    private val disposables = CompositeDisposable()


    fun addArticle(
        articleHeader: String,
        articleList: MutableList<ArticleItem>
    ) {
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                putArticle(it.toList(), articleList, articleHeader)
            }, onError = {
                Log.e("Error", it.message ?: "")
            })
    }

    private fun putArticle(
        urls: List<String>,
        articleList: MutableList<ArticleItem>,
        articleHeader: String
    ) {
        articleList.filter { it.type == TYPE_IMAGE  }.forEachIndexed { index, articleItem ->
            articleItem.imagePath = urls.get(index)
        }

        articleList.forEach {
            if(it.type == TYPE_IMAGE){
                articleContents.add(Block(url = it.imagePath, type = "img"))
            } else {
                articleContents.add(Block(text = it.title, type = "text"))
            }
        }

        disposables += addNewArticleUseCase
            .execute(settingsPreferences.clientId, NewPost(articleHeader, articleContents.toList()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onComplete = {
               Log.d("test111", "yup!")
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}