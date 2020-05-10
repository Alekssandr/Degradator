package com.degradators.degradators.ui.account.signup

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
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SignUpFragment : Fragment() {

    private lateinit var navigation: NavController
    private lateinit var binding: FragmentSignUpBinding

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