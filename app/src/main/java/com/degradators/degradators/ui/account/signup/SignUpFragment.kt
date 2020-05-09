package com.degradators.degradators.ui.account.signup

import ViewModelFactory
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.degradators.degradators.R
import com.degradators.degradators.databinding.FragmentSignUpBinding
import com.degradators.degradators.di.common.viewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class SignUpFragment : Fragment() {

    private lateinit var navigation: NavController
    private lateinit var binding: FragmentSignUpBinding
//
//    @Inject
//    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

//    @Inject
//    lateinit var factory: ViewModelFactory<SignUpViewModel>

//    private val signUpViewModel: SignUpViewModel by viewModel { factory }

//    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    @Inject
    lateinit var signUpViewModel: SignUpViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignUpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
//        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_sign_up)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        root.create.setOnClickListener {
//            signIn()
//        }

        binding.run {
            this.vieWModel = signUpViewModel
            lifecycleOwner = this@SignUpFragment
        }
        setHasOptionsMenu(true)


        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.findNavController().popBackStack()
        return item.onNavDestinationSelected(requireView().findNavController()) || super.onOptionsItemSelected(
            item
        )
    }
}