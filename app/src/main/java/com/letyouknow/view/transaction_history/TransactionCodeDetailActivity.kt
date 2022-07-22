package com.letyouknow.view.transaction_history

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityTransactionCodeDetailBinding
import com.letyouknow.model.TransactionCodeData
import com.letyouknow.retrofit.viewmodel.TransactionCodeViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.callDialerOpen
import com.letyouknow.utils.AppGlobal.Companion.formatPhoneNo
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IS_NOTIFICATION
import com.letyouknow.utils.Constant.Companion.ARG_TRANSACTION_CODE
import com.letyouknow.view.dashboard.MainActivity
import kotlinx.android.synthetic.main.activity_transaction_code_detail.*
import kotlinx.android.synthetic.main.activity_transaction_code_detail.tvBasedState
import kotlinx.android.synthetic.main.dialog_dealer_receipt.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.text.NumberFormat
import java.util.*

class TransactionCodeDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTransactionCodeDetailBinding

    private lateinit var transactionCodeViewModel: TransactionCodeViewModel
    private var isNotification = false
    private lateinit var transData: TransactionCodeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_code_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_code_detail)
        init()
    }

    private fun init() {
        transactionCodeViewModel =
            ViewModelProvider(this)[TransactionCodeViewModel::class.java]
//        backButton()
        if (intent.hasExtra(ARG_TRANSACTION_CODE)) {
            val code = intent.getStringExtra(ARG_TRANSACTION_CODE)
            callTransactionCodeAPI(code)
        }
        if (intent.hasExtra(ARG_IS_NOTIFICATION)) {
            isNotification = true
        }
        btnFindYourCar.setOnClickListener(this)
        tvCallNumber.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        tvDealerReceipt.setOnClickListener(this)
        tvCallNumber.text = Html.fromHtml(getString(R.string.if_you_have_any_questions))
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
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

            transactionCodeViewModel.transactionCodeApiCall(this, code)!!
                .observe(this, Observer { data ->
                    transData = data
                    Constant.dismissLoader()
                    //  Log.e("Transaction Code Data", Gson().toJson(data))
                    tvVehicle.text =
                        data.vehicleYear + " " + data.vehicleMake + " " + data.vehicleModel + " " + data.vehicleTrim
                    tvPaymentMethod.text = data.paymentMethod + "\n" + getString(
                        R.string.last_4_digits,
                        data.paymentLast4
                    )
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
                    var buyerName = ""

                    if (TextUtils.isEmpty(data.buyerName)) {
                        buyerName = ""
                    } else {
                        val arName = data.buyerName?.split(" ")
                        if (arName?.size!! > 0) {
                            buyerName = when (arName.size) {
                                3 -> {
                                    val isMiddle = arName[1][0].let {
                                        Constant.middleNameValidator(
                                            it.toString()
                                        )
                                    }
                                    val middleName = if (isMiddle) {
                                        (arName[1][0] + " ")
                                    } else {
                                        ""
                                    }

                                    arName[0] + " " + middleName + arName[2] + "\n"
                                }
                                2 -> {
                                    arName[0] + " " + arName[1] + "\n"
                                }
                                else -> {
                                    arName[0] + "\n"
                                }
                            }

                        }
                    }
                    var buyerInfo = ""
                    buyerInfo =
                        buyerName +
                                data.buyerAddress1 + "\n" +
                                add2 +
                                data.buyerCity + ", " +
                                data.buyerState + " " +
                                data.buyerZipcode + "\n" +
                                data.buyerPhone + "\n" +
                                data.buyerEmail
                    tvBuyerInfo.text = buyerInfo
                    var promoCode = "-$0.00"
                    if (!TextUtils.isEmpty(data.discount)) {
                        promoCode = "-" + NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data.discount?.toFloat())
                    }
                    tvPromoCode.text = promoCode

                    labelPrice.text = "Your " + data.product + " Price:"
                    if (data.savings!! > 0.0f) {
                        tvSavingsText.visibility = View.VISIBLE
                    } else {
                        tvSavingsText.visibility = View.GONE
                    }

                    if (AppGlobal.isNotEmpty(data.vehicleMiles) || AppGlobal.isNotEmpty(data.vehicleCondition)) {
                        if (AppGlobal.isNotEmpty(data.vehicleMiles))
                            tvDisclosure.text =
                                getString(
                                    R.string.miles_approximate_odometer_reading,
                                    data.vehicleMiles
                                )

                        if (AppGlobal.isNotEmpty(data.vehicleCondition)) {
                            if (AppGlobal.isEmpty(data.vehicleMiles)) {
                                tvDisclosure.text = data.vehicleCondition
                            } else {
                                tvDisclosure.text = tvDisclosure.text.toString()
                                    .trim() + ", " + data.vehicleCondition
                            }
                        }
                        llDisc.visibility = View.VISIBLE
                    } else {
                        llDisc.visibility = View.GONE
                    }
                    if (TextUtils.isEmpty(data.dealerDto.addressInfo?.address2)) {
                        data.dealerDto.addressInfo?.address2 = ""
                    }
                    binding.data = data

//                    var total = data.remainingBalance!! - data.lykDollar!!
                    var total = data.remainingBalance!!
                    binding.total = total
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                startActivity(
                    intentFor<MainActivity>().clearTask().newTask()
                )
            }
            R.id.tvCallNumber -> {
                callDialerOpen(this, getString(R.string.number))
            }
            R.id.tvDealerReceipt -> {
                if (::transData.isInitialized)
                    showDealerReceiptDialog(transData)
            }
            R.id.ivBack -> {
//                if (isNotification) {
                onBackPressed()
//                } else {
//                    finish()
//                }
            }
        }
    }

    override fun onBackPressed() {
        if (isNotification) {
            startActivity(
                intentFor<MainActivity>().clearTask().newTask()
            )
        } else {
            finish()
        }
//       super.onBackPressed()

    }

    private fun showDealerReceiptDialog(transData: TransactionCodeData) {
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
                Html.fromHtml(getString(R.string.based_on_selected_state_of, transData?.buyerState))
            tvEstimatedTaxDiscount.text = "-${
                NumberFormat.getCurrencyInstance(Locale.US)
                    .format(transData?.estimatedRebates)
            }"

            tvSelectRebate.text =
                Html.fromHtml(
                    getString(
                        R.string.subject_to_eligibility_verification_by_the_dealership,
                        if (TextUtils.isEmpty(transData?.rebateDetails)) "None" else transData?.rebateDetails
                    )
                )

            ivDialogClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogReceipt)
        dialogReceipt.show()
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