package com.degradators.degradators.model.comment

import java.io.Serializable

data class CommentBlock(
    var text: String = "",
    var type: String = "",
    var index: Int = 0
) : Serializable