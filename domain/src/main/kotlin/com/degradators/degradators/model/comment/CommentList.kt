package com.degradators.degradators.model.comment

import com.degradators.degradators.model.article.Summary
import java.io.Serializable

data class CommentList(
    var id: String = "",
    var time: Long = 0,
    var responseTime: Long = 0,
    var type: String = "",
    var ancestorId: String = "",
    var parentPostId: String = "",
    var summary: Summary,
    var userId: String = "",
    var userName: String = "",
    var userPhoto: String = "",
    var header: String = "",
    var content: List<CommentBlock> = emptyList(),
    var shortContent: List<CommentBlock> = emptyList(),
    var comments: List<CommentList> = emptyList(),
    var depth: Int = 0,
    var isExpandedItem: Expanded,
    var isEmptyComments: Boolean = true
) : Serializable


sealed class Expanded {
    object Default : Expanded()
    object Open : Expanded()
    object Close : Expanded()
}
