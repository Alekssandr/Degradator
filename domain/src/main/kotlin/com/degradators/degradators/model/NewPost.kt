package com.degradators.degradators.model

data class NewPost(
    var header: String = "",
    var content: List<Block> = emptyList()
)

data class BlockText (
    var text: String = "",
    var type: String
) : Block

data class BlockImage(
    var url: String = "",
    var type: String
) : Block

interface Block