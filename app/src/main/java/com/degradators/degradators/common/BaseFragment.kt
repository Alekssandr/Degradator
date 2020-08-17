package com.degradators.degradators.common

import android.content.Context
import androidx.fragment.app.Fragment
import com.degradators.degradators.di.common.ViewModelFactory
import com.degradators.degradators.ui.main.BaseViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

	@Inject
	lateinit var factory: ViewModelFactory<T>

	abstract val viewModel: T

	override fun onAttach(context: Context) {
		super.onAttach(context)
		AndroidSupportInjection.inject(this)
	}
}