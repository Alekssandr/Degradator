package com.degradators.degradators.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.degradators.degradators.R
import com.degradators.degradators.Reports
import com.degradators.degradators.common.adapter.COMMENTS
import com.degradators.degradators.common.adapter.DETAILS_EXTRA
import com.degradators.degradators.common.adapter.DETAILS_LIKE
import com.degradators.degradators.common.adapter.DETAILS_POSITION
import com.degradators.degradators.databinding.ActivityDetailBinding
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.model.article.Summary
import com.degradators.degradators.model.comment.CommentBlock
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.model.comment.Expanded
import com.degradators.degradators.ui.detail.adapter.CommentsAdapter
import com.degradators.degradators.ui.detail.viewModel.ArticleDetailsViewModel
import com.degradators.degradators.ui.main.ArticlesActivity
import com.degradators.degradators.ui.main.BaseActivity
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
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.article_item_image_text.view.*
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*
import kotlinx.android.synthetic.main.video_layout.view.*
import java.util.*


class DetailActivity : ArticlesActivity<ArticleDetailsViewModel>(), PopupMenu.OnMenuItemClickListener {

    override val viewModel: ArticleDetailsViewModel by viewModels { factory }

    private val intentBindDetails = Intent()

    lateinit var videoProgressbar: ProgressBar
    lateinit var imageForeground: ImageView
    lateinit var imageBg: ImageView
    lateinit var videoLayout: ConstraintLayout
    private var videoPlayer: SimpleExoPlayer? = null
    private var frameLayout: ConstraintLayout? = null
    private lateinit var videoSurfaceView: PlayerView
    private var isVideoViewAdded = false
    private var isPlayed = false
    var isVolumeOff = true
    lateinit var binding: ActivityDetailBinding
    lateinit var articleDetails: ArticleMessage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding  =
            DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.run {
            this.articleDetailsViewModel = viewModel
            lifecycleOwner = this@DetailActivity
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initRecycler(binding)

        fillData()
    }

    private var selectedRadioItem = -1

    private fun showReportDialog(context: Context){
        val reports =   Reports.values()

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Submit a Report")
//3
        builder.setSingleChoiceItems(reports.map { context.getString(it.nameResId) }.toTypedArray(), selectedRadioItem
        ) { _, which ->
            //4
            selectedRadioItem = which
        }
//5
        builder.setPositiveButton("Report") { dialog, which ->
            viewModel.hideArticles(articleDetails.id, reports[selectedRadioItem].name)
            Snackbar.make(binding.root,"Thank you for report",Snackbar.LENGTH_LONG).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Close") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
//6
    }


    private fun fillData() {
        articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
        showArticle(articleDetails)

        btnAddComment.setOnClickListener {
            val shortContent = listOf(CommentBlock(answerText.text.toString(), "text"))

            val comment = CommentList(
                id = btnAddComment.id.hashCode().toString(),
                ancestorId = articleDetails.id,
                parentPostId = articleDetails.id,
                content = shortContent,
                shortContent = shortContent,
                summary = Summary(),
                isExpandedItem = Expanded.Default
            )

            viewModel.putComment(comment) {
                var msgTextCounts = 0
                if (messageText != null) {
                    msgTextCounts = Integer.valueOf(messageText.text.toString())
                }
                val countsComments = msgTextCounts + 1
                setComment(countsComments)
                intentBindDetails.putExtra(COMMENTS, countsComments)
                viewModel.getComment(articleDetails.id)
            }
        }

        show_popup.setOnClickListener {
            showPopupMenu(it)
        }

//        hideArticle.setOnClickListener {
//            showReportDialog(it.context)
//        }

    }

    private fun initRecycler(binding: ActivityDetailBinding) {
        articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
        binding.commentsBlock.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = CommentsAdapter(viewModel) {
                if (it.second == 0) {
                    viewModel.putComment(it.first) {
                        viewModel.getComment(articleDetails.id)
                    }
                    val countsComments = Integer.valueOf(binding.messageText.text.toString()) + 1
                    intentBindDetails.putExtra(COMMENTS, countsComments)
                    setComment(countsComments)
                } else {
                    //TODO set like, dislike
                }
            }
        }
    }

    private fun setLikeDislike(
        articleMessage: ArticleMessage
    ) {
        val summary = articleMessage.summary
        val averageScore = detailsAverageLike
        averageScore.text = (summary.like - summary.dislike).toString()
        val like = detailsLike
        val disLike = detailsDislike

        if (averageScore.text.toString().toInt() <= summary.like - summary.dislike) {
            like.setOnClickListener {
                summary.like = summary.like + 1
                if (!disLike.isEnabled) {
                    summary.dislike = summary.dislike - 1
                }
                disLike.isEnabled = true
                like.isEnabled = false
                averageScore.text = (summary.like - summary.dislike).toString()
                viewModel.putLikes(Pair(articleMessage.id, 1))
                intentBindDetails.putExtra(DETAILS_LIKE, 1)
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
                viewModel.putLikes(Pair(articleMessage.id, -1))
                intentBindDetails.putExtra(DETAILS_LIKE, -1)
            }
        }
    }

    private fun setComment(commentCount: Int) {
        messageText.text = commentCount.toString()
    }

    private fun showArticle(articleDetails: ArticleMessage) {
        detailsImageTextTitle.text = articleDetails.header

        val date = Date(articleDetails.time)
        dateOfPost.text = date.getTimeAgo(this)

        setLikeDislike(articleDetails)

        viewModel.getComment(articleDetails.id)

        setComment(articleDetails.summary.comment)

        setUser(articleDetails)

        createDetailsArticleView(articleDetails)

        mainContainer.setHasTransientState(true)
        initVideo(this)
        setVideoClick(articleDetails)
    }

    private fun createDetailsArticleView(articleDetails: ArticleMessage) {
        articleDetails.content.forEach {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 0, 0)
            when {
                it.type == "video" -> {
                    videoLayout =
                        LayoutInflater.from(this)
                            .inflate(
                                R.layout.video_layout,
                                mainContainer,
                                false
                            ) as ConstraintLayout
                    imageBg = videoLayout.image_bg
                    videoProgressbar = videoLayout.video_progressbar
                    imageForeground = videoLayout.image_foreground_start
                    videoLayout.image_bg.loadImage(it.url, R.drawable.ic_launcher_background)
                    mainContainer.addView(videoLayout)
                }
                it.url.isEmpty() -> {
                    val text = TextView(this)
                    text.text = it.text
                    text.layoutParams = params
                    mainContainer.addView(text)
                }
                else -> {
                    val image = ImageView(this)
                    image.loadImage(it.url, R.drawable.ic_launcher_background)
                    image.layoutParams = params
                    image.adjustViewBounds = true
                    mainContainer.addView(image)
                }
            }
        }
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
        videoPlayer?.volume = 0f
//        videoPlayer?.volume = videoPlayer?.volume ?: 1.0f
        videoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        videoSurfaceView.controllerAutoShow = true

        videoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        imageForeground.visibility = View.GONE
                        videoProgressbar?.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        videoProgressbar?.visibility = View.GONE
                        if (!isVideoViewAdded) {
                            addVideoView()
                            imageBg.visibility = View.GONE
                            isPlayed = true
                        }
                    }
                    Player.STATE_ENDED -> {
                        resetVideoView()
                        imageForeground.visibility = View.VISIBLE
                        imageBg.visibility = View.VISIBLE
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

    private fun addVideoView() {
        frameLayout!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = View.VISIBLE
        videoSurfaceView.alpha = 1f
    }

    fun setVideoClick(articleDetails: ArticleMessage) {
        if (this::videoLayout.isInitialized) {
            if (videoLayout.container_video != null) {
                videoLayout.container_video.setOnClickListener {
                    val currentUrl =
                        articleDetails.content.first { it.type == "video" }.url
                    if (isPlaying()) {
                        videoPlayer?.playWhenReady = false
                    } else if (isPaused()) {
                        videoPlayer?.playWhenReady = true
                    } else {
                        playVideo(
                            it,
                            currentUrl
                        )
                    }
                }
            }
        }
    }

    fun isPlaying(): Boolean {
        return videoPlayer!!.playbackState == Player.STATE_READY && videoPlayer!!.playWhenReady
    }

    fun isPaused(): Boolean {
        return videoPlayer!!.playbackState == Player.STATE_READY && !videoPlayer!!.playWhenReady
    }

    fun playVideo(view: View, url: String) {
        resetVideoView()
        frameLayout = view.container_video
        // set the position of the list-item that is to be played
        if (!::videoSurfaceView.isInitialized) {
            return
        }

        videoSurfaceView.player = videoPlayer
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            view.context,
            Util.getUserAgent(view.context, "RecyclerView VideoPlayer")
        )

        val mediaUrl: String? = url
        if (mediaUrl != null) {
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl))
            videoPlayer?.prepare(videoSource)
            videoPlayer?.playWhenReady = true
        }
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            videoProgressbar.visibility = View.INVISIBLE
            videoSurfaceView.visibility = View.INVISIBLE
        }
    }

    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView?.parent as ViewGroup?
        val index = parent?.indexOfChild(videoView)
        if (index != null && index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
        }
    }

    private fun setUser(articleMessage: ArticleMessage) {
        detailsUserPhoto.loadImage(articleMessage.userPhoto, R.mipmap.ic_launcher_round)
        detailsUserPhoto.shapeAppearanceModel = detailsUserPhoto.roundedCorner()
        detailsUserName.text = articleMessage.userName
    }

    override fun onSupportNavigateUp(): Boolean {
        videoPlayer?.stop()
        val position = intent.getIntExtra(DETAILS_POSITION, 0)
        intentBindDetails.putExtra(DETAILS_POSITION, position)
        setResult(Activity.RESULT_OK, intentBindDetails)
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_addition)
        popupMenu.menu.findItem(R.id.hide_article).isVisible = false
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        when (p0?.itemId) {
            R.id.report -> showReportDialog(this)
        }
        return true
    }
}