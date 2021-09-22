package com.letyouknow.view.forgotpassword

import android.app.Activity
import android.os.Bundle
import android.os.Handler
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
import com.pionymessenger.utils.Constant
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
            Constant.showLoader(this)
            val request = HashMap<String, String>()
            request[ApiConstant.userName] = edtEmail.text.toString()
            request[ApiConstant.email] = edtEmail.text.toString()

            forgotPasswordViewModel.getForgotPassword(this, request)!!
                .observe(this, Observer { forgotVo ->
                    Constant.dismissLoader()
                    Toast.makeText(
                        this,
                        "You will Receive an email with resetting instruction shortly.\nFor security reasons, this link will only remain\nactive for the next hour",
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler().postDelayed({
                        finish()
                    }, 5000)
                }

                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}