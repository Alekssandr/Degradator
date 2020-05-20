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
import com.degradators.degradators.R
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentHomeBinding
import com.degradators.degradators.ui.home.HomeViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
//            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val textView: TextView = binding.root.findViewById(R.id.text_home)
        homeViewModel.apply { setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)  }
        homeViewModel.text.observe(viewLifecycleOwner, Observer<String> {
            textView.text = it
        })
        observeLifecycleIn(homeViewModel)
        return binding.root
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