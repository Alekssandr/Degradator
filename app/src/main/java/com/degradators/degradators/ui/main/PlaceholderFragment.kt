package com.degradators.degradators.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.*
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentHomeBinding
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.detail.DetailActivity
import com.degradators.degradators.ui.home.HomeViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel
    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter
    private val SECOND_ACTIVITY_REQUEST_CODE = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1) }

        homeViewModel.articleMessage.observe(viewLifecycleOwner, Observer<List<ArticleMessage>> {
            bindArticleMessagesAdapter.update(it)
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
                    bindArticleMessagesAdapter.updateItem(Pair(position, like))
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
            layoutManager = LinearLayoutManager(context)
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