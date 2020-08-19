package com.degradators.data.degradators.database.user.mapper

import com.degradators.data.degradators.database.user.model.article.ArticleBlockEntity
import com.degradators.data.degradators.database.user.model.article.ArticleMessageEntity
import com.degradators.data.degradators.database.user.model.article.SummaryEntity
import com.degradators.data.degradators.database.user.model.user.MarkListEntity
import com.degradators.data.degradators.database.user.model.user.UserEntity
import com.degradators.degradators.model.article.ArticleBlock
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.model.article.Summary
import com.degradators.degradators.model.user.MarkList
import com.degradators.degradators.model.user.User


fun UserEntity.toModel() =
    User(
        id ?: "",
        mail ?: "",
        password ?: "",
        username ?: "",

        login ?: "",
        photo ?: "",
        markList?.map { it.markListModel() } ?: emptyList()
    )

fun MarkListEntity.markListModel() =
    MarkList(
        messageId ?: "",
        time ?: 0,
        userId ?: "",
        value ?: 0)