package com.letyouknow.view.account.editinfo

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditInformationBinding
import kotlinx.android.synthetic.main.activity_edit_information.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class EditInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityEditInformationBinding

    private var arState = arrayListOf("State")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_information)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_information)
        init()
    }

    private fun init() {
        backButton()
        setState()
    }

    private fun setState() {
        val adapterYear = ArrayAdapter<String?>(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arState as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterYear
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