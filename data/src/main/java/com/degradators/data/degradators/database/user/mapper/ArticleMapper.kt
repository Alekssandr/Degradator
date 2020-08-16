package com.degradators.data.degradators.database.user.mapper

import com.degradators.data.degradators.database.user.model.article.ArticleBlockEntity
import com.degradators.data.degradators.database.user.model.article.ArticleMessageEntity
import com.degradators.data.degradators.database.user.model.article.SummaryEntity
import com.degradators.degradators.model.article.ArticleBlock
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.model.article.Summary


fun ArticleMessageEntity.toModel() =
    ArticleMessage(
        id ?: "",
        time ?: 0,
        responseTime ?: 0,
        type ?: "",
        summary.toSummaryModel(),
        userId ?: "",
        header ?: "",
        userName?.userName() ?: "",
        userPhoto ?: "",
        content.map { it.toModel() },
        like ?: 0
    )

fun SummaryEntity.toSummaryModel() =
    Summary(
        like ?: 0,
        dislike ?: 0,
        comment ?: 0,
        views ?: 0,
        loads ?: 0
    )

fun ArticleBlockEntity.toModel() =
    ArticleBlock(
        url ?: "",
        text ?: "",
        type ?: "",
        index ?: 0
    )

fun String?.userName() =
    this?.let { if (indexOf(" ") > 1) substring(0, indexOf(" ")) else this }

