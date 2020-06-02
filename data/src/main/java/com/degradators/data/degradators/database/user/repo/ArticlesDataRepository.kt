package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.ArticlesAPI
import com.degradators.data.degradators.database.user.mapper.toArticle
import com.degradators.degradators.model.Articles
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import okhttp3.ResponseBody
import retrofit2.Response

class ArticlesDataRepository(
    private val api: ArticlesAPI
) : ArticlesRepository {

    override fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles> =
        api.getListOfPost(clientId, type, skip)
//            .map { it.toArticle() }
//            .flatMap {
//                Observable.zip<Articles, Int, Articles>(
//                    Observable.just(it),
//                    api.getLike(it.messageList.get(0).id).toObservable(),
//                    BiFunction { article, like ->
//                        article.messageList.get(0).like = like
//                        article
//                    }
//                )
//                it.messageList.forEach {
//                 api.getLike(it.id)
//                }

    override fun getLike(clientId: String, articleId: String, like: Int): Completable =
        api.getLike(clientId, articleId, like)

    override fun addArticle(clientId: String, newPost: NewPost): Completable =  api.addArticle(clientId, newPost)
}

