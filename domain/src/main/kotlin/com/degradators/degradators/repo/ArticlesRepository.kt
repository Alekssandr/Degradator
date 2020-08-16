package com.degradators.degradators.repo

import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.article.Articles
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.model.PostIds
import com.degradators.degradators.model.comment.Comments
import io.reactivex.Completable
import io.reactivex.Single

interface ArticlesRepository {
    fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles>
    fun getComment(clientId: String, id: String): Single<Comments>
    fun getLike(clientId: String, articleId: String, like: Int): Completable
    fun addComment(clientId: String, newComment: NewComment): Completable
    fun addArticle(clientId: String, newPost: NewPost): Completable
    fun removeArticles(clientId: String, messageId: String): Completable
    fun getArticlesByList(articleId: PostIds): Single<Articles>
}