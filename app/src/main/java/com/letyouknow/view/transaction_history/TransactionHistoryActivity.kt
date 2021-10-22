package com.letyouknow.view.transaction_history

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
import com.letyouknow.databinding.ActivityTransactionHistoryBinding
import com.letyouknow.retrofit.viewmodel.TransactionHistoryViewModel
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class TransactionHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTransactionHistoryBinding
    private lateinit var transactionHistoryViewModel: TransactionHistoryViewModel
    private lateinit var adapterTransactionHistory: TransactionHistoryAdapter
    private val arTransaction =
        arrayListOf(false, false, false, false, false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_history)
        init()
    }

    private fun init() {
        transactionHistoryViewModel =
            ViewModelProvider(this).get(TransactionHistoryViewModel::class.java)
        backButton()
        adapterTransactionHistory =
            TransactionHistoryAdapter(R.layout.list_item_transaction_history1, this)
        rvTransactionHistory.adapter = adapterTransactionHistory
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

    private var selectPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardTransaction -> {
                /* val pos = v.tag as Int
                 if (selectPos != -1) {
                     var data = adapterTransactionHistory.getItem(selectPos)
                     data = false
                     adapterTransactionHistory.update(selectPos, data)
                 }

                 var data = adapterTransactionHistory.getItem(pos)
                 data = true
                 adapterTransactionHistory.update(pos, data)
                 selectPos = pos*/
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callBidHistoryAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            transactionHistoryViewModel.transactionHistoryApiCall(this)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Bid Data", Gson().toJson(data))
                    adapterTransactionHistory.addAll(data)
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}