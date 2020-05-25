package com.degradators.data.degradators.database.user.mapper

import com.degradators.data.degradators.database.user.model.ArticlesEntity
import com.degradators.degradators.model.Articles


fun ArticlesEntity.toArticle(): Articles = Articles(
    emptyList()
)
