package com.degradators.data.degradators.database.user.mapper

import com.degradators.data.degradators.database.user.model.article.ArticlesEntity
import com.degradators.degradators.model.article.Articles


fun ArticlesEntity.toArticle(): Articles =
    Articles(
        emptyList()
    )
