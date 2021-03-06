package com.degradators.degradators.usecase

import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import javax.inject.Inject

class RemoveArticlesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(token: String, clientId: String, messageId: String): Completable {
        return articlesRepository.removeArticles(token, clientId, messageId)
    }
}
