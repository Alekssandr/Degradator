package com.degradators.degradators.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.degradators.degradators.R
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
import com.degradators.degradators.ui.main.BaseActivity
import com.degradators.degradators.ui.utils.loadImage
import com.degradators.degradators.ui.utils.roundedCorner
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : BaseActivity<ArticleDetailsViewModel>() {

    override val viewModel: ArticleDetailsViewModel by viewModels { factory }

    private val intentBindDetails = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.run {
            this.articleDetailsViewModel = viewModel
            lifecycleOwner = this@DetailActivity
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initRecycler(binding)

        fillData()
    }

    private fun fillData() {
        val articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
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
                val countsComments = Integer.valueOf(messageText.text.toString()) + 1
                setComment(countsComments)
                intentBindDetails.putExtra(COMMENTS, countsComments)
                viewModel.getComment(articleDetails.id)
            }
        }
    }

    private fun initRecycler(binding: ActivityDetailBinding) {
        val articleDetails = intent.extras?.get(DETAILS_EXTRA) as ArticleMessage
        binding.commentsBlock.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = CommentsAdapter {
                if (it.second == 0) {
                    viewModel.putComment(it.first) {
                        viewModel.getComment(articleDetails.id)
                    }
                    val countsComments = Integer.valueOf(messageText.text.toString()) + 1
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

        setLikeDislike(articleDetails)

        viewModel.getComment(articleDetails.id)

        setComment(articleDetails.summary.comment)

        setUser(articleDetails)

        createDetailsArticleView(articleDetails)

        mainContainer.setHasTransientState(true)
    }

    private fun createDetailsArticleView(articleDetails: ArticleMessage) {
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
                image.loadImage(it.url, R.drawable.ic_launcher_background)
                image.layoutParams = params
                image.adjustViewBounds = true
                mainContainer.addView(image)
            }
        }
    }

    private fun setUser(articleMessage: ArticleMessage) {
        detailsUserPhoto.loadImage(articleMessage.userPhoto, R.mipmap.ic_launcher_round)
        detailsUserPhoto.shapeAppearanceModel = detailsUserPhoto.roundedCorner()
        detailsUserName.text = articleMessage.userName
    }

    override fun onSupportNavigateUp(): Boolean {
        val position = intent.getIntExtra(DETAILS_POSITION, 0)
        intentBindDetails.putExtra(DETAILS_POSITION, position)
        setResult(Activity.RESULT_OK, intentBindDetails)
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}