package com.degradators.degradators.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.User
import com.degradators.degradators.usecase.AuthUserUseCase
import com.degradators.degradators.usecase.SystemSettingsUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (settingsPreferences.clientId.isEmpty()) getSystemSetting()
    }

    private fun getSystemSetting() {
        disposables += systemSettingsUseCase
            .execute()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.clientId = it
                Log.d("Test111", "systemSettings: $it")
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }
}