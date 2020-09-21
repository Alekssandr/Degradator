package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.ArticlesAPI
import com.degradators.data.degradators.database.user.mapper.toModel
import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.model.PostIds
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

    override fun getListSubmissions(token: String, clientId: String, skip: Long): Single<Articles> =
        api.getListSubmissions(token, clientId, skip).map { articlesEntity ->
            articlesEntity.messageList
                .let {
                    it.filter { it.type == "POST" }
                        .map { articlesMessageEntity -> articlesMessageEntity.toModel() }
                }.let { Articles(it) }
        }

    override fun getArticlesByList(articleId: PostIds): Single<Articles> =
        api.getArticlesByList(articleId)
            .map { articlesEntity ->
                articlesEntity.messageList
                    .let {
                        it.map { articlesMessageEntity -> articlesMessageEntity.toModel() }
                    }.let { Articles(it) }
            }


    override fun getLike(clientId: String, articleId: String, like: Int): Completable =
        api.getLike(clientId, articleId, like)

    override fun addArticle(token: String, clientId: String, newPost: NewPost): Completable =
        api.addArticle(token, clientId, newPost)

    override fun addComment(clientId: String, newComment: NewComment): Completable =
        api.addComment(clientId, newComment)

    override fun getComment(clientId: String, id: String): Single<Comments> =
        api.getComment(clientId, id)

    override fun removeArticles(token: String, clientId: String, messageId: String): Completable =
        api.removeArticles(token, clientId, messageId)
}

