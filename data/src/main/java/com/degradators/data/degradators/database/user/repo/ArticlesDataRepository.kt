package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.ArticlesAPI
import com.degradators.data.degradators.database.user.api.UserAuthAPI
import com.degradators.degradators.model.Articles
import com.degradators.degradators.repo.ArticlesRepository
import io.reactivex.Single

class ArticlesDataRepository(
    private val api: ArticlesAPI
) : ArticlesRepository {

    override fun getListOfPost(clientId: String, type: String, skip: Long): Single<Articles> =
        api.getListOfPost(clientId, type, skip)

}

