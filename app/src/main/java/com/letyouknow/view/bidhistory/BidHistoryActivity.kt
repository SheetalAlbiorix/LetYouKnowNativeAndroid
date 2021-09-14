package com.letyouknow.view.bidhistory

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityBidHistoryBinding
import kotlinx.android.synthetic.main.activity_bid_history.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class BidHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterBidHistory: BidHistoryAdapter
    private lateinit var binding: ActivityBidHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_history)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid_history)
        init()
    }

    private fun init() {
        backButton()
        adapterBidHistory = BidHistoryAdapter(R.layout.list_item_bid_history, this)
        rvBidHistory.adapter = adapterBidHistory
        val arBid = arrayListOf("", "", "", "", "", "", "", "", "", "")
        adapterBidHistory.addAll(arBid)

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

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}