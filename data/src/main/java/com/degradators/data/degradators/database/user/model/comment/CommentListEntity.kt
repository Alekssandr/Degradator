package com.degradators.data.degradators.database.user.model.comment

data class CommentListEntity(
    var id: String = "",
    var time: Long = 0,
    var responseTime: Long = 0,
    var type: String = "",
    var ancestorId: String = "",
    var parentPostId: String = "",
    var userId: String = "",
    var header: String = "",
//    var content: List<ArticleBlockEntity> = emptyList(),
    var like: Int = 0
)
