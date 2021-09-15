package com.letyouknow.view.account.editrefer.referstep2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.databinding.ActivityReferStep2Binding

class ReferStep2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityReferStep2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_step2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_step2)
        init()
    }

    private fun init() {

    }
}