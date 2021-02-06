package com.degradators.degradators.usecase.articles

import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Completable
import javax.inject.Inject

class ReportUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(token: String, articleId: String, reason: String): Completable {
        return articlesRepository.report(token, articleId, reason)
    }
}
