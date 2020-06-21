package com.degradators.degradators.model

data class NewComment(
    var ancestorId: String = "",
    var parentPostId: String = "",
    var content: List<Block> = emptyList()
)

