package com.degradators.degradators.ui.addArticles.components

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.ui.addArticles.model.AddArticleAction
import com.degradators.degradators.ui.addArticles.model.AddArticleActionMain

class FabAddingListContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var _adapter: FabAddingAdapter = FabAddingAdapter()

    init {
        adapter = _adapter
    }

    fun setData(list: List<AddArticleAction>?) {
        _adapter.updateList(list)
    }

    fun setCallback(onItemClicked: ((AddArticleActionMain.AddArticle) -> Unit)? = null){
        _adapter.setCallback(onItemClicked)
    }
}


