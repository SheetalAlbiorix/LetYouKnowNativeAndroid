package com.letyouknow.view.bidhistory

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityBidHistoryBinding
import com.letyouknow.retrofit.viewmodel.BidHistoryViewModel
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_bid_history.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class BidHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterBidHistory: BidHistoryAdapter
    private lateinit var binding: ActivityBidHistoryBinding
    private lateinit var bidHistoryViewModel: BidHistoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_history)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid_history)
        init()
    }

    private fun init() {
        bidHistoryViewModel = ViewModelProvider(this).get(BidHistoryViewModel::class.java)
        backButton()
        adapterBidHistory = BidHistoryAdapter(R.layout.list_item_bid_history, this)
        rvBidHistory.adapter = adapterBidHistory
        val arBid = arrayListOf("", "", "", "", "", "", "", "", "", "")
        adapterBidHistory.addAll(arBid)
        callBidHistoryAPI()
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

    private fun callBidHistoryAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            bidHistoryViewModel.bidHistoryApiCall(this)!!.observe(this, Observer { data ->
                Constant.dismissLoader()
                Log.e("Bid Data", Gson().toJson(data))
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

}