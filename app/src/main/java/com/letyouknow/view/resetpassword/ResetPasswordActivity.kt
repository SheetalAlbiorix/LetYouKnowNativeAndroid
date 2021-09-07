package com.letyouknow.view.resetpassword

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityResetPasswordBinding
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        init()
    }

    private fun init() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        btnSave.setOnClickListener(this)
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}