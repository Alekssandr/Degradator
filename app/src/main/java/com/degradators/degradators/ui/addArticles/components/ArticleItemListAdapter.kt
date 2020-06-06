package com.degradators.degradators.ui.addArticles.components

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.ui.addArticles.model.ArticleItem
import com.degradators.degradators.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.add_article_item_image.view.*
import kotlinx.android.synthetic.main.add_article_item_text.view.*


//const val TYPE_0 = 0
const val TYPE_IMAGE = 0
const val TYPE_TEXT = 1


class ArticleItemListAdapter : ListAdapter<ArticleItem, RecyclerView.ViewHolder>(ArticleItemDiffCallback()) {
    private var articleItemList: MutableList<ArticleItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_IMAGE -> return ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_article_item_image, parent, false)
            )
            TYPE_TEXT -> return TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_article_item_text, parent, false)
            )
        }
        return TextViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.add_article_item_text, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (articleItemList[position].type) {
            TYPE_IMAGE -> {
                val a = (holder as ImageViewHolder)
                a.bind(articleItemList[position])
            }
            TYPE_TEXT -> {
                val a = (holder as TextViewHolder)
                a.bind(articleItemList[position])
            }
        }
    }

    fun update(items: ArticleItem) {
        articleItemList.add(items)
        notifyItemInserted(articleItemList.size)
    }

    fun removeItemBy(position: Int) {
        articleItemList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        when (articleItemList[position].type) {
            0 -> return TYPE_IMAGE
            1 -> return TYPE_TEXT
        }
        return TYPE_TEXT
    }

    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(articleItem: ArticleItem) {
            itemView.addImage.setImageBitmap(articleItem.bitmap)
            itemView.removeImage.setOnClickListener {
                articleItem.action.invoke(layoutPosition)
            }
        }
    }

    class TextViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(articleItem: ArticleItem) {
            itemView.add_text.afterTextChanged {
                articleItem.title = it
            }
            itemView.add_text.setText(articleItem.title)
            itemView.removeText.setOnClickListener {
                articleItem.action.invoke(layoutPosition)
            }
        }

    }

    override fun getItemCount(): Int {
        return articleItemList.size
    }

    fun getArticleList(): MutableList<ArticleItem> {
      return  articleItemList
    }
}

class ArticleItemDiffCallback : DiffUtil.ItemCallback<ArticleItem>() {
    override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
        oldItem == newItem
}
