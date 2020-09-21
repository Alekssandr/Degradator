package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.article.Articles
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Single
import javax.inject.Inject

class GetSubmissionsArticleUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(token: String, clientId: String, skip: Long, type: String): Single<Articles> {
        return articlesRepository.getListSubmissions(token, clientId, skip, type)
    }
}
