package com.degradators.degradators.model.article

import java.io.Serializable

data class ArticleBlock(
    var url: String = "",
    var text: String = "",
    var type: String = "",
    var index: Int = 0,
    var urlImageForVideo: String = "",
    var urlVideo: String = ""
    ) : Serializable