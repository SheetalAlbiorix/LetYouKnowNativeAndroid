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
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.ivDialogClose
import kotlinx.android.synthetic.main.dialog_dealer_receipt.*
import kotlinx.android.synthetic.main.dialog_my_receipt.*
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
            R.id.tvSeeMore -> {
                val pos = v.tag as Int
                popupMyReceipt(adapterTransactionHistory.getItem(pos))
//                popupTransactionHistory(adapterTransactionHistory.getItem(pos))
            }
            R.id.llTransaction -> {
                val pos = v.tag as Int
                val data = adapterTransactionHistory.getItem(pos)
                if (!TextUtils.isEmpty(data.transactionCode))
                    startActivity<TransactionCodeDetailActivity>(Constant.ARG_TRANSACTION_CODE to data.transactionCode)
            }

            R.id.tvDealerReceipt -> {
                val pos = v.tag as Int
                showDealerReceiptDialog(adapterTransactionHistory.getItem(pos))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callTransactionHistoryAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

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
                    "LCD" -> type = "LightningCarDeals"
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
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDialogDisclosure.text =
                            context.getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDialogDisclosure.text = condition
                        } else {
                            tvDialogDisclosure.text =
                                tvDialogDisclosure.text.toString().trim() + ", " + condition
                        }

                    }
                    llDisc.visibility = View.VISIBLE
                } else {
                    llDisc.visibility = View.GONE
                }
            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun popupMyReceipt(data: TransactionHistoryData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_my_receipt)
        dialog.run {
            ivDialogCloseRec.setOnClickListener {
                dismiss()
            }
            data.run {
                var type = ""
                when (label) {
                    "LCD" -> type = "LightningCarDeals"
                    "UCD" -> type = "UnlockedCarDeals"
                    "LYK" -> type = "LetYouKnow"
                }
                tvDialogSuccessUnSuccessRec.text = type
                tvDialogTitleRec.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColorRec.text =
                    if (TextUtils.isEmpty(vehicleExteriorColor)) "ANY" else vehicleExteriorColor
                tvDialogInteriorColorRec.text =
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


                tvDialogPackageRec.text = if (packages.isEmpty()) "ANY" else packages
                tvDialogOptionsRec.text = if (accessories.isEmpty()) "ANY" else accessories
                tvDialogZipCodeRec.text = zipCode
                tvDialogRadiusRec.text = if (searchRadius == "6000") "All" else searchRadius
                tvDialogSubmittedDateRec.text = timeStampFormatted
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDialogDisclosureRec.text =
                            context.getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDialogDisclosureRec.text = condition
                        } else {
                            tvDialogDisclosureRec.text =
                                tvDialogDisclosureRec.text.toString().trim() + ", " + condition
                        }

                    }
                    llDiscRec.visibility = View.VISIBLE
                } else {
                    llDiscRec.visibility = View.GONE
                }

                tvPriceRec.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data?.price)
                tvPrePaymentRec.text =
                    "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.reservationPrepayment)
                    }"
                tvRemainingBalRec.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data?.remainingBalance)

                tvEstimatedTaxRec.text =
                    "+${NumberFormat.getCurrencyInstance(Locale.US).format(data?.carSalesTax)}"
                tvEstimatedRegRec.text =
                    "+${
                        NumberFormat.getCurrencyInstance(Locale.US).format(data?.nonTaxRegFee)
                    }"
                tvEstimatedTotalRec.text = NumberFormat.getCurrencyInstance(Locale.US)
                    .format(data?.estimatedTotalRemainingBalance)
                if (data.discount != null && data.discount != 0.0) {
                    tvPromoRec.text = "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.discount)
                    }"
                    llPromoRec.visibility = View.VISIBLE
                } else {
                    llPromoRec.visibility = View.GONE
                }

                if (data.lykDollar != null && data.lykDollar != 0.0) {
                    tvDollarsRec.text = "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.lykDollar)
                    }"
                    llDollarRec.visibility = View.VISIBLE
                } else {
                    llDollarRec.visibility = View.GONE
                }
                tvBasedStateRec.text =
                    getString(R.string.based_on_selected_state_of, data?.buyerState)
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

    private fun showDealerReceiptDialog(transData: TransactionHistoryData) {
        val dialogReceipt = Dialog(this, R.style.FullScreenDialog)
        dialogReceipt.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogReceipt.setCancelable(false)
        dialogReceipt.setContentView(R.layout.dialog_dealer_receipt)
        dialogReceipt.run {
            tvAdjustLYKPrice.text =
                NumberFormat.getCurrencyInstance(Locale.US).format(transData?.adjustedLYKPrice)
            tvDocFee.text =
                "+${
                    NumberFormat.getCurrencyInstance(Locale.US).format(transData?.documentationFee)
                }"
            tvPrePayment.text =
                "-${
                    NumberFormat.getCurrencyInstance(Locale.US)
                        .format(transData?.prePaymentToDealer)
                }"
            tvRemainingBal.text =
                NumberFormat.getCurrencyInstance(Locale.US).format(transData?.remainingBalance)

            tvEstimatedTax.text =
                "+${NumberFormat.getCurrencyInstance(Locale.US).format(transData?.carSalesTax)}"
            tvEstimatedReg.text =
                "+${
                    NumberFormat.getCurrencyInstance(Locale.US).format(transData?.nonTaxRegFee)
                }"
            tvEstimatedTotal.text = NumberFormat.getCurrencyInstance(Locale.US)
                .format(transData?.estimatedTotalRemainingBalance)
            tvBasedState.text =
                getString(R.string.based_on_selected_state_of, transData?.buyerState)
            ivDialogClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogReceipt)
        dialogReceipt.show()
    }
}