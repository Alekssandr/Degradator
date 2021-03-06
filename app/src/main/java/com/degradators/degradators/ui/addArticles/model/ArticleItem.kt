package com.degradators.degradators.ui.addArticles.model

import android.graphics.Bitmap
import android.net.Uri

data class ArticleItem(val type: Int, var title: String = "", val bitmap: Bitmap? = null,
                       var imagePath: String = "", var videoPath: String = "", val action: (Int) -> Unit)