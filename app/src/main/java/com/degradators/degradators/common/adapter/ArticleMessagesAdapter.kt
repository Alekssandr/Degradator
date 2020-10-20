package com.degradators.degradators.common.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.model.article.ArticleBlock
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.utils.getTimeAgo
import com.degradators.degradators.ui.utils.loadImage
import com.degradators.degradators.ui.utils.roundedCorner
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadHelper.createMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_video_player.view.*
import kotlinx.android.synthetic.main.article_item_image_text.view.*
import kotlinx.android.synthetic.main.video_layout.view.*
import java.util.*


const val DETAILS_EXTRA = "details"
const val VIDEO_URL = "url"
const val DETAILS_POSITION = "details_position"
const val DETAILS_LIKE = "details_like"
const val COMMENTS = "comments"

class ArticleMessagesAdapter(val listenerOpenDetail: (Pair<ArticleMessage, Int>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listenerRemoveItem: (String) -> Unit

    private var articleMessageList = mutableListOf<ArticleMessage>()
    private val publishSubjectItem = PublishSubject.create<Pair<String, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_item_image_text, parent, false)
        )
    }

    fun getlistenerRemoveItem(listenerRemoveItem: (String) -> Unit) {
        this.listenerRemoveItem = listenerRemoveItem
    }

    override fun getItemCount(): Int {
        return articleMessageList.size
    }

    fun getClickItemObserver(): Observable<Pair<String, Int>> {
        return publishSubjectItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = (holder as ImageViewHolder)
        Log.d("test1111", "size: " + articleMessageList.size.toString() + " pos: " + position)
        item.bind(articleMessageList[position])
        setLikeDislike(articleMessageList[position], holder.itemView)
        setComment(articleMessageList[position], holder.itemView)
        setUser(articleMessageList[position], holder.itemView)
        val date = Date(articleMessageList[position].time)
        holder.itemView.dateOfPost.text = date.getTimeAgo(context = holder.itemView.context)

        item.itemView.message.setOnClickListener {
            val article = articleMessageList[position]
            val articleDetails =
                ArticleMessage(
                    id = article.id,
                    time = article.time,
                    type = article.type,
                    summary = article.summary,
                    header = article.header,
                    content = article.content,
                    userPhoto = article.userPhoto,
                    userName = article.userName
                )
            listenerOpenDetail(Pair(articleDetails, position))
        }

        removeArticleBy(position, item)
    }

    private fun removeArticleBy(
        position: Int,
        item: ImageViewHolder
    ) {
        if (articleMessageList[position].isRemovable) {
            item.itemView.removeArticle.visibility = View.VISIBLE
            item.itemView.removeArticle.setOnClickListener {
                listenerRemoveItem(articleMessageList[position].id)
            }
        }
    }

    private fun setUser(articleMessage: ArticleMessage, itemView: View) {
        itemView.userPhoto.loadImage(articleMessage.userPhoto, R.mipmap.ic_launcher_round)
        itemView.userPhoto.shapeAppearanceModel = itemView.userPhoto.roundedCorner()
        itemView.userName.text = articleMessage.userName
    }

    private fun setComment(articleMessage: ArticleMessage, itemView: View) {
        itemView.messageText.text = articleMessage.summary.comment.toString()
    }

    fun updateItem(pair: Triple<Int, Int, Int>) {
        if (pair.second == 1) {
            articleMessageList[pair.first].summary.like += 1
        } else if (pair.second == -1) {
            articleMessageList[pair.first].summary.dislike -= 1
        }
        if (pair.third != -1) {
            articleMessageList[pair.first].summary.comment = pair.third
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

    fun update(items: List<ArticleMessage>, isInitialList: Boolean) {
        if (isInitialList) this.articleMessageList.clear()
        this.articleMessageList.addAll(items.toMutableList())
        notifyDataSetChanged()
    }


    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var videoSurfaceView: PlayerView

        fun bind(articleMessage: ArticleMessage) {
            if (articleMessage.header.isNotEmpty()) {
                itemView.imageTextTitle.text = articleMessage.header
            } else {
                itemView.imageTextTitle.visibility = View.GONE
            }

            articleMessage.content.forEach {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)
                when {
                    it.type == "video" -> {
                        setVideo(it)
                    }
                    it.url.isEmpty() -> {
                        val text = TextView(itemView.context)
                        text.text = it.text
                        text.layoutParams = params
                        itemView.container.addView(text)
                    }
                    else -> {
                        val image = ImageView(itemView.context)
                        setImage(image, it.url)
                        image.layoutParams = params
                        image.adjustViewBounds = true
                        itemView.container.addView(image)
                    }
                }
            }
            itemView.container.setHasTransientState(true)
        }

        private fun setVideo(it: ArticleBlock) {
            val videoLayout =
                LayoutInflater.from(itemView.container.context)
                    .inflate(R.layout.video_layout, itemView.container, false)
            videoSurfaceView = videoLayout.player_view
            val player = SimpleExoPlayer.Builder(itemView.context).build()

            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                itemView.context,
                Util.getUserAgent(itemView.context, "RecyclerView VideoPlayer")
            )
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(it.url))
            player.prepare(videoSource)
            player.seekTo(1)
            videoSurfaceView.player = player

            itemView.container.addView(videoLayout)
        }

        private fun setImage(image: ImageView, url: String) {
            image.loadImage(url, R.drawable.ic_launcher_background)
        }
    }
}
