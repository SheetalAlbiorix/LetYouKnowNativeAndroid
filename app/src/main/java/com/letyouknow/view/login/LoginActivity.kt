package com.letyouknow.view.login

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.LoginViewModel
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.forgotpassword.ForgotPasswordActivity
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import com.letyouknow.view.signup.SignUpActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_POLICY
import com.pionymessenger.utils.Constant.Companion.PRIVACY_POLICY_LINK
import com.pionymessenger.utils.Constant.Companion.TERMS_CONDITIONS_LINK
import com.pionymessenger.utils.Constant.Companion.emailValidator
import com.pionymessenger.utils.Constant.Companion.makeLinks
import com.pionymessenger.utils.Constant.Companion.onTextChange
import com.pionymessenger.utils.Constant.Companion.passwordValidator
import com.pionymessenger.utils.Constant.Companion.setErrorBorder
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.Executor


class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    lateinit var loginViewModel: LoginViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FacebookSdk.sdkInitialize(this)
        init()
    }

    private fun init() {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        tvSignUp.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
        ivFingerPrint.setOnClickListener(this)
        ivPasswordInfo.setOnClickListener(this)
        ivGoogle.setOnClickListener(this)

        initBiometric()
        setTermsLink()

        //Google Sign In
        firebaseAuth()
        googleInit()
        facebookInit()
        onTextChange(this, edtEmailAddress, tvErrorEmailAddress)
        onTextChange(this, edtPassword, tvErrorPassword)
        hashKey()
    }

    private fun hashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.letyouknow",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }

    private fun initBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    try {
                        startActivity(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
                    } catch (e: Exception) {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for " + getString(R.string.app_name))
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

    }

    private fun setTermsLink() {
        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(ARG_POLICY to TERMS_CONDITIONS_LINK)
            }),
            Pair("Privacy Policy", View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(ARG_POLICY to PRIVACY_POLICY_LINK)
            })
        )
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSignUp -> {
                startActivity<SignUpActivity>()
            }
            R.id.txtForgotPassword -> {
                startActivity<ForgotPasswordActivity>()
            }
            R.id.btnSignIn -> {
                tvErrorEmailAddress.visibility = View.GONE
                tvErrorPassword.visibility = View.GONE

                if (isValid()) {
                    if (Constant.isOnline(this)) {
                        Constant.showLoader(this)
                        val request = HashMap<String, String>()
                        request[ApiConstant.username] = edtEmailAddress.text.toString()
                        request[ApiConstant.password] = edtPassword.text.toString()

                        loginViewModel.getUser(this, request)!!.observe(this, Observer { loginVo ->
                            Constant.dismissLoader()
                            if (loginVo.buyerId != 0) {
                                startActivity<MainActivity>()
                                finish()
                            } else {
                                Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        )
                    } else {
                        Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.ivFingerPrint -> {
                biometricPrompt.authenticate(promptInfo)
            }
            R.id.ivPasswordInfo -> {
                popupPassword()
            }
            R.id.ivGoogle -> {
                googleSignIn()
            }

        }
    }


    private fun isValid(): Boolean {
        when {
            TextUtils.isEmpty(edtEmailAddress.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_email_address_vali)
                setErrorBorder(edtEmailAddress, tvErrorEmailAddress)
                return false
            }
            !emailValidator(edtEmailAddress.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_valid_email)
                setErrorBorder(edtEmailAddress, tvErrorEmailAddress)
                return false
            }
            TextUtils.isEmpty(edtPassword.text.toString().trim()) -> {
                tvErrorPassword.text = getString(R.string.enter_password)
                setErrorBorder(edtPassword, tvErrorPassword)
                return false
            }
            !passwordValidator(edtPassword.text.toString().trim()) -> {
                tvErrorPassword.text = getString(R.string.enter_valid_password)
                setErrorBorder(edtPassword, tvErrorPassword)
                return false
            }
            else -> return true
        }
    }

    private fun popupPassword() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_password_hint)
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    //Google

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private val GOOGLE_SIGN_IN = 101

    private fun firebaseAuth() {
        auth = FirebaseAuth.getInstance();
    }

    private fun googleInit() {
        firebaseAuth()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id)).requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    //Facebook
    private fun facebookInit() {
        ivFacebook.setBackgroundResource(R.drawable.ic_facebook)
        ivFacebook.text = ""
        ivFacebook.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        callbackManager = CallbackManager.Factory.create()
        ivFacebook.setReadPermissions("email", "public_profile")

        ivFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_SIGN_IN) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }


        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e("UserName : ", user?.displayName!!)
                    Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT)
                        .show()
                    // updateUI(user)
                    googleSignOut()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //  updateUI(null)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e("UserName FB : ", user?.displayName!!)
                    Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT)
                        .show()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
        facebookSignOut()
    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut()
        auth.signOut()
    }

    private fun facebookSignOut() {
        LoginManager.getInstance().logOut()
        auth.signOut()
    }
}


