package com.letyouknow.view.account.viewDollar

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityViewDollarBinding
import com.letyouknow.view.dashboard.MainActivity
import kotlinx.android.synthetic.main.activity_view_dollar.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class ViewDollarActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityViewDollarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_dollar)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_dollar)
        init()
    }

    private fun init() {
//        backButton()
        ivBack.setOnClickListener(this)
        btnFindYourCar.setOnClickListener(this)
    }

    /* private fun backButton() {
         toolbar.setNavigationIcon(R.drawable.ic_back)
         toolbar.setTitleTextColor(resources.getColor(R.color.color545d64))

         setSupportActionBar(toolbar)
         if (supportActionBar != null) {
             supportActionBar!!.setDisplayHomeAsUpEnabled(true)
             supportActionBar!!.setDisplayShowHomeEnabled(true)
         }
     }*/

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                startActivity(
                    intentFor<MainActivity>().clearTask().newTask()
                )
            }
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}