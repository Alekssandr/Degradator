package com.degradators.degradators.model

import java.io.Serializable

data class ArticleBlock(
    var url: String = "",
    var text: String = "",
    var type: String = "",
    var index: Int = 0
) : Serializable