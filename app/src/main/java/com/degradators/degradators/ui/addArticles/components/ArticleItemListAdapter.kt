package com.degradators.degradators.ui.addArticles.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.ui.addArticles.model.ArticleItem

//const val TYPE_0 = 0
const val TYPE_IMAGE = 0
const val TYPE_TEXT = 1


class ArticleItemListAdapter :
    ListAdapter<ArticleItem, RecyclerView.ViewHolder>(
        NavigationItemDiffCallback()
    ) {
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


//		ItemViewHolder(
//			LayoutInflater.from(parent.context).inflate(
//				R.layout.item_navigation,
//				parent,
//				false
//			) as NavigationButton
//		)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position).type) {
            TYPE_IMAGE -> {
                val a = (holder as ImageViewHolder)
                a.bind()
            }
            TYPE_TEXT -> {
                val a = (holder as TextViewHolder)
                a.bind()
            }
        }
    }

//	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//
////		if (articleMessageList[position].content[0].url.isNullOrEmpty()) { // put your condition, according to your requirements
////			val a = (holder as TextViewHolder)
////			a.bind(articleMessageList[position])
////		} else {
////			val a = (holder as ImageViewHolder)
////			a.bind(articleMessageList[position])
////		}
//		holder.bindData(getItem(position))
//	}

    override fun getItemViewType(position: Int): Int {
        when (getItem(position).type) {
            0 -> return TYPE_IMAGE
            1 -> return TYPE_TEXT
        }
        return TYPE_TEXT
    }

    //	class ItemViewHolder(private val button: NavigationButton) :
//		RecyclerView.ViewHolder(button) {
//
//		fun bindData(model: NavigationItem) = button.apply {
//			setText(model.title)
//			setImageResource(model.iconResId)
//			setOnClickListener { model.action.invoke() }
//		}
//	}
//
    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind() {
        }
    }

    class TextViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind() {
        }

    }


}

class NavigationItemDiffCallback : DiffUtil.ItemCallback<ArticleItem>() {
    override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
        oldItem == newItem
}