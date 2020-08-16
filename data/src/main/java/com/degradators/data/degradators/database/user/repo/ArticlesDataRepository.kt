package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.ArticlesAPI
import com.degradators.data.degradators.database.user.mapper.toModel
import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.model.article.Articles
import com.degradators.degradators.model.comment.Comments
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import io.reactivex.Single

class ArticlesDataRepository(
    private val api: ArticlesAPI
) : ArticlesRepository {

    override fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles> =
        api.getListOfPost(clientId, type, skip).map { articlesEntity ->
            articlesEntity.messageList
                .let {
                    it.map { articlesMessageEntity -> articlesMessageEntity.toModel() }
                }.let { Articles(it) }
        }

    override fun getLike(clientId: String, articleId: String, like: Int): Completable =
        api.getLike(clientId, articleId, like)

    override fun addArticle(clientId: String, newPost: NewPost): Completable =
        api.addArticle(clientId, newPost)

    override fun addComment(clientId: String, newComment: NewComment): Completable =
        api.addComment(clientId, newComment)

    override fun getComment(clientId: String, id: String): Single<Comments> =
        api.getComment(clientId, id)

    override fun removeArticles(clientId: String, messageId: String): Completable =
        api.removeArticles(clientId, messageId)
}

