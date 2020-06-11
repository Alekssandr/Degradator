package com.degradators.degradators.usecase.comment

import com.degradators.degradators.model.comment.Comments
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CommentUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(clientId: String, articleId: String): Single<Comments> {
        return articlesRepository.getComment(clientId, articleId)
    }
}
