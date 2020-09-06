package com.degradators.degradators.ui.userMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.degradators.degradators.MESSAGEIDS
import com.degradators.degradators.common.BaseFragment
import com.degradators.degradators.common.adapter.*
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentMyListBinding
import com.degradators.degradators.databinding.FragmentMySubmissionsBinding
import com.degradators.degradators.model.article.ArticleMessage
import com.degradators.degradators.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.fragment_home.*


class MySubmissionsFragment : BaseFragment<MySubmissionsViewModel>() {

    override val viewModel: MySubmissionsViewModel by viewModels { factory }

    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter
    lateinit var layoutManagerRW : LinearLayoutManager
    private val SECOND_ACTIVITY_REQUEST_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMySubmissionsBinding =
            FragmentMySubmissionsBinding.inflate(inflater, container, false).apply {
                mySubmissionsViewModel = viewModel
            }
        viewModel.articleMessage.observe(viewLifecycleOwner, Observer<List<ArticleMessage>> {
            bindArticleMessagesAdapter.update(it, true)
        })

        observeLifecycleIn(viewModel)

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