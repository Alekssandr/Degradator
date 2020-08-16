package com.degradators.degradators.ui.userMenu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.user.UserInfoUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MyListViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : BaseViewModel() {
    private val disposables = CompositeDisposable()
    val userPhotoLoginVisibility = MutableLiveData<Boolean>()
    val userSignedIn = MutableLiveData<Boolean>()


    val text: MutableLiveData<String> = MutableLiveData<String>()

    init {
        isLogin()
    }

    fun isLogin() {
        if (settingsPreferences.token.isNotEmpty()) {
            getUser()
        } else {
            userPhotoLoginVisibility.value = false
            userSignedIn.value = false
        }
    }

    fun logout() {
        settingsPreferences.token = ""
        userPhotoLoginVisibility.value = false
        userSignedIn.value = false
    }

    private fun getUser() {
        disposables += userInfoUseCase
            .execute(settingsPreferences.token)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.clientId = it.id
                userPhotoLoginVisibility.value = true
                text.value = it.mail
                userSignedIn.value = true
                Log.d("Test11123", "token: $it")
            }, onError = {
                Log.e("Test11123", "error: ${it.message} ?: ")
            })
    }

}