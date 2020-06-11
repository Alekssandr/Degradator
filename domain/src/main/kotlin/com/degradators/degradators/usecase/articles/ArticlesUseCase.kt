package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.article.Articles
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Single
import javax.inject.Inject

class ArticlesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(clientId: String, type: String, skip: Long): Single<Articles> {
        return articlesRepository.getListOfPost(clientId, type, skip)
    }
}
