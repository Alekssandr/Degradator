package com.degradators.degradators.ui.detail.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.model.comment.Expanded

@BindingAdapter("commentsList")
fun RecyclerView.bindComments(items: List<CommentList>?) {
    items?.let {
        val adapter = adapter as CommentsAdapter
        val newCommentList: MutableList<CommentList> = mutableListOf()
        getAllListComments(items, newCommentList, 0)
        adapter.update(newCommentList)
    }
}

fun getAllListComments(items: List<CommentList>, newCommentList: MutableList<CommentList>, depth: Int) {
    items.forEach {
        it.depth = depth
        newCommentList.add(it)
        if (it.comments.isNotEmpty()) {
            it.isExpandedItem = Expanded.Open
            it.isEmptyComments = false
            getAllListComments(it.comments, newCommentList, depth+1)
        }
        else {
            it.isExpandedItem = Expanded.Default
            it.isEmptyComments = true
        }
    }
}