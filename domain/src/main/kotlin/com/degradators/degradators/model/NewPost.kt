package com.degradators.degradators.model

data class NewPost(
    var header: String = "",
    var content: List<Block> = emptyList()
)

data class Block (
    var text: String = "",
    var url: String = "",
    var type: String
)

