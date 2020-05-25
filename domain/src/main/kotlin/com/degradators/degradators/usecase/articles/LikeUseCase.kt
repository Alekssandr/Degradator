package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.Articles
import com.degradators.degradators.repo.ArticlesRepository
import com.degradators.degradators.repo.UserAuthRepository
import com.google.gson.JsonElement
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class LikeUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository

) {
    fun execute(clientId: String, articleId: String, like: Int): Completable {
        return articlesRepository.getLike(clientId, articleId, like)
    }
}
