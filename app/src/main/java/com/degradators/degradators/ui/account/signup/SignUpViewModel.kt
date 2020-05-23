package com.degradators.degradators.ui.account.signup

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.degradators.degradators.model.User
import com.degradators.degradators.usecase.InsertNewUserUseCase
import com.degradators.degradators.di.common.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val insertNewUserUseCase: InsertNewUserUseCase,
    private val schedulers: RxSchedulers
) : ViewModel(), LifecycleObserver {

    private val disposables = CompositeDisposable()
    val text: MutableLiveData<String> = MutableLiveData<String>()

    val psw = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    fun test(){
        text.value = "aaaa"
    }

    fun createUser() {
        disposables += insertNewUserUseCase
            .execute(User(email.value!!, psw.value!!))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onComplete = {
                Log.d("Test111", "yep!!!")
                //progressbar
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }
}