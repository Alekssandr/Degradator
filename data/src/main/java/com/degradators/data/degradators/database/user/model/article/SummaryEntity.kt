package com.degradators.data.degradators.database.user.model.article

import java.io.Serializable

data class SummaryEntity(
    var like: Int? = 0,
    var dislike: Int? = 0,
    var comment: Int? = 0,
    var views: Int? = 0,
    var loads: Int? = 0
) : Serializable