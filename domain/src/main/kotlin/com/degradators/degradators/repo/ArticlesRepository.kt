package com.degradators.degradators.repo

import com.degradators.degradators.model.Articles
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ArticlesRepository {
    fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles>
    fun getLike(clientId: String, articleId: String, like: Int): Completable
}