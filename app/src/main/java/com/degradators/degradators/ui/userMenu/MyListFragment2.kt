package com.degradators.degradators.ui.userMenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.ArticleMessagesAdapter
import com.degradators.degradators.common.adapter.DETAILS_EXTRA
import com.degradators.degradators.common.adapter.DETAILS_POSITION
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentMyList2Binding
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.detail.DetailActivity
import com.degradators.degradators.ui.home.HomeViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */

//TODO maybe we need to create new vm?
//think how to save list from user likes
class MyListFragment2 : DaggerFragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel
    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter
    lateinit var layoutManagerRW : LinearLayoutManager
    private val SECOND_ACTIVITY_REQUEST_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMyList2Binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_list2, container, false)

        homeViewModel.articleMessage.observe(viewLifecycleOwner, Observer<List<ArticleMessage>> {
            bindArticleMessagesAdapter.update(it)
        })

        observeLifecycleIn(homeViewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        recycler_articles.apply {
            layoutManagerRW = LinearLayoutManager(context)
            layoutManager = layoutManagerRW
            bindArticleMessagesAdapter = ArticleMessagesAdapter {
                startActivityForResult(Intent(activity, DetailActivity::class.java).apply {
                    putExtra(DETAILS_EXTRA, it.first)
                    putExtra(DETAILS_POSITION, it.second)
                }, SECOND_ACTIVITY_REQUEST_CODE)
            }
            adapter = bindArticleMessagesAdapter
            homeViewModel.subscribeForItemClick(bindArticleMessagesAdapter.getClickItemObserver())
        }
    }
}