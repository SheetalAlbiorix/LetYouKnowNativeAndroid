package com.letyouknow.view.ucd.submitdealsummary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivitySubmitDealSummaryBinding
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_CAL_TAX_DATA
import com.letyouknow.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_PRODUCT
import com.letyouknow.view.dashboard.MainActivity
import kotlinx.android.synthetic.main.activity_submit_deal_summary.*
import kotlinx.android.synthetic.main.dialog_dealer_receipt.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.text.NumberFormat
import java.util.*

class SubmitDealSummaryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var submitDealData: SubmitDealLCDData
    private lateinit var calculateTaxData: CalculateTaxData
    private lateinit var product: String
    private lateinit var binding: ActivitySubmitDealSummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_deal_summary)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_deal_summary)
        if (intent.hasExtra(ARG_CAL_TAX_DATA) && intent.hasExtra(ARG_SUBMIT_DEAL) && intent.hasExtra(
                Constant.ARG_TYPE_PRODUCT
            )
        ) {
            submitDealData = Gson().fromJson(
                intent.getStringExtra(ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )
            calculateTaxData = Gson().fromJson(
                intent.getStringExtra(ARG_CAL_TAX_DATA),
                CalculateTaxData::class.java
            )
            binding.taxData = calculateTaxData

            if (AppGlobal.isNotEmpty(submitDealData.miles) || AppGlobal.isNotEmpty(submitDealData.conditions)) {
                if (AppGlobal.isNotEmpty(submitDealData.miles)) {
                    tvDisclosure.text =
                        getString(R.string.miles_approximate_odometer_reading, submitDealData.miles)
                }

                if (AppGlobal.isNotEmpty(submitDealData.conditions)) {
                    if (AppGlobal.isEmpty(submitDealData.miles)) {
                        tvDisclosure.text = submitDealData.conditions
                    } else {
                        tvDisclosure.text =
                            tvDisclosure.text.toString().trim() + ", " + submitDealData.conditions
                    }
                }
                tableRowDis.visibility = View.VISIBLE
            } else {
                tableRowDis.visibility = View.GONE
            }

            product = intent.getStringExtra(ARG_TYPE_PRODUCT)!!
            binding.userName = pref?.getUserData()?.firstName + " " + pref?.getUserData()?.lastName
            binding.data = submitDealData

            var add2 = submitDealData.successResult?.transactionInfo?.buyerAddress2
            if (add2 != "null" && add2 != "NULL" && !TextUtils.isEmpty(add2?.trim())) {
                add2 += "\n"
            }

            var buyerName = ""

            if (TextUtils.isEmpty(submitDealData.successResult?.transactionInfo?.buyerName)) {
                buyerName = ""
            } else {
                val arName = submitDealData.successResult?.transactionInfo?.buyerName?.split(" ")
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
            var buyerMobNo = ""
            buyerMobNo =
                if (submitDealData.successResult?.transactionInfo?.buyerPhone?.contains("(") == false)
                    AppGlobal.formatPhoneNo(submitDealData.successResult?.transactionInfo?.buyerPhone)!!
                else
                    submitDealData.successResult?.transactionInfo?.buyerPhone!!

            var buyerInfo = ""
            buyerInfo =
                buyerName +
                        submitDealData.successResult?.transactionInfo?.buyerAddress1 + "\n" +
                        add2 +
                        submitDealData.successResult?.transactionInfo?.buyerCity + ", " +
                        submitDealData.successResult?.transactionInfo?.buyerState + " " +
                        submitDealData.successResult?.transactionInfo?.buyerZipcode + "\n" +
                        buyerMobNo + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerEmail

            var dealInfoMob = ""
            dealInfoMob = if (submitDealData.matchedDealerInfo?.phoneNumber?.contains("(") == false)
                AppGlobal.formatPhoneNo(submitDealData.matchedDealerInfo?.phoneNumber)!!
            else
                submitDealData.matchedDealerInfo?.phoneNumber!!

            var dealerInfo = ""
            dealerInfo =
                submitDealData.matchedDealerInfo?.addressInfo?.address1!! + " " +
                        submitDealData.matchedDealerInfo?.addressInfo?.address2!! + "\n" +
                        submitDealData.matchedDealerInfo?.addressInfo?.city!! + ", " +
                        submitDealData.matchedDealerInfo?.addressInfo?.state!! + " " +
                        submitDealData.matchedDealerInfo?.addressInfo?.zipcode!! + "\n" +
                        submitDealData.matchedDealerInfo?.email + "\n" +
                        dealInfoMob

            tvBuyerInfo.text = buyerInfo
            tvDealerInfo.text = dealerInfo
            tvLabelPrice.text = "Your " + product + " Price:"

            if (submitDealData.successResult?.savings!! > 0.0) {
                tvSavingsText.visibility = View.VISIBLE
            } else {
                tvSavingsText.visibility = View.GONE
            }

            var total =
                (submitDealData?.successResult?.transactionInfo?.remainingBalance!!) - submitDealData.successResult?.transactionInfo?.lykDollar!!
            binding.total = total
        }
        btnFindYourCar.setOnClickListener(this)
        tvCallNumber.setOnClickListener(this)
        tvDealerReceipt.setOnClickListener(this)
        tvCallNumber.text = Html.fromHtml(getString(R.string.if_you_have_any_questions))
        ivBack.visibility = View.GONE
        tvTitleTool.visibility = View.GONE
//        showProgressDialog()
    }


    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                onBackPressed()
            }
            R.id.tvCallNumber -> {
                AppGlobal.callDialerOpen(this, getString(R.string.number))
            }
            R.id.tvDealerReceipt -> {
                showDealerReceiptDialog(submitDealData)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            intentFor<MainActivity>().clearTask().newTask()
        )
    }

    private fun showDealerReceiptDialog(data: SubmitDealLCDData) {
        val transData = data.successResult?.transactionInfo
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

}