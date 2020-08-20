package com.degradators.degradators.model.article

import java.io.Serializable

data class ArticleMessage(
    var id: String = "",
    var time: Long = 0,
    var responseTime: Long = 0,
    var type: String = "",
    var summary: Summary,
    var userId: String = "",
    var clientId: String = "",
    var header: String = "",
    var userName: String = "",
    var userPhoto: String = "",
    var content: List<ArticleBlock> = emptyList(),
    var like: Int = 0,
    var isRemovable: Boolean = false
) : Serializable