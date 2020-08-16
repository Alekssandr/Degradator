package com.degradators.data.degradators.database.user.model.article

data class ArticleBlockEntity(
    var url: String? = "",
    var text: String? = "",
    var type: String? = "",
    var index: Int? = 0
)