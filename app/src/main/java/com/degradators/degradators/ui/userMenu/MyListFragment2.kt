package com.degradators.degradators.ui.userMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.degradators.degradators.R
import com.degradators.degradators.databinding.FragmentMyList2Binding
import dagger.android.support.DaggerFragment

/**
 * A placeholder fragment containing a simple view.
 */
class MyListFragment2 : DaggerFragment() {

//    @Inject
//    lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMyList2Binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_list2, container, false)

//        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1) }

//        homeViewModel.articleMessage.observe(viewLifecycleOwner, Observer<List<ArticleMessage>> {
//            bindArticleMessagesAdapter.update(it)
//            isLoading = false
//        })

//        observeLifecycleIn(homeViewModel)

        return binding.root
    }
}