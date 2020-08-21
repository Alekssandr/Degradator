package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.NewPost
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddNewArticleUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(token: String, clientId: String, newPost: NewPost): Completable {
        return articlesRepository.addArticle(token, clientId, newPost)
    }
}
