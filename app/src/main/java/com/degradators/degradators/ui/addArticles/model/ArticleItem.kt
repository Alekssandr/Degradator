package com.degradators.degradators.ui.addArticles.model

data class ArticleItem(val type: Int, val title: String = "", val url: String = "", val action: () -> Unit)