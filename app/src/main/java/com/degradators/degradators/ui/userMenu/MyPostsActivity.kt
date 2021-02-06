package com.degradators.degradators.ui.userMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.*
import com.degradators.degradators.databinding.ActivityMyPostsBinding
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.detail.DetailActivity
import com.degradators.degradators.ui.main.ArticlesActivity
import com.degradators.degradators.ui.main.BaseActivity
import kotlinx.android.synthetic.main.fragment_home.*

class MyPostsActivity : ArticlesActivity<MySubmissionsViewModel>() {

    override val viewModel: MySubmissionsViewModel by viewModels { factory }
    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter
    lateinit var layoutManagerRW : LinearLayoutManager
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    lateinit var binding: ActivityMyPostsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_posts)
        binding.apply {
                mySubmissionsViewModel = viewModel.apply { this.getSubmissionsArticles("POST") }
        }
        binding.lifecycleOwner = this@MyPostsActivity


        viewModel.articleMessage.observe(this, Observer<List<ArticleMessage>> {
            bindArticleMessagesAdapter.update(it, true)
        })

        initRecycler()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


    }

    override fun onSupportNavigateUp(): Boolean {
        bindArticleMessagesAdapter.pausePlayer()
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    var oldPos = -2

    private fun initRecycler() {
        recycler_articles.apply {
            layoutManagerRW = LinearLayoutManager(context)
            layoutManager = layoutManagerRW
            bindArticleMessagesAdapter = ArticleMessagesAdapter(viewModel) {
                startActivityForResult(Intent(this@MyPostsActivity, DetailActivity::class.java).apply {
                    putExtra(DETAILS_EXTRA, it.first)
                    putExtra(DETAILS_POSITION, it.second)
                }, SECOND_ACTIVITY_REQUEST_CODE)
            }.also {
                it.getlistenerRemoveItem { messageId ->
//                    homeViewModel.removeArticles(messageId)
                }
                it.getlistenerUpdateItemPosition {position ->
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 20)
                }
                recycler_articles.setOnFocusChangeListener { view, b ->
                    val a = b

                }
                recycler_articles .addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
//                        if (newState === RecyclerView.SCROLL_STATE_DRAGGING) {
                        val position: Int =
                            (recycler_articles.getLayoutManager() as LinearLayoutManager)
                                .findFirstVisibleItemPosition()
                        if(oldPos != position){
                            oldPos = position
                            bindArticleMessagesAdapter.stopPlayer((layoutManager as LinearLayoutManager).findViewByPosition(position), position)

                        }
//                        }
                    }
                })

            }
            adapter = bindArticleMessagesAdapter
//            viewModel.subscribeForItemClick(bindArticleMessagesAdapter.getClickItemObserver())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val position = it.getIntExtra(DETAILS_POSITION, 0)
                    val like = it.getIntExtra(DETAILS_LIKE, 0)
                    val commentsCount = it.getIntExtra(COMMENTS, -1)
                    bindArticleMessagesAdapter.updateItem(Triple(position, like, commentsCount))
                }
            }
        }
    }
}

