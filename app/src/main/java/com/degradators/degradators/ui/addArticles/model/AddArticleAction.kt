package com.degradators.degradators.ui.addArticles.model

import com.degradators.degradators.R

class AddArticleAction (
    val addArticleEvent: AddArticleActionMain.AddArticle = AddArticleActionMain.AddArticle.WriteComment,
    val title: Int = R.string.write_comment,
    val drawable: Int = R.drawable.ic_create_black_18dp
)