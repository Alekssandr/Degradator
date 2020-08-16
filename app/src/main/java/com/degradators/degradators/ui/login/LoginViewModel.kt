package com.degradators.degradators.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import com.degradators.degradators.R
import com.degradators.degradators.common.preferencies.SettingsPreferences
import com.degradators.degradators.di.common.rx.RxSchedulers
import com.degradators.degradators.model.user.User
import com.degradators.degradators.ui.main.BaseViewModel
import com.degradators.degradators.usecase.AuthUserUseCase
import com.degradators.degradators.usecase.InsertNewUserUseCase
import com.degradators.degradators.usecase.SocialSignInUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val insertNewUserUseCase: InsertNewUserUseCase,
    private val socialSignInUseCase: SocialSignInUseCase,
    private val settingsPreferences: SettingsPreferences,
    private val schedulers: RxSchedulers
) : BaseViewModel() {

    private val disposables = CompositeDisposable()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        disposables += authUserUseCase
            .execute(User(mail = username, password = password))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.token = it
                Log.d("Test1112", "token: $it")
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = "sign in success"))
            }, onError = {
                Log.e("Test1112", "error: ${it.message} ?: ")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            })
    }

    fun signUp(username: String, password: String){
        disposables += insertNewUserUseCase
            .execute(User(mail = username, password = password))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onComplete = {
                Log.d("Test1112", "yep!!!")
                login(username, password)
            }, onError = {
                Log.e("Test1112", "error: ${it.message} ?: ")
                _loginResult.value = LoginResult(error = R.string.signup_failed)
            })
    }

    fun socialSignIn(token: String){
        disposables += socialSignInUseCase
            .execute(token)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribeBy(onSuccess = {
                settingsPreferences.token = token
                Log.d("Test11123", "token: $it")
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = "sign in success"))
            }, onError = {
                Log.e("Test11123", "error: ${it.message} ?: ")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            })
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
