package com.letyouknow.view.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.forgotpassword.ForgotPasswordActivity
import com.letyouknow.view.signup.CreateAccountActivity
import com.pionymessenger.utils.Constant.Companion.makeLinks
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity


class LoginActivity : BaseActivity(), View.OnClickListener {
    val REQUEST_CODE = 101

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvSignUp.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)

        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                Toast.makeText(applicationContext, "Terms of Service Clicked", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(applicationContext, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }

    override fun getViewActivity(): Activity? {
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
                startActivity<MainActivity>()

                val biometricManager = BiometricManager.from(this)
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                        startActivity(Intent(this, CreateAccountActivity::class.java))
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                        Log.e("MY_APP_TAG", "No biometric features available on this device.")
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                        Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        // Prompts the user to create credentials that your app accepts.
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        startActivityForResult(enrollIntent, REQUEST_CODE)
                    }
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {

                    }
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {

                    }
                    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {

                    }
                }
            }
        }
    }
}