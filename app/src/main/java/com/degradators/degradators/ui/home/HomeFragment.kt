package com.degradators.degradators.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.degradators.degradators.R
import com.degradators.degradators.common.lifecircle.observeLifecycleIn
import com.degradators.degradators.databinding.FragmentHomeBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val textView: TextView = binding.root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        binding.run {
            this.homeViewModel = homeViewModel
            lifecycleOwner = this@HomeFragment

        }

        observeLifecycleIn(homeViewModel)

        return binding.root
    }
}