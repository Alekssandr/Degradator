package com.degradators.degradators.ui.account

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.User
import com.degradators.degradators.usecase.AuthUserUseCase
import com.degradators.degradators.usecase.InsertNewUserUseCase
import com.google.android.gms.tasks.OnSuccessListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val disposables = CompositeDisposable()

    val psw = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    fun signIn() {
        disposables += authUserUseCase
            .execute(User(email.value!!, psw.value!!))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.token = it
                Log.d("Test111", "token: $it")
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")

            })
    }
}