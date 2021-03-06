package com.degradators.degradators.common.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.utils.getTimeAgo
import com.degradators.degradators.ui.utils.loadImage
import com.degradators.degradators.ui.utils.roundedCorner
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.article_item_image_text.view.*
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*
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
    lateinit var listenerUpdateCurrentItem: (Int) -> Unit
    lateinit var listenerLastCurrentItem: (Int) -> Unit

    private var articleMessageList = mutableListOf<ArticleMessage>()
    private val publishSubjectItem = PublishSubject.create<Pair<String, Int>>()
    private var videoPlayer: SimpleExoPlayer? = null
    private lateinit var videoSurfaceView: PlayerView
    private var progressBar: ProgressBar? = null
    private var isVideoViewAdded = false
    private var frameLayout: FrameLayout? = null
    private lateinit var imageForeground: ImageView
    private lateinit var imageBG: ImageView
    private var isPlayed = false
    private var lastUrl = ""
    private var currentPosition = 0
    var lastView : View? = null
    var isVolumeOff = true


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        initVideo(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_item_image_text, parent, false)
        )
    }

    fun getlistenerLastItemPosition(listenerLastCurrentItem: (Int) -> Unit) {
        this.listenerLastCurrentItem = listenerLastCurrentItem
    }

    fun getlistenerRemoveItem(listenerRemoveItem: (String) -> Unit) {
        this.listenerRemoveItem = listenerRemoveItem
    }

    fun getlistenerUpdateItemPosition(listenerUpdateCurrentItem: (Int) -> Unit) {
        this.listenerUpdateCurrentItem = listenerUpdateCurrentItem
    }

    override fun getItemCount(): Int {
        return articleMessageList.size
    }

    fun getClickItemObserver(): Observable<Pair<String, Int>> {
        return publishSubjectItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = (holder as ImageViewHolder)
        holder.itemView.setOnFocusChangeListener { focusedView, hasFocus ->
            val a = hasFocus
        }

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

        if (item.itemView.container_video != null) {
            currentPosition = position
            item.itemView.container_video.setOnClickListener {
                listenerUpdateCurrentItem(position)
                val currentUrl =
                    articleMessageList[position].content.first { it.type == "video" }.url
                if (currentUrl != lastUrl) {
                    lastView?.let{
                        it.image_bg.loadImage(lastUrl, R.drawable.ic_launcher_background)
                    }
                    videoSurfaceView.player?.stop(true)
                }
                if (isPlaying()) {
                    videoPlayer?.playWhenReady = false
                } else if (isPaused()) {
                    videoPlayer?.playWhenReady = true
                } else {
                    lastView.let {
                        if (this::imageBG.isInitialized) {
                            imageBG.visibility = View.VISIBLE
                            imageForeground.visibility = View.VISIBLE
                        }
                    }
                    lastView = it
                    volumeMute()
                    playVideo(
                        it,
                        currentUrl
                    )
                }
                if (isPlaying()) {
                    isVideoViewAdded = false
                }
            }
        }
        removeArticleBy(position, item)
    }

    fun stopPlayer(view: View?, position: Int) {
        view?.let {
            lastView.let {
                videoPlayer?.playWhenReady = false
            }
            if (view.container_video != null) {
                lastView.let {
                    if (this::imageBG.isInitialized) {
                        imageBG.visibility = View.VISIBLE
                        imageForeground.visibility = View.VISIBLE
                    }
                }
                lastView = view
                volumeMute()

                playVideo(
                    view.container_video,
                    articleMessageList[position].content.first { it.type == "video" }.url
                )
                currentPosition = position
            }
        }
    }

    private fun volumeMute() {
        if (isVolumeOff) {
            videoPlayer?.volume = 0f
        } else {
            videoPlayer?.volume = 1f
        }
    }

    fun isPlaying(): Boolean {
        return videoPlayer!!.playbackState == Player.STATE_READY && videoPlayer!!.playWhenReady
    }
    fun isPaused(): Boolean {
        return videoPlayer!!.playbackState == Player.STATE_READY && !videoPlayer!!.playWhenReady
    }

    fun pausePlayer() {
        videoPlayer?.stop()
    }

    private fun initVideo(context: Context) {
        videoSurfaceView = PlayerView(context)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        videoSurfaceView.showController()
        // 2. Create the player
        videoPlayer = SimpleExoPlayer.Builder(context).build()
        // Bind the player to the view.
        videoSurfaceView.useController = true
        addVolumeController(context)
        videoSurfaceView.player = videoPlayer
        videoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        videoSurfaceView.controllerAutoShow = true
        videoSurfaceView.setKeepContentOnPlayerReset(true)
        videoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        Log.d("test", "STATE_BUFFERING")
                        imageForeground.visibility = View.GONE
                        progressBar?.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        Log.d("test", "STATE_READY")

                        progressBar?.visibility = View.GONE
                        if (!isVideoViewAdded) {
                            addVideoView()
                            imageBG.visibility = View.GONE
                            isPlayed = true
                        }
                    }
                    Player.STATE_ENDED -> {
                        Log.d("test", "end")
                        resetVideoView()
                        imageForeground.visibility = View.VISIBLE
                        imageBG.visibility = View.VISIBLE
                        isPlayed = false
                    }
                    Player.STATE_IDLE -> {
                        val a = 0
                    }
                    else -> {
                        imageForeground.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun addVolumeController(context: Context) {
        videoSurfaceView.volumeController.setOnClickListener {
            if (isVolumeOff) {
                isVolumeOff = false
                videoSurfaceView.volumeController.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_volume_up_white_36dp
                    )
                )
                videoPlayer?.volume = 1f
            } else {
                isVolumeOff = true
                videoSurfaceView.volumeController.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_volume_off_white_36dp
                    )
                )
                videoPlayer?.volume = 0f
            }
        }
    }

    private fun addVideoView() {
        frameLayout!!.layoutParams.height = imageBG.height
        frameLayout!!.layoutParams.width = imageBG.width
        frameLayout!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = View.VISIBLE
        videoSurfaceView.alpha = 1f
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

    fun playVideo(view: View, url: String) {
        resetVideoView()
        lastUrl = url
        progressBar = view.video_progressbar
        imageForeground = view.image_foreground_start
        imageBG = view.image_bg
        frameLayout= view.video_frame
        // set the position of the list-item that is to be played
        if (!::videoSurfaceView.isInitialized) {
            return
        }

        videoSurfaceView.player = videoPlayer

        var dataSourceFactory: DataSource.Factory? =
            DefaultDataSourceFactory(
                view.context,
                Util.getUserAgent(view.context, "AppName")
            )
        dataSourceFactory =
            CacheDataSourceFactory(VideoCacheSingleton.getInstance(view.context), dataSourceFactory)

        val mediaUrl: String? = url
        if (mediaUrl != null) {
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl))
            videoPlayer?.prepare(videoSource)
            videoPlayer?.playWhenReady = true
        }

    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView?.parent as ViewGroup?
        val index = parent?.indexOfChild(videoView)
        if (index != null && index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
        }
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            progressBar?.visibility = View.INVISIBLE
            videoSurfaceView.visibility = View.INVISIBLE
        }
    }

    fun update(items: List<ArticleMessage>, isInitialList: Boolean) {
        if (isInitialList) this.articleMessageList.clear()
        this.articleMessageList.addAll(items.toMutableList())
        notifyDataSetChanged()
    }


    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var videoProgressbar: ProgressBar
        lateinit var imageForeground: ImageView
        lateinit var imageBg: ImageView

        fun bind(articleMessage: ArticleMessage) {
            if (articleMessage.header.isNotEmpty()) {
                itemView.imageTextTitle.text = articleMessage.header
            } else {
                itemView.imageTextTitle.visibility = View.GONE
            }

            Log.d("tes1121", "bind")
            articleMessage.content.forEach {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)
                if (it.type == "video") {
                    val videoLayout =
                        LayoutInflater.from(itemView.container.context)
                            .inflate(R.layout.video_layout, itemView.container, false)
                    imageBg = videoLayout.image_bg
                    videoProgressbar = videoLayout.video_progressbar
                    imageForeground = videoLayout.image_foreground_start

                    setImage(videoLayout.image_bg, it.url)
                    itemView.container.setHasTransientState(true)
                    itemView.container.addView(videoLayout)
                } else if (it.url.isEmpty()) {
                    val text = TextView(itemView.context)
                    text.text = it.text
                    text.layoutParams = params
                    itemView.container.setHasTransientState(true)
                    itemView.container.addView(text)
                } else {
                    val image = ImageView(itemView.context)
                    setImage(image, it.url)
                    image.layoutParams = params
                    image.adjustViewBounds = true
                                itemView.container.setHasTransientState(true)
                    itemView.container.addView(image)
                }
            }

        }

        private fun setImage(image: ImageView, url: String) {
            image.loadImage(url, R.drawable.ic_launcher_background)
        }
    }
}
