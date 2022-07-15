package com.letyouknow.view.samsungpay

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.databinding.ActivitySamPaymentBinding
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AddressControl
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetUpdatedListener
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SpinnerControl

class SamPaymentActivity : AppCompatActivity(), DialogInterface.OnClickListener {
    var mBinding: ActivitySamPaymentBinding? = null
    private val TAG = "SamsungPaymentActivity"
    private var mContext: Context? = null
    private var mSampleAppPartnerInfoHolder: SampleAppPartnerInfoHolder? = null
    private var mPaymentManager: PaymentManager? = null
    private var mActivityResumed = false

    // private var mAmountDetailControls: AmountDetailControls? = null
    private var mAmountDetailControls: AmountDetailNewControls? = null
    private var mBillingAddressControls: BillingAddressControls? = null
    private var mShippingAddressControls: ShippingAddressControls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sam_payment)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sam_payment)

        mContext = this.applicationContext

        init()
    }

    private fun init() {
        mSampleAppPartnerInfoHolder = SampleAppPartnerInfoHolder(mContext)

        val orderDetailsListener = OrderDetailsListener { bool ->
            if (bool) {
                enablePayButton()
            } else {
                disablePayButton()
            }
        }

        //   mAmountDetailControls = AmountDetailControls(mContext, mBinding!!.amountDetails, orderDetailsListener)
        mAmountDetailControls = AmountDetailNewControls(mContext, 1000.0, orderDetailsListener)

        val addressRequestListener =
            AddressRequestListener { type: CustomSheetPaymentInfo.AddressInPaymentSheet ->
                if (type == CustomSheetPaymentInfo.AddressInPaymentSheet.DO_NOT_SHOW || type == CustomSheetPaymentInfo.AddressInPaymentSheet.NEED_BILLING_SPAY) {
                    mAmountDetailControls!!.setAddedShippingAmount(0.0)
                    mAmountDetailControls!!.updateAndCheckAmountValidation()
                }
                mShippingAddressControls!!.setNeedAllShippingMethodItems(
                    type == CustomSheetPaymentInfo.AddressInPaymentSheet.NEED_SHIPPING_SPAY
                            || type == CustomSheetPaymentInfo.AddressInPaymentSheet.NEED_BILLING_AND_SHIPPING
                )
                mBillingAddressControls!!.updateBillingLayoutVisibility(type)
                mShippingAddressControls!!.updateShippingAddressLayout(type)
            }
        mBinding!!.tvSamsungPay.setOnClickListener { v -> startInAppPayWithCustomSheet() }
        updateSamsungPayButton()
    }

    protected fun updateSamsungPayButton() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        try {
            samsungPay.getSamsungPayStatus(object : StatusListener {
                override fun onSuccess(status: Int, bundle: Bundle) {
                    when (status) {
                        SpaySdk.SPAY_READY -> {
                            mBinding!!.tvSamsungPay.visibility = View.VISIBLE
                            if (mPaymentManager == null) {
                                mPaymentManager = PaymentManager(
                                    mContext,
                                    mSampleAppPartnerInfoHolder!!.partnerInfo
                                )
                                // Get Card List.
                            }
                        }
                        SpaySdk.SPAY_NOT_SUPPORTED, SpaySdk.SPAY_NOT_READY, SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> mBinding!!.tvSamsungPay.setVisibility(
                            View.INVISIBLE
                        )
                        else -> mBinding!!.tvSamsungPay.visibility = View.INVISIBLE
                    }
                    showOnSuccessLog(status, bundle) // Print log
                    showOnSuccessMessage(status, bundle) // Print messages.
                }

                override fun onFail(errorCode: Int, bundle: Bundle) {
                    mBinding!!.tvSamsungPay.visibility = View.INVISIBLE
                    showOnFailLogAndMessage(errorCode, bundle) // Print log and messages.
                }
            })
        } catch (e: NullPointerException) {
            Log.e(
                TAG, e.message!!
            )
        }
    }

    private fun showOnSuccessLog(status: Int, bundle: Bundle) {
        Log.d(TAG, "getSamsungPayStatus status : $status")
        val extraError = bundle.getInt(SpaySdk.EXTRA_ERROR_REASON)
        Log.d(
            TAG,
            TAG + extraError + " / " + ErrorCode.getInstance()
                .getErrorCodeName(extraError)
        )
    }

    private fun showOnSuccessMessage(status: Int, bundle: Bundle) {
        Log.d(TAG, "showOnSuccessMessage")
        /* if (!getUserVisibleHint()) {
             return
         }*/
        when (status) {
            SpaySdk.SPAY_NOT_SUPPORTED -> {
                displayToastMessageIfRequired(getString(R.string.spay_not_supported))
            }
            SpaySdk.SPAY_NOT_READY -> {
                val extraError = bundle.getInt(SpaySdk.EXTRA_ERROR_REASON)
                displayToastMessageIfRequired(getString(R.string.spay_not_ready))
                mSampleAppPartnerInfoHolder!!.spayNotReadyStatus = extraError
                SamsungPayStatusDialog.getInstance()
                    .showSamsungPayStatusErrorDialog(this, extraError, this)
            }
            SpaySdk.SPAY_READY -> {
                displayToastMessageIfRequired(getString(R.string.spay_ready))
            }
            SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> {
                val extraError = bundle.getInt(SpaySdk.EXTRA_ERROR_REASON)
                displayToastMessageIfRequired(getString(R.string.spay_not_allowed_temporally) + " / " + extraError)
            }
            else -> {
                displayToastMessageIfRequired(getString(R.string.get_samsung_pay_status_result) + ": " + status)
            }
        }
    }

    private fun showOnFailLogAndMessage(errorCode: Int, bundle: Bundle?) {
        var extraReason = SpaySdk.ERROR_NONE
        if (bundle != null && bundle.containsKey(SpaySdk.EXTRA_ERROR_REASON)) {
            extraReason = bundle.getInt(SpaySdk.EXTRA_ERROR_REASON)
        }
        if (this == null || this.isFinishing()) {
            Log.e(
                TAG,
                "showOnFailLogAndMessage " + ErrorCode.getInstance().getErrorCodeName(errorCode)
                    .toString() + ", extraReason = " + extraReason
            )
        } else {
            displayToastMessageIfRequired(
                getString(R.string.get_samsung_pay_status_on_fail) + errorCode
                        + " " + ErrorCode.getInstance().getErrorCodeName(errorCode)
                        + ", extraReason = " + extraReason
            )
        }
    }

    private fun displayToastMessageIfRequired(msg: String) {
        displayToastMessageIfRequired(msg, false)
    }

    private fun displayToastMessageIfRequired(msg: String, isRetry: Boolean) {
        if (mActivityResumed) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
        } else if (!isRetry) {
            Handler(Looper.getMainLooper()).postDelayed({
                displayToastMessageIfRequired(
                    msg,
                    true
                )
            }, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        mActivityResumed = true
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        mActivityResumed = false
    }

    private fun doActivateSamsungPay() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.activateSamsungPay()
    }

    private fun doUpdateSamsungPay() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.goToUpdatePage()
    }

    protected fun startInAppPayWithCustomSheet() {
        Log.d(
            TAG, "startInAppPayWithCustomSheet"
        )
        // PaymentManager.startInAppPayWithCustomSheet method to show custom payment sheet.
        disablePayButton()
        mPaymentManager = PaymentManager(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)

        mPaymentManager!!.startInAppPayWithCustomSheet(
            makeTransactionDetailsWithSheet(),
            transactionListener
        )

    }

    private fun makeTransactionDetailsWithSheet(): CustomSheetPaymentInfo? {
        val customSheetPaymentInfo: CustomSheetPaymentInfo
        val customSheetPaymentInfoBuilder = CustomSheetPaymentInfo.Builder()
        val extraPaymentInfo = Bundle()

        customSheetPaymentInfo = customSheetPaymentInfoBuilder
            .setMerchantId("")
            .setMerchantName(mSampleAppPartnerInfoHolder!!.sampleAppName)
            .setOrderNumber("1")
            .setCustomSheet(makeUpCustomSheet())
            .setExtraPaymentInfo(extraPaymentInfo)
            .build()
        return customSheetPaymentInfo
    }

    private fun makeUpCustomSheet(): CustomSheet? {
        val sheetUpdatedListener = SheetUpdatedListener { controlId: String, sheet: CustomSheet? ->
            Log.d(TAG, "onResult control id : $controlId")
            updateControlId(controlId, sheet!!)
        }

        val customSheet = CustomSheet()
        customSheet.addControl(mAmountDetailControls!!.makeAmountControl())
        return customSheet
    }

    private fun updateControlId(controlId: String, sheet: CustomSheet) {
        Log.d(
            TAG, "updateSheet : $controlId"
        )
        when (controlId) {
            BillingAddressControls.BILLING_ADDRESS_ID ->                 // Call updateSheet with AmountBoxControl. This is mandatory
                receivedBillingAddress(controlId, sheet)
            ShippingAddressControls.SHIPPING_ADDRESS_ID ->                 // Call updateSheet with AmountBoxControl. This is mandatory
                receivedBillingAddress(controlId, sheet)
            ShippingAddressControls.SHIPPING_METHOD_SPINNER_ID -> receivedShippingMethodSpinner(
                controlId,
                sheet
            )
            else -> Log.e(
                TAG, "sheetUpdatedListener default called:"
            )
        }
    }

    protected fun receivedBillingAddress(updatedControlId: String?, sheet: CustomSheet) {
        val addressControl = sheet.getSheetControl(updatedControlId) as AddressControl
        if (addressControl == null) {
            Log.e(TAG, "receivedBillingAddress addressControl  : null ")
            return
        }
        val billAddress = addressControl.address
        val errorCode = 201
        addressControl.errorCode = errorCode
        sheet.updateControl(addressControl)
        val needCustomErrorMessage = mBillingAddressControls!!.needCustomErrorMessage()
        Log.d(
            TAG,
            "onResult receivedBillingAddress  errorCode: $errorCode, customError: $needCustomErrorMessage"
        )
        updateSheetToSdk(mAmountDetailControls!!.updateAmountControl(sheet))

    }

    private fun receivedShippingMethodSpinner(updatedControlId: String, sheet: CustomSheet) {
        val shippingMethodSpinnerControl = sheet.getSheetControl(updatedControlId) as SpinnerControl
        if (shippingMethodSpinnerControl == null) {
            Log.e(TAG, "onResult shippingMethodSpinnerControl: null")
            return
        }
        if (shippingMethodSpinnerControl.selectedItemId == null) {
            Log.e(TAG, "onResult shippingMethodSpinnerControl  getSelectedItemId : null")
            return
        }

        updateSheetToSdk(mAmountDetailControls!!.updateAmountControl(sheet))
    }

    private val transactionListener: PaymentManager.CustomSheetTransactionInfoListener =
        object : PaymentManager.CustomSheetTransactionInfoListener {
            // This callback is received when the user changes card on the custom payment sheet in Samsung Pay.
            override fun onCardInfoUpdated(selectedCardInfo: CardInfo, customSheet: CustomSheet) {
                Log.d(TAG, "onCardInfoUpdated $selectedCardInfo")
                displayToastMessageIfRequired("onCardInfoUpdated")
                updateSheetToSdk(customSheet)
            }

            override fun onSuccess(
                response: CustomSheetPaymentInfo, paymentCredential: String,
                extraPaymentData: Bundle
            ) {
                Log.d(TAG, "Transaction : onSuccess $extraPaymentData")
                val fragmentActivity: Activity = this@SamPaymentActivity
                if (fragmentActivity == null || fragmentActivity.isFinishing || fragmentActivity.isDestroyed) {
                    return
                }
                val mPaymentResultDialog = PaymentResultDialog(this@SamPaymentActivity)
                mPaymentResultDialog.onSuccessDialog(response, paymentCredential, extraPaymentData)
                displayToastMessageIfRequired("Transaction : onSuccess")
                enablePayButton()
            }

            // This callback is received when the online payment transaction has failed.
            override fun onFailure(errorCode: Int, errorData: Bundle) {
                try {
                    val errorName: String = ErrorCode.getInstance().getErrorCodeName(errorCode)
                    var extraReason = 0
                    var extraReasonMsg: String? = null
                    if (errorData != null) {
                        extraReason = errorData.getInt(SpaySdk.EXTRA_ERROR_REASON)
                        extraReasonMsg = errorData.getString(SpaySdk.EXTRA_ERROR_REASON_MESSAGE)
                    }
                    Log.d(TAG, "Transaction : onFailure $errorCode / $errorName / $extraReason")
                    displayToastMessageIfRequired(
                        "Transaction : onFailure - " + errorCode + " / " + errorName
                                + " / " + extraReasonMsg
                    )
                    // Called when some error occurred during in-app cryptogram generation.
                    enablePayButton()
                } catch (e: java.lang.NullPointerException) {
                    Log.e(
                        TAG, (e.message)!!
                    )
                }
            }
        }

    protected fun updateSheetToSdk(sheet: CustomSheet?) {
        Handler().postDelayed({
            try {
                Log.d(TAG, "updateSheetToSdk")
                mPaymentManager!!.updateSheet(mAmountDetailControls!!.updateAmountControl(sheet))
            } catch (e: IllegalStateException) {
                //Service is disconnected.
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                e.printStackTrace()
            }
        }, 0)
    }

    private fun enablePayButton() {
        mBinding!!.tvSamsungPay.isClickable = true
        mBinding!!.tvSamsungPay.alpha = 1.0f
    }

    private fun disablePayButton() {
        mBinding!!.tvSamsungPay.isClickable = false
        mBinding!!.tvSamsungPay.alpha = 0.4f
    }

    override fun onClick(dialogInterface: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                if (SpaySdk.ERROR_SPAY_APP_NEED_TO_UPDATE == mSampleAppPartnerInfoHolder!!.spayNotReadyStatus) {
                    doUpdateSamsungPay()
                } else if (SpaySdk.ERROR_SPAY_SETUP_NOT_COMPLETED == mSampleAppPartnerInfoHolder!!.spayNotReadyStatus) {
                    doActivateSamsungPay()
                }
                dialogInterface.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> dialogInterface.cancel()
            else -> dialogInterface.dismiss()
        }
    }
}