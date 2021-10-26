package com.letyouknow.view.transaction_history

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityTransactionHistoryBinding
import com.letyouknow.model.TransactionHistoryData
import com.letyouknow.retrofit.viewmodel.TransactionHistoryViewModel
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*

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
//        backButton()
        ivBack.setOnClickListener(this)
        adapterTransactionHistory =
            TransactionHistoryAdapter(R.layout.list_item_transaction_history1, this)
        rvTransactionHistory.adapter = adapterTransactionHistory

        callTransactionHistoryAPI()
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
            R.id.ivBack -> {
                onBackPressed()
            }
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
            R.id.tvSeeMore -> {
                val pos = v.tag as Int
                popupTransactionHistory(adapterTransactionHistory.getItem(pos))
            }
            R.id.llTransaction -> {
                val pos = v.tag as Int
                val data = adapterTransactionHistory.getItem(pos)
                if (!TextUtils.isEmpty(data.transactionCode))
                    startActivity<TransactionCodeDetailActivity>(Constant.ARG_TRANSACTION_CODE to data.transactionCode)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callTransactionHistoryAPI() {
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

    private fun popupTransactionHistory(data: TransactionHistoryData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_bid_history)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            data.run {
                var type = ""
                when (label) {
                    "LCD" -> type = "LightingCarDeals"
                    "UCD" -> type = "UnlockedCarDeals"
                    "LYK" -> type = "LetYouKnow"
                }
                tvDialogSuccessUnSuccess.text = type
                tvDialogTitle.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text =
                    if (TextUtils.isEmpty(vehicleExteriorColor)) "ANY" else vehicleExteriorColor
                tvDialogInteriorColor.text =
                    if (TextUtils.isEmpty(vehicleInteriorColor)) "ANY" else vehicleInteriorColor
                var accessories = ""
                for (i in 0 until vehicleAccessories?.size!!) {
                    accessories = if (i == 0) {
                        vehicleAccessories[i].accessory!!
                    } else {
                        accessories + ",\n" + vehicleAccessories[i].accessory!!
                    }
                }

                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }


                tvDialogPackage.text = if (packages.isEmpty()) "ANY" else packages
                tvDialogOptions.text = if (accessories.isEmpty()) "ANY" else accessories
                tvDialogZipCode.text = zipCode
                tvDialogRadius.text = if (searchRadius == "6000") "All" else searchRadius
                tvDialogPrice.text = NumberFormat.getCurrencyInstance(Locale.US).format(price)
                tvDialogSubmittedDate.text = timeStampFormatted

            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }
}