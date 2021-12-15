package com.letyouknow.view.transaction_history

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityTransactionCodeDetailBinding
import com.letyouknow.retrofit.viewmodel.TransactionCodeViewModel
import com.letyouknow.utils.AppGlobal.Companion.formatPhoneNo
import com.letyouknow.view.dashboard.MainActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_TRANSACTION_CODE
import kotlinx.android.synthetic.main.activity_transaction_code_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class TransactionCodeDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTransactionCodeDetailBinding

    private lateinit var transactionCodeViewModel: TransactionCodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_code_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_code_detail)
        init()
    }

    private fun init() {
        transactionCodeViewModel =
            ViewModelProvider(this).get(TransactionCodeViewModel::class.java)
        backButton()
        if (intent.hasExtra(ARG_TRANSACTION_CODE)) {
            val code = intent.getStringExtra(ARG_TRANSACTION_CODE)
            callTransactionCodeAPI(code)
        }
        btnFindYourCar.setOnClickListener(this)
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

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun callTransactionCodeAPI(code: String?) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            transactionCodeViewModel.transactionCodeApiCall(this, code)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Transaction Code Data", Gson().toJson(data))
                    var accessories = ""
                    for (i in 0 until data.vehicleAccessories?.size!!) {
                        accessories = if (i == 0) {
                            data.vehicleAccessories[i].accessory!!
                        } else {
                            accessories + ",\n" + data.vehicleAccessories[i].accessory!!
                        }
                    }
                    data.accessoriesStr = accessories

                    var packages = ""
                    for (i in 0 until data.vehiclePackages?.size!!) {
                        packages = if (i == 0) {
                            data.vehiclePackages[i].packageName!!
                        } else {
                            packages + ",\n" + data.vehiclePackages[i].packageName!!
                        }
                    }
                    data.packageStr = packages
                    if (data.buyerPhone?.contains("(") != true) {
                        data.buyerPhone = formatPhoneNo(data.buyerPhone)
                    }
                    var add2 = data.buyerAddress2
                    if (add2 != "null" && add2 != "NULL" && !TextUtils.isEmpty(add2?.trim())) {
                        add2 += "\n"
                    }
                    var buyerInfo = ""
                    buyerInfo =
                        data.buyerName + "\n" +
                                data.buyerAddress1 + "\n" +
                                add2 +
                                data.buyerCity + ", " +
                                data.buyerState + " " +
                                data.buyerZipcode + "\n" +
                                data.buyerPhone + "\n" +
                                data.buyerEmail
                    tvBuyerInfo.text = buyerInfo
                    binding.data = data
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            intentFor<MainActivity>().clearTask().newTask()
        )
    }
}