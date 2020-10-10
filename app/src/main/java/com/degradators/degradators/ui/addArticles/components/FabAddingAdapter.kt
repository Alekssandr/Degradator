package com.degradators.degradators.ui.addArticles.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.ui.addArticles.model.AddArticleAction
import com.degradators.degradators.ui.addArticles.model.AddArticleActionMain
import kotlinx.android.synthetic.main.fab_adding_list_item2.view.*

class FabAddingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var fabAddingList: MutableList<AddArticleAction> = mutableListOf()
    private var onItemClicked: ((AddArticleActionMain.AddArticle) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fab_adding_list_item2, parent, false)
        return FabAddingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fabAddingList.size
    }

    fun setCallback(onItemClicked: ((AddArticleActionMain.AddArticle) -> Unit)? = null) {
        this.onItemClicked = onItemClicked
    }

    fun updateList(list: List<AddArticleAction>?) {
        list?.let {
            fabAddingList.clear()
            fabAddingList.addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val heroViewHolder = (holder as FabAddingViewHolder)
        heroViewHolder.bind(fabAddingList[position])

        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(fabAddingList[position].addArticleEvent)
        }
    }


    class FabAddingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imageFab = itemView.imageFab
        private val titleFab = itemView.nameFab

        fun bind(articleAction: AddArticleAction) {
            titleFab.text = itemView.resources.getText(articleAction.title)
            val imageFabDrawable = ResourcesCompat.getDrawable(itemView.resources, articleAction.drawable, null)
            imageFab.setImageDrawable(imageFabDrawable)
        }
    }
}