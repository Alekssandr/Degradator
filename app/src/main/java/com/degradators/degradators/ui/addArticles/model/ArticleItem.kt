package com.degradators.degradators.ui.addArticles.model

import android.graphics.Bitmap

data class ArticleItem(val type: Int, var title: String = "", val bitmap: Bitmap? = null,
                       var imagePath: String = "", val action: (Int) -> Unit)