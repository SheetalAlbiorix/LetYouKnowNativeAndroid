package com.letyouknow.view.samsungpay

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.databinding.ActivitySamsungPaymentBinding
import com.letyouknow.view.samsungpay.BillingAddressControls.BILLING_ADDRESS_ID
import com.letyouknow.view.samsungpay.ShippingAddressControls.*
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager.CardInfoListener
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager.CustomSheetTransactionInfoListener
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.*

class SamsungPaymentActivity : AppCompatActivity(), DialogInterface.OnClickListener {

    var mBinding: ActivitySamsungPaymentBinding? = null
    private val TAG = "SamsungPaymentActivity"

    private val PLAIN_TEXT_PRE_DEFINED_EXAMPLE_ID = "PLAIN_TEXT_PRE_DEFINED_EXAMPLE_ID"
    private val PLAIN_TEXT_EDITABLE_EXAMPLE_ID = "PLAIN_TEXT_EDITABLE_EXAMPLE_ID"

    private var mContext: Context? = null

    private var mSampleAppPartnerInfoHolder: SampleAppPartnerInfoHolder? = null

    private var mRequestAddressOptions: RequestAddressOptions? = null
    private var mBillingAddressControls: BillingAddressControls? = null
    private var mShippingAddressControls: ShippingAddressControls? = null
    private var mAmountDetailControls: AmountDetailControls? = null

    private var mActivityResumed = false

    private var mPaymentManager: PaymentManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samsung_payment)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_samsung_payment)

        mContext = this.applicationContext
        initView()
    }

    protected fun initView() {
        // sample app title area(server level, debug mode)
        mSampleAppPartnerInfoHolder = SampleAppPartnerInfoHolder(mContext)
        mBinding!!.cardBrandControl.cardBrandControlEnable
            .setOnCheckedChangeListener { buttonView, isChecked ->
                mBinding!!.cardBrandControl.cardBrandControlLayout.visibility =
                    if (isChecked) View.VISIBLE else View.GONE
            }

        // plain text, custom error message
        mBinding!!.extraControl.extraControlSheetEnable.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding!!.extraControl.extraControlSheetLayout.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }
        mBinding!!.extraControl.updateSheetWithMessageCheckbox.setOnCheckedChangeListener { button, checked ->
            mBinding!!.extraControl.customErrorMessageEditLayout.visibility =
                if (checked) View.VISIBLE else View.GONE
        }
        mBinding!!.extraControl.plainTextEditableCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding!!.extraControl.plainTextMessageLayout.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        // request address options
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
        mRequestAddressOptions =
            RequestAddressOptions(mBinding!!.addressControlsTitle, addressRequestListener)

        // billing address controls
        mBillingAddressControls = BillingAddressControls(mContext, mBinding!!.billingAddressLayout)

        // shipping address controls
        val shippingMethodListener = ShippingMethodListener { newAmount ->
            mAmountDetailControls!!.setAddedShippingAmount(newAmount)
            mAmountDetailControls!!.updateAndCheckAmountValidation()
        }
        mShippingAddressControls =
            ShippingAddressControls(mContext, mBinding!!.shippingAddressControl)
        mShippingAddressControls!!.setShippingMethodChangedListener(shippingMethodListener)

        // amount controls
        val orderDetailsListener = OrderDetailsListener { bool ->
            if (bool) {
                enablePayButton()
            } else {
                disablePayButton()
            }
        }
        mAmountDetailControls =
            AmountDetailControls(mContext, mBinding!!.amountDetails, orderDetailsListener)

        // SDK APIs test buttons
        mBinding!!.sdkApiTest.getSamsungPayStatusApiTestButton.setOnClickListener { v -> updateSamsungPayButton() }
        mBinding!!.sdkApiTest.requestCardInfoApiTestButton.setOnClickListener { v -> requestCardInfoMethodTest() }
        mBinding!!.sdkApiTest.activateSamsungPayApiTestButton.setOnClickListener { v -> doActivateSamsungPay() }
        mBinding!!.sdkApiTest.goToUpdatePageApiTestButton.setOnClickListener { v -> doUpdateSamsungPay() }
        mBinding!!.samsungPayButton.setOnClickListener { v -> startInAppPayWithCustomSheet() }
        updateSamsungPayButton()
    }

    protected fun updateSamsungPayButton() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        try {

            /*
             * Method to get the Samsung Pay status on the device.
             * Partner (Issuers, Merchants, Wallet providers, and so on) applications must call this method to
             * check the current state of Samsung Pay before doing any operation.
             */
            samsungPay.getSamsungPayStatus(object : StatusListener {
                override fun onSuccess(status: Int, bundle: Bundle) {
                    when (status) {
                        SpaySdk.SPAY_READY -> {
                            mBinding!!.samsungPayButton.visibility = View.VISIBLE
                            if (mPaymentManager == null) {
                                mPaymentManager = PaymentManager(
                                    mContext,
                                    mSampleAppPartnerInfoHolder!!.partnerInfo
                                )
                                // Get Card List.
                            }
                        }
                        SpaySdk.SPAY_NOT_SUPPORTED, SpaySdk.SPAY_NOT_READY, SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> mBinding!!.samsungPayButton.setVisibility(
                            View.INVISIBLE
                        )
                        else -> mBinding!!.samsungPayButton.visibility = View.INVISIBLE
                    }
                    showOnSuccessLog(status, bundle) // Print log
                    showOnSuccessMessage(status, bundle) // Print messages.
                }

                override fun onFail(errorCode: Int, bundle: Bundle) {
                    mBinding!!.samsungPayButton.visibility = View.INVISIBLE
                    showOnFailLogAndMessage(errorCode, bundle) // Print log and messages.
                }
            })
        } catch (e: NullPointerException) {
            Log.e(
                TAG, e.message!!
            )
        }
    }

    fun getBrandList(): ArrayList<SpaySdk.Brand>? {
        val brandList = ArrayList<SpaySdk.Brand>()
        if (mBinding!!.cardBrandControl.visaBrand.isChecked) {
            brandList.add(SpaySdk.Brand.VISA)
        }
        if (mBinding!!.cardBrandControl.masterBrand.isChecked) {
            brandList.add(SpaySdk.Brand.MASTERCARD)
        }
        if (mBinding!!.cardBrandControl.amexBrand.isChecked) {
            brandList.add(SpaySdk.Brand.AMERICANEXPRESS)
        }
        if (mBinding!!.cardBrandControl.discoverBrand.isChecked) {
            brandList.add(SpaySdk.Brand.DISCOVER)
        }
        return brandList
    }

    private fun makeBundleWithRequestFilter(): Bundle? {
        val filter = Bundle()
        filter.putParcelableArrayList(PaymentManager.EXTRA_KEY_CARD_BRAND_FILTER, getBrandList())
        return filter
    }

    private fun doActivateSamsungPay() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.activateSamsungPay()
    }

    private fun doUpdateSamsungPay() {
        val samsungPay = SamsungPay(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.goToUpdatePage()
    }

    private fun requestCardInfoMethodTest() {
        if (mPaymentManager == null) {
            mPaymentManager = PaymentManager(mContext, mSampleAppPartnerInfoHolder!!.partnerInfo)
        }
        mPaymentManager!!.requestCardInfo(makeBundleWithRequestFilter(), cardInfoListener)
    }

    val cardInfoListener: CardInfoListener = object : CardInfoListener {

        override fun onResult(cardResponse: List<CardInfo>) {
            var numVISA = 0
            var numMastercard = 0
            var numAMEX = 0
            var numDiscover = 0
            var brandStrings = "Cards : "
            if (cardResponse != null) {
                var brand: SpaySdk.Brand?
                for (i in cardResponse.indices) {
                    brand = cardResponse[i].brand
                    when (brand) {
                        SpaySdk.Brand.AMERICANEXPRESS -> numAMEX++
                        SpaySdk.Brand.MASTERCARD -> numMastercard++
                        SpaySdk.Brand.VISA -> numVISA++
                        SpaySdk.Brand.DISCOVER -> numDiscover++
                        else -> {}
                    }
                }
            }
            brandStrings += "  VI = $numVISA,  MC = $numMastercard,  AX = $numAMEX,  DS = $numDiscover"
            Log.d(
                TAG, "cardInfoListener onResult  : $brandStrings"
            )
            displayToastMessageIfRequired(brandStrings)
        }

        /*
         * This callback is received when the card information cannot be retrieved.
         * For example, when SDK service in the Samsung Pay app dies abnormally.
         */
        override fun onFailure(errorCode: Int, errorData: Bundle) {
            Log.d(
                TAG, "cardInfoListener onFailure errorCode : $errorCode"
            )
            displayToastMessageIfRequired("onFailure requestCardInfo()")
        }
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
        // Get BrandList (supported card brands)
        val brandList = getBrandList()
        val customSheetPaymentInfo: CustomSheetPaymentInfo
        val customSheetPaymentInfoBuilder = CustomSheetPaymentInfo.Builder()
        val extraPaymentInfo = Bundle()
        val selectCryptogramType =
            mBinding!!.cardBrandControl.cryptogramTypeSpinner.selectedItem.toString()
        if (!TextUtils.equals(selectCryptogramType, "NONE")) {
            extraPaymentInfo.putString(SpaySdk.EXTRA_CRYPTOGRAM_TYPE, selectCryptogramType)
        }
        if (mBinding!!.cardBrandControl.requestComboCard.isChecked) {
            extraPaymentInfo.putBoolean(SpaySdk.EXTRA_ACCEPT_COMBO_CARD, true)
        }
        if (mBinding!!.cardBrandControl.requestCpfCard.isChecked) {
            extraPaymentInfo.putBoolean(SpaySdk.EXTRA_REQUIRE_CPF, true)
        }
        customSheetPaymentInfoBuilder.setAddressInPaymentSheet(mRequestAddressOptions!!.requestAddressType)
        customSheetPaymentInfo = customSheetPaymentInfoBuilder
            .setMerchantId("")
            .setMerchantName(mSampleAppPartnerInfoHolder!!.sampleAppName)
            .setOrderNumber(mBinding!!.amountDetails.orderNo.text.toString())
            .setAddressInPaymentSheet(mRequestAddressOptions!!.requestAddressType)
            .setAllowedCardBrands(brandList)
            .setCardHolderNameEnabled(mBinding!!.cardBrandControl.displayCardHolderName.isChecked)
            .setCustomSheet(makeUpCustomSheet())
            .setExtraPaymentInfo(extraPaymentInfo)
            .build()
        return customSheetPaymentInfo
    }

    private fun makeUpCustomSheet(): CustomSheet? {

        /*
         * This callback is received when Controls are updated from Samsung Pay.
         */
        val sheetUpdatedListener = SheetUpdatedListener { controlId: String, sheet: CustomSheet? ->
            Log.d(TAG, "onResult control id : $controlId")
            updateControlId(controlId, sheet!!)
        }

        /*
           * Make SheetControls you want and add to custom sheet.
           * Each SheetControl is located in sequence.
           * There must be a AmountBoxControl and it must be located on last.
           */
        val customSheet = CustomSheet()
        if (mBillingAddressControls!!.needBillingControl()) {
            customSheet.addControl(mBillingAddressControls!!.makeBillingAddress(sheetUpdatedListener))
        }
        if (mShippingAddressControls!!.needSendShippingControl()) {
            customSheet.addControl(
                mShippingAddressControls!!.makeShippingAddress(
                    sheetUpdatedListener
                )
            )
        }
        if (mBinding!!.extraControl.plainTextPreDefinedCheckBox.isChecked) {
            val plainTextControl =
                PlainTextControl(PLAIN_TEXT_PRE_DEFINED_EXAMPLE_ID)
            plainTextControl.setText(
                mContext!!.getString(R.string.plain_text_pre_defined_title),
                mContext!!.getString(R.string.plain_text_pre_defined_message)
            )
            customSheet.addControl(plainTextControl)
        }
        if (mBinding!!.extraControl.plainTextEditableCheckBox.isChecked) {
            val plainTextControl =
                PlainTextControl(PLAIN_TEXT_EDITABLE_EXAMPLE_ID)
            plainTextControl.setText(
                mBinding!!.extraControl.plainTextWithTitleCheckboxTitleEditBox.text.toString(),
                mBinding!!.extraControl.plainTextWithTitleCheckboxTextEditBox.text.toString()
            )
            customSheet.addControl(plainTextControl)
        }
        if (mShippingAddressControls!!.needSendShippingControl()) {
            customSheet.addControl(
                mShippingAddressControls!!.makeShippingMethodSpinnerControl(
                    sheetUpdatedListener
                )
            )
            updateControlId(SHIPPING_METHOD_SPINNER_ID, customSheet)
        }
        customSheet.addControl(mAmountDetailControls!!.makeAmountControl())
        return customSheet
    }

    private fun updateControlId(controlId: String, sheet: CustomSheet) {
        Log.d(
            TAG, "updateSheet : $controlId"
        )
        when (controlId) {
            BILLING_ADDRESS_ID ->                 // Call updateSheet with AmountBoxControl. This is mandatory
                receivedBillingAddress(controlId, sheet)
            SHIPPING_ADDRESS_ID ->                 // Call updateSheet with AmountBoxControl. This is mandatory
                receivedShippingAddress(controlId, sheet)
            SHIPPING_METHOD_SPINNER_ID -> receivedShippingMethodSpinner(controlId, sheet)
            else -> Log.e(
                TAG, "sheetUpdatedListener default called:"
            )
        }
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
        var addedShippingAmount = 0.0
        if (mShippingAddressControls!!.needSendShippingControl()) {
            Log.d(
                TAG,
                "onResult receivedShippingMethodSpinner : " + shippingMethodSpinnerControl.selectedItemId
            )
            when (shippingMethodSpinnerControl.selectedItemId) {
                SHIPPING_METHOD_1 -> addedShippingAmount = 0.0
                SHIPPING_METHOD_2 -> addedShippingAmount = 0.1
                SHIPPING_METHOD_3 -> addedShippingAmount = 0.2
                else -> {}
            }
            mAmountDetailControls!!.setAddedShippingAmount(addedShippingAmount)
        }
        updateSheetToSdk(mAmountDetailControls!!.updateAmountControl(sheet))
    }

    private val transactionListener: CustomSheetTransactionInfoListener =
        object : CustomSheetTransactionInfoListener {
            // This callback is received when the user changes card on the custom payment sheet in Samsung Pay.
            override fun onCardInfoUpdated(selectedCardInfo: CardInfo, customSheet: CustomSheet) {
                Log.d(TAG, "onCardInfoUpdated $selectedCardInfo")
                displayToastMessageIfRequired("onCardInfoUpdated")
                mAmountDetailControls!!.updateAmount()
                mAmountDetailControls!!.updateAmountControl(customSheet)
                if (mBinding!!.extraControl.updateSheetWithMessageCheckbox.isChecked) {
                    updateSheetWithCustomErrorMessageToSdk(
                        customSheet,
                        PaymentManager.CUSTOM_MESSAGE,
                        mBinding!!.extraControl.customErrorMessageEditText.text.toString(),
                        "onCardInfoUpdated"
                    )
                } else {
                    updateSheetToSdk(customSheet)
                }
            }

            override fun onSuccess(
                response: CustomSheetPaymentInfo, paymentCredential: String,
                extraPaymentData: Bundle
            ) {
                Log.d(TAG, "Transaction : onSuccess $extraPaymentData")
                val fragmentActivity: Activity = this@SamsungPaymentActivity
                if (fragmentActivity == null || fragmentActivity.isFinishing || fragmentActivity.isDestroyed) {
                    return
                }
                val mPaymentResultDialog = PaymentResultDialog(this@SamsungPaymentActivity)
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

    protected fun updateSheetWithCustomErrorMessageToSdk(
        sheet: CustomSheet?,
        errorCode: Int,
        customErrorMessage: String,
        reason: String
    ) {
        Handler().postDelayed({
            try {
                Log.d(TAG, "updateSheetWithCustomErrorMessageToSdk - reason : $reason")
                Log.d(TAG, "$errorCode CUSTOM_MESSAGE : $customErrorMessage")
                mPaymentManager!!.updateSheet(
                    mAmountDetailControls!!.updateAmountControl(sheet),
                    errorCode,
                    customErrorMessage
                )
            } catch (e: java.lang.IllegalStateException) {
                //Service is disconnected.
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                e.printStackTrace()
            }
        }, 0)
    }

    protected fun receivedBillingAddress(updatedControlId: String?, sheet: CustomSheet) {
        val addressControl = sheet.getSheetControl(updatedControlId) as AddressControl
        if (addressControl == null) {
            Log.e(TAG, "receivedBillingAddress addressControl  : null ")
            return
        }
        val billAddress = addressControl.address
        val errorCode = mBillingAddressControls!!.validateBillingAddress(billAddress)
        addressControl.errorCode = errorCode
        sheet.updateControl(addressControl)
        val needCustomErrorMessage = mBillingAddressControls!!.needCustomErrorMessage()
        Log.d(
            TAG,
            "onResult receivedBillingAddress  errorCode: $errorCode, customError: $needCustomErrorMessage"
        )
        if (needCustomErrorMessage) {
            updateSheetWithCustomErrorMessageToSdk(
                sheet, PaymentManager.CUSTOM_MESSAGE, mBillingAddressControls!!.customErrorMessage,
                "receivedShippingAddress"
            )
        } else {
            updateSheetToSdk(mAmountDetailControls!!.updateAmountControl(sheet))
        }
    }

    protected fun receivedShippingAddress(updatedControlId: String?, sheet: CustomSheet) {
        val addressControl = sheet.getSheetControl(updatedControlId) as AddressControl
        if (addressControl == null) {
            Log.e(TAG, "receivedShippingAddress addressControl  : null ")
            return
        }
        val shippingAddress = addressControl.address
        val errorCode = mShippingAddressControls!!.validateShippingAddress(shippingAddress)
        addressControl.errorCode = errorCode
        sheet.updateControl(addressControl)
        val needCustomErrorMessage = mShippingAddressControls!!.needCustomErrorMessage()
        Log.d(
            TAG,
            "onResult receivedShippingAddress  errorCode: $errorCode, customError: $needCustomErrorMessage"
        )
        if (needCustomErrorMessage) {
            updateSheetWithCustomErrorMessageToSdk(
                sheet, PaymentManager.CUSTOM_MESSAGE, mShippingAddressControls!!.customErrorMessage,
                "receivedShippingAddress"
            )
        } else {
            updateSheetToSdk(mAmountDetailControls!!.updateAmountControl(sheet))
        }

    }

    private fun enablePayButton() {
        mBinding!!.samsungPayButton.isClickable = true
        mBinding!!.samsungPayButton.alpha = 1.0f
    }

    private fun disablePayButton() {
        mBinding!!.samsungPayButton.isClickable = false
        mBinding!!.samsungPayButton.alpha = 0.4f
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

    private fun runWithTryCatch(runnable: Runnable) {
        try {
            runnable.run()
        } catch (e: Exception) {
            enablePayButton()
            displayToastMessageIfRequired(TAG + "NullPointerException, All mandatory fields cannot be null.")
            Log.e(TAG, e.message!!)
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
