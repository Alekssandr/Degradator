package com.degradators.data.degradators.database.user.model

data class ArticleMessageEntity(
    var id: String = "",
    var time: Long = 0,
    var responseTime: Long = 0,
    var type: String = "",
    var userId: String = "",
    var header: String = "",
    var content: List<ArticleBlockEntity> = emptyList(),
    var like: Int = 0
)