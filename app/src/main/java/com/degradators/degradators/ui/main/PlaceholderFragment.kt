package com.degradators.degradators.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.BR
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.ArticleMessagesAdapter
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentHomeBinding
import com.degradators.degradators.model.ArticleMessage
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
    private lateinit var  binding: FragmentHomeBinding
    private lateinit var bindArticleMessagesAdapter: ArticleMessagesAdapter

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
//        val textView: TextView = binding.root.findViewById(R.id.text_home)
        val recyclerAticles: RecyclerView = binding.root.findViewById(R.id.recycler_articles)
        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)  }
//        homeViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
//        })
        homeViewModel.articleMessage .observe(viewLifecycleOwner, Observer<List<ArticleMessage>> {
            bindArticleMessagesAdapter.update(it)
        })
        observeLifecycleIn(homeViewModel)
//        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)  }
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//        binding.let {
//            binding.homeViewModel = homeViewModel
//            it.lifecycleOwner = this
//        }
//
//
//
//        val textView: TextView = binding.root.findViewById(R.id.text_home)
////        this.homeViewModel = homeViewModel
//        homeViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
//        })
////        initRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
//        observeLifecycleIn(homeViewModel)
//
//        binding.run {
//            binding.lifecycleOwner = this@PlaceholderFragment
//            binding.homeViewModel = homeViewModel
//            initRecycler()
//        }
    }

    private fun initRecycler() {
//        val recyclerPairRus = binding.recyclerArticles
        recycler_articles.apply {
            layoutManager =  LinearLayoutManager(context)
            bindArticleMessagesAdapter = ArticleMessagesAdapter()
            adapter  = bindArticleMessagesAdapter
        }
//        val layoutManager = LinearLayoutManager(context)
//        val recyclerArticles = binding.recyclerArticles
//        recyclerArticles.layoutManager = layoutManager
//        recyclerArticles.hasFixedSize()
//        val adapter  = ArticleMessagesAdapter()
//        recyclerArticles.adapter = adapter

//        binding.recyclerArticles.apply {
//            layoutManager =
//                GridLayoutManager(this.context, 2, RecyclerView.VERTICAL, true)
//
////            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            bindArticleMessagesAdapter = ArticleMessagesAdapter()
//            this.adapter = bindArticleMessagesAdapter
////            wordsWritingViewModel.subscribeForRusItemClick(bindWordsWritingAdapter.getClickItemObserver())
//        }
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