package com.degradators.degradators.repo

import com.degradators.degradators.model.Articles
import com.degradators.degradators.model.NewPost
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

interface ArticlesRepository {
    fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles>
    fun getLike(clientId: String, articleId: String, like: Int): Completable
    fun addArticle(clientId: String, newPost: NewPost): Completable
}