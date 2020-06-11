package com.degradators.degradators.common.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.model.article.ArticleMessage


@BindingAdapter("articleMessageList")
fun RecyclerView.bindCommonWords(items: List<ArticleMessage>?) {
    items?.let {
        val adapter = adapter as ArticleMessagesAdapter
        adapter.update(items)
    }
}
