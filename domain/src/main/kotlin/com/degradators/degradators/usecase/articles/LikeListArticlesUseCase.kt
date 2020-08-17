package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.PostIds
import com.degradators.degradators.model.article.Articles
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Single
import javax.inject.Inject

class LikeListArticlesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(postIds: PostIds): Single<Articles> {
        return articlesRepository.getArticlesByList(postIds)
    }
}
