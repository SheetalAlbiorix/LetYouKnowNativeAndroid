package com.letyouknow.view.login

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.LoginViewModel
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.forgotpassword.ForgotPasswordActivity
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import com.letyouknow.view.signup.CreateAccountActivity
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
        init()
    }

    private fun init() {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        tvSignUp.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
        ivFingerPrint.setOnClickListener(this)
        ivPasswordInfo.setOnClickListener(this)

        initBiometric()
        setTermsLink()

        onTextChange(this, edtEmailAddress, tvErrorEmailAddress)
        onTextChange(this, edtPassword, tvErrorPassword)
    }

    private fun initBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
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
                startActivity<CreateAccountActivity>()
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
}

