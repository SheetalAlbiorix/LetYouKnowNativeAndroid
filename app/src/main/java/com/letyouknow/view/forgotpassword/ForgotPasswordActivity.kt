package com.letyouknow.view.forgotpassword

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityForgotPasswordBinding
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ForgotPasswordViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.setEmojiKeyBoard
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        init()
    }

    private fun init() {
        forgotPasswordViewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        btnSubmit.setOnClickListener(this)
        setEmojiKeyBoard(edtEmail)
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> {
                callForgotPasswordAPI()

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callForgotPasswordAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, String>()
            request[ApiConstant.userName] = edtEmail.text.toString()
            request[ApiConstant.email] = edtEmail.text.toString()

            forgotPasswordViewModel.getForgotPassword(this, request)!!
                .observe(this, Observer { forgotVo ->
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        this,
                        getString(R.string.forgot_password_email)
                    )
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}