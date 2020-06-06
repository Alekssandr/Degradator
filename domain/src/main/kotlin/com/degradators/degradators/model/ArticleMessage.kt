package com.degradators.degradators.model

import java.io.Serializable

data class ArticleMessage(
    var id: String = "",
    var time: Long = 0,
    var responseTime: Long = 0,
    var type: String = "",
    var summary: Summary,
    var userId: String = "",
    var header: String = "",
    var content: List<ArticleBlock> = emptyList()
) : Serializable