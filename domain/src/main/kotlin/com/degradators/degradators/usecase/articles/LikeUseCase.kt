package com.degradators.degradators.usecase.articles

import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import javax.inject.Inject

class LikeUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(clientId: String, articleId: String, like: Int): Completable {
        return articlesRepository.getLike(clientId, articleId, like)
    }
}
