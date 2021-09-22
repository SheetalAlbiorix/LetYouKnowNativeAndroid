package com.letyouknow.view.account.editlogin

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditLoginBinding
import com.letyouknow.model.LoginData
import kotlinx.android.synthetic.main.layout_toolbar.*

class EditLoginActivity : BaseActivity() {
    private lateinit var binding: ActivityEditLoginBinding
    private lateinit var userData: LoginData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_login)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_edit_login)
        init()
    }

    private fun init() {
        userData = pref?.getUserData()!!
        binding.loginData = userData
        backButton()
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}