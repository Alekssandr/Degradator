package com.degradators.degradators.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.*
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentHomeBinding
import com.degradators.degradators.ui.detail.DetailActivity
import com.degradators.degradators.ui.home.HomeViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : DaggerFragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel
    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    private var isLoading: Boolean = false
    lateinit var layoutManagerRW : LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1) }

        homeViewModel.articleMessage.observe(viewLifecycleOwner, Observer {
            bindArticleMessagesAdapter.update(it.first, it.second)
            isLoading = false
        })

        observeLifecycleIn(homeViewModel)

        return binding.root
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
            }.also {
                it.getlistenerRemoveItem { messageId ->
                    homeViewModel.removeArticles(messageId)
                }
                it.getlistenerUpdateItemPosition {position ->
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 20)
                }
                recycler_articles.setOnFocusChangeListener { view, b ->
                    val a = b

                }
                recycler_articles .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        Log.d("test111", "dx: " + dx + "  |  dy:" + dy)
                        var scroll = dy
                        if(scroll<0) {
                            scroll *= -1
                        }
                        if(scroll>50){
                            bindArticleMessagesAdapter.stopPlayer((layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
                        }
                    }
                })
            }
            adapter = bindArticleMessagesAdapter
            homeViewModel.subscribeForItemClick(bindArticleMessagesAdapter.getClickItemObserver())
            addScrollerListener()
        }
    }

    private fun addScrollerListener() {
        recycler_articles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (layoutManagerRW.findLastVisibleItemPosition() == layoutManagerRW.itemCount - 1) {
                        homeViewModel.getArticles(layoutManagerRW.itemCount.toLong())
                        isLoading = true
                    }
                }
            }
        })
    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}