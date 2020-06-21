package com.degradators.degradators.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.DETAILS_EXTRA
import com.degradators.degradators.common.adapter.DETAILS_LIKE
import com.degradators.degradators.common.adapter.DETAILS_POSITION
import com.degradators.degradators.databinding.ActivityDetailBinding
import com.degradators.degradators.di.common.ViewModelFactory
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.model.article.Summary
import com.degradators.degradators.model.comment.CommentBlock
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.model.comment.Expanded
import com.degradators.degradators.ui.detail.adapter.CommentsAdapter
import com.degradators.degradators.ui.detail.viewModel.ArticleDetailsViewModel
import com.google.android.material.shape.CornerFamily
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.messageText
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory<ArticleDetailsViewModel>

    val viewmodel: ArticleDetailsViewModel by viewModels { factory }

    private val intentBindWords = Intent()

    private lateinit var commentsAdapter: CommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.run {
            this.articleDetailsViewModel = viewmodel
            lifecycleOwner = this@DetailActivity
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        showArticle()

        initRecycler(binding)

        btnAddComment.setOnClickListener {
            val articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
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

            viewmodel.putComment(comment) // add closure, add request update comments
            setComment(Integer.valueOf(messageText.text.toString())+1)
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000)
                viewmodel.getComment(articleDetails.id)
            }
        }
    }

    private fun initRecycler(binding: ActivityDetailBinding) {
        binding.commentsBlock.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = CommentsAdapter{
                if(it.second == 0){
                    viewmodel.putComment(it.first)
                    setComment(Integer.valueOf(messageText.text.toString())+1)
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
                viewmodel.putLikes(Pair(articleMessage.id, 1))
                intentBindWords.putExtra(DETAILS_LIKE, 1)
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
                viewmodel.putLikes(Pair(articleMessage.id, -1))
                intentBindWords.putExtra(DETAILS_LIKE, -1)
            }
        }
    }

    private fun setComment(commentCount: Int) {
        messageText.text = commentCount.toString()
    }

    private fun showArticle() {
        val articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
        setLikeDislike(articleDetails)
        viewmodel.getComment(articleDetails.id)
        setComment(articleDetails.summary.comment)
        setUser(articleDetails)
        detailsImageTextTitle.text = articleDetails.header
        articleDetails.content.forEach {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 0, 0)

            if (it.url.isNullOrEmpty()) {
                val text = TextView(this)
                text.text = it.text
                text.layoutParams = params
                mainContainer.addView(text)
            } else {
                val image = ImageView(this)
                setImage(image, it.url)
                image.layoutParams = params
                image.adjustViewBounds = true
                mainContainer.addView(image)
            }
        }
        mainContainer.setHasTransientState(true)
    }

    private fun setUser(articleMessage: ArticleMessage) {
        Glide.with(this)
            .load(articleMessage.userPhoto)
            .apply(
                RequestOptions()
                    .placeholder(R.mipmap.ic_launcher_round).fitCenter()
            )
            .into(detailsUserPhoto)
        detailsUserPhoto.shapeAppearanceModel =
            detailsUserPhoto.shapeAppearanceModel
                .toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    detailsUserPhoto.resources.getDimension(R.dimen.image_corner_radius)
                )
                .build()
        detailsUserName.text = articleMessage.userName
    }

    private fun setImage(image: ImageView, url: String) {
        Glide.with(this)
            .load(url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background).fitCenter()
            )
            .into(image)
    }

    override fun onSupportNavigateUp(): Boolean {
        val position = intent.getIntExtra(DETAILS_POSITION, 0)
        intentBindWords.putExtra(DETAILS_POSITION, position)
        setResult(Activity.RESULT_OK, intentBindWords)
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}