package com.degradators.degradators.ui.account

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.degradators.degradators.R
import com.degradators.degradators.databinding.FragmentAccountBinding
import com.degradators.degradators.databinding.FragmentSignUpBinding
import com.degradators.degradators.ui.account.signup.SignUpViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


class AccountFragment : Fragment() {

    @Inject
    lateinit var accountViewModel: AccountViewModel

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    lateinit var githubAuthURLFull: String
    lateinit var githubdialog: Dialog


    var id = ""
    var displayName = ""
    var email = ""
    var avatar = ""
    var accessToken = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        notificationsViewModel =
//            ViewModelProviders.of(this).get(AccountViewModel::class.java)
        val binding: FragmentAccountBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)

//        val root = inflater.inflate(R.layout.fragment_account, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)

//        val navController = Navigation.findNavController(root)
        binding.button.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.navigation_signup)
//               val navController: NavController = Navigation.findNavController(this.requireActivity(), R.id.nav_host_fragment)
//               navController.navigate(R.id.action_navigation_signin_to_navigation_signup)
        }
        binding.root.sign_in_button.setOnClickListener {
            signIn()
        }

        binding.run {
            this.mainViewModel = accountViewModel
            lifecycleOwner = this@AccountFragment
        }

//        val state = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
//
//        githubAuthURLFull =
//            GithubConstants.AUTHURL + "?client_id=" + GithubConstants.CLIENT_ID + "&scope=" + GithubConstants.SCOPE  + "&state=" + state

//        root.github_login_btn.setOnClickListener {
//            setupGithubWebviewDialog(githubAuthURLFull)
//        }

        return binding.root
    }


    //google btn
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("546237982303-jev6kd0kosqc66vrfrmoke6vqp4mh646.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )

            // Signed in successfully
            val googleId = account?.idToken ?: ""
            Log.i("Google ID", googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)


//            val myIntent = Intent(this, DetailsActivity::class.java)
//            myIntent.putExtra("google_id", googleId)
//            myIntent.putExtra("google_first_name", googleFirstName)
//            myIntent.putExtra("google_last_name", googleLastName)
//            myIntent.putExtra("google_email", googleEmail)
//            myIntent.putExtra("google_profile_pic_url", googleProfilePicURL)
//            myIntent.putExtra("google_id_token", googleIdToken)
//            this.startActivity(myIntent)
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }


    // Show Github login page in a dialog
//    @SuppressLint("SetJavaScriptEnabled")
//    fun setupGithubWebviewDialog(url: String) {
//        githubdialog = Dialog(this.requireContext())
//        val webView = WebView(this.requireContext())
//        webView.isVerticalScrollBarEnabled = false
//        webView.isHorizontalScrollBarEnabled = false
//        webView.webViewClient = GithubWebViewClient()
//        webView.settings.javaScriptEnabled = true
//        webView.loadUrl(url)
//        githubdialog.setContentView(webView)
//        githubdialog.show()
//    }
//
//    // A client to know about WebView navigations
//    // For API 21 and above
//    @Suppress("OverridingDeprecatedMember")
//    inner class GithubWebViewClient : WebViewClient() {
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        override fun shouldOverrideUrlLoading(
//            view: WebView?,
//            request: WebResourceRequest?
//        ): Boolean {
//            if (request!!.url.toString().startsWith(GithubConstants.REDIRECT_URI)) {
//                handleUrl(request.url.toString())
//
//                // Close the dialog after getting the authorization code
//                if (request.url.toString().contains("code=")) {
//                    githubdialog.dismiss()
//                }
//                return true
//            }
//            return false
//        }
//
//        // For API 19 and below
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//            if (url.startsWith(GithubConstants.REDIRECT_URI)) {
//                handleUrl(url)
//
//                // Close the dialog after getting the authorization code
//                if (url.contains("?code=")) {
//                    githubdialog.dismiss()
//                }
//                return true
//            }
//            return false
//        }
//
//        // Check webview url for access token code or error
//        private fun handleUrl(url: String) {
//            val uri = Uri.parse(url)
//            if (url.contains("code")) {
//                val githubCode = uri.getQueryParameter("code") ?: ""
//                requestForAccessToken(githubCode)
//            }
//        }
//    }
//
//
//    fun requestForAccessToken(code: String) {
//        val grantType = "authorization_code"
//
//        val postParams =
//            "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + GithubConstants.REDIRECT_URI + "&client_id=" + GithubConstants.CLIENT_ID + "&client_secret=" + GithubConstants.CLIENT_SECRET
//        GlobalScope.launch(Dispatchers.Default) {
//            val url = URL(GithubConstants.TOKENURL)
//            val httpsURLConnection =
//                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
//            httpsURLConnection.requestMethod = "POST"
//            httpsURLConnection.setRequestProperty(
//                "Accept",
//                "application/json"
//            );
//            httpsURLConnection.doInput = true
//            httpsURLConnection.doOutput = true
//            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
//            withContext(Dispatchers.IO) {
//                outputStreamWriter.write(postParams)
//                outputStreamWriter.flush()
//            }
//            val response = httpsURLConnection.inputStream.bufferedReader()
//                .use { it.readText() }  // defaults to UTF-8
//            withContext(Dispatchers.Main) {
//                val jsonObject = JSONTokener(response).nextValue() as JSONObject
//
//                val accessToken = jsonObject.getString("access_token") //The access token
//
//                // Get user's id, first name, last name, profile pic url
//                fetchGithubUserProfile(accessToken)
//            }
//        }
//    }
//
//    fun fetchGithubUserProfile(token: String) {
//        GlobalScope.launch(Dispatchers.Default) {
//            val tokenURLFull =
//                "https://api.github.com/user"
//
//            val url = URL(tokenURLFull)
//            val httpsURLConnection =
//                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
//            httpsURLConnection.requestMethod = "GET"
//            httpsURLConnection.setRequestProperty("Authorization", "Bearer $token")
//            httpsURLConnection.doInput = true
//            httpsURLConnection.doOutput = false
//            val response = httpsURLConnection.inputStream.bufferedReader()
//                .use { it.readText() }  // defaults to UTF-8
//            val jsonObject = JSONTokener(response).nextValue() as JSONObject
//            Log.i("GitHub Access Token: ", token)
//            accessToken = token
//
//            // GitHub Id
//            val githubId = jsonObject.getInt("id")
//            Log.i("GitHub Id: ", githubId.toString())
//            id = githubId.toString()
//
//            // GitHub Display Name
//            val githubDisplayName = jsonObject.getString("login")
//            Log.i("GitHub Display Name: ", githubDisplayName)
//            displayName = githubDisplayName
//
//            // GitHub Email
//            val githubEmail = jsonObject.getString("email")
//            Log.i("GitHub Email: ", githubEmail)
//            email = githubEmail
//
//            // GitHub Profile Avatar URL
//            val githubAvatarURL = jsonObject.getString("avatar_url")
//            Log.i("Github Avatar URL: ", githubAvatarURL)
//            avatar = githubAvatarURL
//
//            openDetailsActivity()
//        }
//    }
//
//
//    fun openDetailsActivity() {
////        val myIntent = Intent(this, DetailsActivity::class.java)
////        myIntent.putExtra("github_id", id)
////        myIntent.putExtra("github_display_name", displayName)
////        myIntent.putExtra("github_email", email)
////        myIntent.putExtra("github_avatar_url", avatar)
////        myIntent.putExtra("github_access_token", accessToken)
////        startActivity(myIntent)
//    }

//    override fun onConnected(connectionHint: Bundle?) {
////        if (mLoginAction === LoginActivity.Action.LOGOUT) {
////            logOut()
////        } else {
////            silentSignIn()
////        }
//    }
//
//    override fun onConnectionFailed(result: ConnectionResult) {
//        Log.e(TAG, "onConnectionFailed:" + result.errorMessage!!)
//    }
}