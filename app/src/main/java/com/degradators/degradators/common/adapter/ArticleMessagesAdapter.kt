package com.degradators.degradators.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.degradators.degradators.R
import com.degradators.degradators.model.ArticleMessage
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.article_item_image_text.view.*
import kotlinx.android.synthetic.main.article_item_text_image.view.*

const val TYPE_IMAGE = 0
const val TYPE_TEXT = 1

class ArticleMessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articleMessageList: List<ArticleMessage> = emptyList()
    private val publishSubjectItem = PublishSubject.create<Pair<String, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_IMAGE -> return ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.article_item_image_text, parent, false)
            )
            TYPE_TEXT -> return TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.article_item_text_image, parent, false)
            )
        }
        return TextViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_item_text_image, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return articleMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (articleMessageList[position].content[0].url.isNullOrEmpty()) {
            return TYPE_TEXT
        }
        return TYPE_IMAGE
    }

    fun getClickItemObserver(): Observable<Pair<String, Int>> {
        return publishSubjectItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (articleMessageList[position].content[0].url.isNullOrEmpty()) { // put your condition, according to your requirements
            val a = (holder as TextViewHolder)
            a.bind(articleMessageList[position])
            val isImage = false

            setLikeDislike(articleMessageList[position], holder.itemView, isImage)
        } else {
            val a = (holder as ImageViewHolder)
            a.bind(articleMessageList[position])

            val isImage = true
            setLikeDislike(articleMessageList[position], holder.itemView, isImage)
        }
    }

    private fun setLikeDislike(
        articleMessage: ArticleMessage,
        item: View,
        isImage: Boolean
    ) {
        var listViewText =
            listOf(item.averageTextLike, item.textImageViewLike, item.textImageViewDislike)
        if (isImage) listViewText =
            listOf(item.averageImageLike, item.imageViewLike, item.imageViewDislike)

        val summary = articleMessage.summary
        val averageScore = (listViewText[0] as TextView)
        averageScore.text = (summary.like - summary.dislike).toString()
        val like = (listViewText[1] as ImageButton)
        val disLike = (listViewText.last() as ImageButton)

        if (averageScore.text.toString().toInt() <= summary.like - summary.dislike) {
            like.setOnClickListener {
                summary.like = summary.like + 1
                if(!disLike.isEnabled){
                    summary.dislike = summary.dislike - 1
                }
                disLike.isEnabled = true
                like.isEnabled = false
                averageScore.text = (summary.like - summary.dislike).toString()
                publishSubjectItem.onNext(Pair(articleMessage.id, 1))
            }
        }

        if (averageScore.text.toString().toInt() >= summary.like - summary.dislike) {
            disLike.setOnClickListener {

                summary.dislike = summary.dislike + 1
                if(!like.isEnabled){
                    summary.like = summary.like - 1
                }
                disLike.isEnabled = false
                like.isEnabled = true
                averageScore.text = (summary.like - summary.dislike).toString()
                publishSubjectItem.onNext(Pair(articleMessage.id, -1))
            }
        }
    }

    fun update(items: List<ArticleMessage>) {
        this.articleMessageList = items
        notifyDataSetChanged()
    }


    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(articleMessage: ArticleMessage) {
            itemView.imageTextTitle.text = articleMessage.header
            setImage(itemView.imageViewText, articleMessage.content.first().url)
            if (articleMessage.content.size > 1) {
                itemView.imageTextView.text = articleMessage.content[1].text
            }
        }

        private fun setImage(image: ImageView, url: String) {
            Glide.with(itemView.context)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                )
                .into(image)
        }
    }

    class TextViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(articleMessage: ArticleMessage) {
            itemView.textImageTitle.text = articleMessage.header
            itemView.textImage.text = articleMessage.content.first().text
            if (articleMessage.content.size > 1) {
                setImage(itemView, articleMessage.content[1].url)
            }
        }

        private fun setImage(itemView: View, url: String) {
            Glide.with(itemView.context)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                )
                .into(itemView.textImageView)
        }
    }


}
