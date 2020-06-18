package com.degradators.degradators.usecase.comment

import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(clientId: String, newComment: NewComment): Completable {
        return articlesRepository.addComment(clientId, newComment)
    }
}
