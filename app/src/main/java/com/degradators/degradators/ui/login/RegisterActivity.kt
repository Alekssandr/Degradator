package com.degradators.degradators.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.degradators.degradators.MainActivity
import com.degradators.degradators.R
import com.degradators.degradators.di.common.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

   @Inject
    lateinit var factory: ViewModelFactory<LoginViewModel>

    val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.activity_create_account)
        val usernameCreateAccount = findViewById<EditText>(R.id.username_create_account)
        val passwordCreateAccount = findViewById<EditText>(R.id.password_create_account)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel.loginFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            create_account.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                usernameCreateAccount.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                passwordCreateAccount.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(Activity.RESULT_OK)
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })

        usernameCreateAccount.afterTextChangedAccountCreated {
            loginViewModel.loginDataChanged(
                usernameCreateAccount.text.toString(),
                passwordCreateAccount.text.toString()
            )
        }

        passwordCreateAccount.apply {
            afterTextChangedAccountCreated {
                loginViewModel.loginDataChanged(
                    usernameCreateAccount.text.toString(),
                    passwordCreateAccount.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            usernameCreateAccount.text.toString(),
                            passwordCreateAccount.text.toString()
                        )
                }
                false
            }

            create_account.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.signUp(usernameCreateAccount.text.toString(), passwordCreateAccount.text.toString())
            }
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_LONG).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChangedAccountCreated(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
