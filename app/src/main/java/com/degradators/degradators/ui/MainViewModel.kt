package com.degradators.degradators.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.SystemSettingsUseCase
import com.degradators.degradators.usecase.user.UserInfoUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val systemSettingsUseCase: SystemSettingsUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : BaseViewModel() {
    private val disposables = CompositeDisposable()
    val userPhotoLoginVisibility = MutableLiveData<Boolean>()
    val userSignedIn = MutableLiveData<Boolean>()
    val userName: MutableLiveData<String> = MutableLiveData<String>()
    val userUrl: MutableLiveData<String> = MutableLiveData<String>()
    private var _messageIds = MutableLiveData<List<String>>()
    val messageIds: LiveData<List<String>>
        get() = _messageIds

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
        settingsPreferences.clientId = ""
        settingsPreferences.userId = ""
        userPhotoLoginVisibility.value = false
        getSystemSetting()
    }

    private fun getUser() {
        disposables += userInfoUseCase
            .execute(settingsPreferences.token)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                val flattened: List<String> =
                    it.markList.flatMap { list -> mutableListOf(list.messageId) }
                _messageIds.value = flattened
                settingsPreferences.userId = it.id
                userPhotoLoginVisibility.value = true
                userUrl.value = it.photo
                userName.value = if (it.username.isNullOrEmpty()) it.mail else it.username
                userSignedIn.value = true
                Log.d("Test11123", "token: $it")
            }, onError = {
                Log.e("Test11123", "error: ${it.message} ?: ")
            })
    }

    private fun getSystemSetting() {
        disposables += systemSettingsUseCase
            .execute()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.clientId = it
                userSignedIn.value = false
            }, onError = {
                Log.e("Test111", "error: ${it.message} ?: ")
            })
    }

}