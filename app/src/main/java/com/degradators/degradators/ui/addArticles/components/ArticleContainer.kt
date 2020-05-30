package com.degradators.degradators.ui.addArticles.components

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.ui.addArticles.model.ArticleItem

class ArticleContainer @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

	private var _adapter: ArticleItemListAdapter = ArticleItemListAdapter()

	init {
		layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
		adapter = _adapter
	}
	
	fun setArticleItems(items : List<ArticleItem>) = _adapter.submitList(items)
}


