package com.degradators.degradators.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.degradators.degradators.R
import com.degradators.degradators.model.article.ArticleMessage
import com.google.android.material.shape.CornerFamily
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.article_item_image_text.view.*

const val DETAILS_EXTRA = "details"
const val DETAILS_POSITION = "details_position"
const val DETAILS_LIKE = "details_like"

class ArticleMessagesAdapter(val listener: (Pair<ArticleMessage, Int>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articleMessageList: List<ArticleMessage> = emptyList()
    private val publishSubjectItem = PublishSubject.create<Pair<String, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_item_image_text, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return articleMessageList.size
    }

    fun getClickItemObserver(): Observable<Pair<String, Int>> {
        return publishSubjectItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val a = (holder as ImageViewHolder)
        a.bind(articleMessageList[position])
        setLikeDislike(articleMessageList[position], holder.itemView)
        setComment(articleMessageList[position], holder.itemView)
        setUser(articleMessageList[position], holder.itemView)
        a.itemView.setOnClickListener {
            val article = articleMessageList[position]
            val articleDetails =
                ArticleMessage(
                    id = article.id,
                    type = article.type,
                    summary = article.summary,
                    header = article.header,
                    content = article.content,
                    userPhoto = article.userPhoto,
                    userName = article.userName
                )
            listener(Pair(articleDetails, position))
        }
    }

    private fun setUser(articleMessage: ArticleMessage, itemView: View) {
        setImage(itemView, articleMessage.userPhoto)
        itemView.userName.text = articleMessage.userName
    }

    private fun setImage(itemView: View, url: String) {
        Glide.with(itemView.context)
            .load(url)
            .apply(
                RequestOptions()
                    .placeholder(R.mipmap.ic_launcher_round).fitCenter()
            )
            .into(itemView.userPhoto)
        itemView.userPhoto.shapeAppearanceModel =
            itemView.userPhoto.shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, itemView.resources.getDimension(R.dimen.image_corner_radius))
                .build()
    }

    private fun setComment(articleMessage: ArticleMessage, itemView: View) {
        itemView.messageText.text = articleMessage.summary.comment.toString()
    }

    fun updateItem(pair: Pair<Int, Int>) {
        if (pair.second == 1) {
            articleMessageList[pair.first].summary.like += 1
        } else if (pair.second == -1) {
            articleMessageList[pair.first].summary.dislike -= 1
        }
        notifyItemChanged(pair.first)
    }


    private fun setLikeDislike(
        articleMessage: ArticleMessage,
        item: View
    ) {
        val summary = articleMessage.summary
        val averageScore = item.averagelikeText
        averageScore.text = (summary.like - summary.dislike).toString()
        val like = item.like
        val disLike = item.dislike

        if (averageScore.text.toString().toInt() <= summary.like - summary.dislike) {
            like.setOnClickListener {
                summary.like = summary.like + 1
                if (!disLike.isEnabled) {
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
                if (!like.isEnabled) {
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
            articleMessage.content.forEach {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)

                if (it.url.isNullOrEmpty()) {
                    val text = TextView(itemView.context)
                    text.text = it.text
                    text.layoutParams = params
                    itemView.container.addView(text)
                } else {
                    val image = ImageView(itemView.context)
                    setImage(image, it.url)
                    image.layoutParams = params
                    image.adjustViewBounds = true
                    itemView.container.addView(image)
                }
            }
            itemView.container.setHasTransientState(true)
        }

        private fun setImage(image: ImageView, url: String) {
            Glide.with(itemView.context)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background).fitCenter()
                )
                .into(image)
        }
    }
}
