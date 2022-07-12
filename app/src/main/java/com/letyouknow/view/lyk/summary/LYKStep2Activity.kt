package com.letyouknow.view.lyk.summary

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityLykStep2Binding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.alertPaymentError
import com.letyouknow.utils.AppGlobal.Companion.arState
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_URL
import com.letyouknow.utils.Constant.Companion.ARG_MSRP_RANGE
import com.letyouknow.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_PRODUCT
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.utils.CreditCardNumberTextWatcher
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.signup.CardListAdapter
import com.letyouknow.view.spinneradapter.DeliveryPreferenceAdapter
import com.letyouknow.view.spinneradapter.RebateDiscAdapter
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import com.letyouknow.view.ucd.submitdealsummary.SubmitDealSummaryActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentAuthConfig
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayPaymentMethodLauncher
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.activity_lyk_step2.*
import kotlinx.android.synthetic.main.dialog_deal_progress_bar.*
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_card_google_samsung.*
import kotlinx.android.synthetic.main.dialog_rebate_disc.*
import kotlinx.android.synthetic.main.layout_dealer_shipping_info.*
import kotlinx.android.synthetic.main.layout_lyk_step2.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.*


class LYKStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, ApiResultCallback<PaymentIntentResult>,
    CompoundButton.OnCheckedChangeListener {
    lateinit var binding: ActivityLykStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer

    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var dataPendingDeal: SubmitPendingUcdData
    private lateinit var lykDollarViewModel: LYKDollarViewModel
    private lateinit var promoCodeViewModel: PromoCodeViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodViewModel
    private lateinit var buyerViewModel: BuyerViewModel
    private lateinit var submitDealViewModel: SubmitDealViewModel
    private lateinit var tokenModel: RefreshTokenViewModel

    private lateinit var arImage: ArrayList<String>

    private var isTimeOver = false

    private var isFirst60 = true
    private var isStripe = true
    private var isGooglePay = false
    private var isSamsungPay = false
    private var isShowSamsungPay = false
    private var state = "NC"
    private var imageId = "0"

    private lateinit var calculateTaxViewModel: CalculateTaxViewModel
    private lateinit var rebateViewModel: RebateViewModel
    private lateinit var rebateResetViewModel: RebateResetViewModel
    private lateinit var rebateListViewModel: RebateListViewModel
    private lateinit var rebateCheckedViewModel: RebateCheckedViewModel

    private var dollar = 0.0

    private var isFirstName = false
    private var isMiddleName = false
    private var isLastName = false
    private var isAddress1 = false
    private var isCity = false
    private var isZipCode = false
    private var isState = false
    private var isBuyerEmail = false
    private var isBuyerMNo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyk_step2)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_lyk_step2)
        init()
    }

    private fun init() {
        lykDollarViewModel = ViewModelProvider(this)[LYKDollarViewModel::class.java]
        promoCodeViewModel = ViewModelProvider(this)[PromoCodeViewModel::class.java]
        paymentMethodViewModel = ViewModelProvider(this)[PaymentMethodViewModel::class.java]
        buyerViewModel = ViewModelProvider(this)[BuyerViewModel::class.java]
        submitDealViewModel = ViewModelProvider(this)[SubmitDealViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        calculateTaxViewModel = ViewModelProvider(this)[CalculateTaxViewModel::class.java]
        rebateViewModel = ViewModelProvider(this)[RebateViewModel::class.java]
        rebateResetViewModel = ViewModelProvider(this)[RebateResetViewModel::class.java]
        rebateListViewModel = ViewModelProvider(this)[RebateListViewModel::class.java]
        rebateCheckedViewModel = ViewModelProvider(this)[RebateCheckedViewModel::class.java]

        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(ARG_UCD_DEAL_PENDING) && intent.hasExtra(
                ARG_IMAGE_URL
            )
            && intent.hasExtra(
                Constant.ARG_IMAGE_ID
            ) && intent.hasExtra(
                Constant.ARG_MSRP_RANGE
            )
        ) {
            imageId = intent.getStringExtra(Constant.ARG_IMAGE_ID)!!
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            dataPendingDeal = Gson().fromJson(
                intent.getStringExtra(ARG_UCD_DEAL_PENDING),
                SubmitPendingUcdData::class.java
            )
            arImage = Gson().fromJson(
                intent.getStringExtra(ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            binding.data = yearModelMakeData
            binding.pendingUcdData = dataPendingDeal
            binding.pendingUCDShippingData = dataPendingDeal
            binding.lightDealBindData = lightBindData

            binding.isStripe = isStripe
            binding.isGooglePay = isGooglePay
            binding.isSamsungPay = isSamsungPay
            binding.isShowSamsungPay = isShowSamsungPay

            binding.isStripe = true
            binding.selectState = state
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }
//            callRefreshTokenApi()
            callDollarAPI()

            if (dataPendingDeal.buyer?.phoneNumber?.contains("(") == false)
                edtPhoneNumber.setText(AppGlobal.formatPhoneNo(dataPendingDeal.buyer?.phoneNumber))
            else
                edtPhoneNumber.setText(dataPendingDeal.buyer?.phoneNumber)

            if (imageId == "0") {
                ll360.visibility = View.GONE
                llGallery.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(intent.getStringExtra(ARG_MSRP_RANGE))) {
                tvPriceMSRP.text = intent.getStringExtra(ARG_MSRP_RANGE)
            } else {
                tvPriceMSRP.visibility = View.GONE
            }
        }
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber, tvErrorCardNumber)
        edtCardNumber.addTextChangedListener(textWatcher)

        initCardAdapter()
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvHavePromoCode.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        btnProceedDeal.setOnClickListener(this)
        tvApplyPromo.setOnClickListener(this)
//        ivEdit.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ll360.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        tvRebatesDisc.setOnClickListener(this)
        llCreditCard.setOnClickListener(this)
        llAndroidPay.setOnClickListener(this)
        llSamsungPay.setOnClickListener(this)
        btnGooglePayProceedDeal.setOnClickListener(this)

        setState()

        edtPhoneNumber.filters =
            arrayOf<InputFilter>(
                filterPhoneNo(edtPhoneNumber),
                InputFilter.LengthFilter(13)
            )//        backButton()

        onStateChange()
        initPayment()
        setOnChangeCard()
        setEmojiOnEditText()
        edtZipCode.inputType = InputType.TYPE_CLASS_NUMBER
        edtCardZipCode.inputType = InputType.TYPE_CLASS_NUMBER
        initLiveGoogle()
        checkEmptyData()

        callRebateListAPI()
        setDeliveryOptions()
    }

    private fun setEmojiOnEditText() {
        AppGlobal.setEmojiKeyBoard(edtGiftCard)
        AppGlobal.setEmojiKeyBoard(edtFirstName)
        AppGlobal.setEmojiKeyBoard(edtLastName)
        AppGlobal.setEmojiKeyBoard(edtAddress1)
        AppGlobal.setEmojiKeyBoard(edtAddress2)
        AppGlobal.setEmojiKeyBoard(edtCity)
        AppGlobal.setEmojiKeyBoard(edtMiddleName)

        AppGlobal.setEmojiKeyBoard(edtShippingFirstName)
        AppGlobal.setEmojiKeyBoard(edtShippingLastName)
        AppGlobal.setEmojiKeyBoard(edtShippingAddress1)
        AppGlobal.setEmojiKeyBoard(edtShippingAddress2)
        AppGlobal.setEmojiKeyBoard(edtShippingCity)
        AppGlobal.setEmojiKeyBoard(edtShippingMiddleName)
    }

    private lateinit var stripe: Stripe

    private fun initPayment() {
        stripe =
            Stripe(this, Objects.requireNonNull(getString(R.string.stripe_publishable_key)))
    }

    private fun onStateChange() {
        onTextChangeFirstName1(edtFirstName, tvErrorFirstName)
        onTextChangeMiddleName1(edtMiddleName)
        onTextChangeLastName1(edtLastName, tvErrorLastName)
        onTextChangeAddress11(edtAddress1, tvErrorAddress1)
        onTextChangeEmail(edtEmail, tvErrorEmailAddress)
        onTextChangePhoneNo(edtPhoneNumber, tvErrorPhoneNo)
        Constant.onTextChange(this, edtAddress2, tvErrorAddress2)
        onTextChangeCity1(edtCity, tvErrorCity)
        onTextChangeZipCode(edtZipCode, tvErrorZipCode)

        edtGiftCard.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.isNotEmpty()) {
                    tvApplyPromo.isEnabled = true
                    tvApplyPromo.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                } else {
                    tvApplyPromo.isEnabled = false
                    tvApplyPromo.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.color88898A))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    private lateinit var adapterState: StateSpinnerAdapter
    private fun setState() {
        adapterState = StateSpinnerAdapter(
            this,
            arState
        )
//        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterState
        spState.onItemSelectedListener = this

        for (i in 0 until arState.size) {
            if (arState[i] == dataPendingDeal.buyer?.state) {
                spState.setSelection(i)
            }
        }
    }


    private fun callDollarAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            lykDollarViewModel.getDollar(this, dataPendingDeal.dealID)!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    if (data == "0.00") {
                        llDollar.visibility = View.GONE
                    }
                    tvDollar.text =
                        "-" + NumberFormat.getCurrencyInstance(Locale.US).format(data.toFloat())
                    binding.dollar = data.toFloat()
                    dollar = data.toDouble()
                    callCalculateTaxAPI()
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callBuyerAPI() {
        if (Constant.isOnline(this)) {

            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.buyerId] = dataPendingDeal.buyer?.buyerId!!
            map[ApiConstant.firstName] = edtFirstName.text.toString().trim()
            map[ApiConstant.middleName] = edtMiddleName.text.toString().trim()
            map[ApiConstant.lastName] = edtLastName.text.toString().trim()
            map[ApiConstant.phoneNumber] = edtPhoneNumber.text.toString()
            map[ApiConstant.email] = edtEmail.text.toString().trim()
            map[ApiConstant.addressId] = dataPendingDeal.buyer?.addressId!!
            map[ApiConstant.address1] = edtAddress1.text.toString().trim()
            map[ApiConstant.address2] = edtAddress2.text.toString().trim()
            map[ApiConstant.city] = edtCity.text.toString().trim()
            map[ApiConstant.state] = state
            map[ApiConstant.zipcode] = edtZipCode.text.toString().trim()
            map[ApiConstant.country] = "US"
            map[ApiConstant.ShipToFirstName] =
                if (isShipping()) edtShippingFirstName.text.toString().trim() else ""
            map[ApiConstant.ShipToMiddleName] =
                if (isShipping()) edtShippingMiddleName.text.toString().trim() else ""
            map[ApiConstant.ShipToLastName] =
                if (isShipping()) edtShippingLastName.text.toString().trim() else ""
            map[ApiConstant.ShipToPhoneNumber] =
                if (isShipping()) edtShippingPhoneNumber.text.toString().trim() else ""
            map[ApiConstant.ShipToEmail] =
                if (isShipping()) edtShippingEmail.text.toString().trim() else ""
            map[ApiConstant.ShipToAddress1] =
                if (isShipping()) edtShippingAddress1.text.toString().trim() else ""
            map[ApiConstant.ShipToAddress2] =
                if (isShipping()) edtShippingAddress2.text.toString().trim() else ""
            map[ApiConstant.ShipToCity] =
                if (isShipping()) edtShippingCity.text.toString().trim() else ""
            map[ApiConstant.ShipToState] = if (isShipping()) shippingState else ""
            map[ApiConstant.ShipToZipcode] =
                if (isShipping()) edtShippingZipCode.text.toString().trim() else ""
            map[ApiConstant.ShipToCountry] = "US"
            map[ApiConstant.ShipIt] = isShipping()
            Log.e("buyer req", Gson().toJson(map))
            buyerViewModel.buyerCall(this, map)!!
                .observe(
                    this
                ) { data ->
//                    Constant.dismissLoader()
                    if (TextUtils.isEmpty(data.buyerId)) {
                        alertError("Something went wrong. Please try again later.")
                    } else {
                        callSubmitDealAPI(false)
                    }
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isShipping(): Boolean {
        return spDeliveryPreference.selectedItemPosition == 1
    }

    fun alertError(message: String?) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_error)
        dialog.run {
            tvErrorMessage.text = message
            tvErrorOk.setOnClickListener {
                startActivity(intentFor<MainActivity>().clearTask().newTask())
            }
        }
        AppGlobal.setLayoutParam(dialog)
        dialog.show()
    }


    private fun callSubmitDealAPI(isStripe: Boolean) {
        if (Constant.isOnline(this)) {

            showProgressDialog()
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.dealID] = dataPendingDeal.dealID!!
            map[ApiConstant.buyerID] = dataPendingDeal.buyer?.buyerId!!
            if (isStripe)
                map[ApiConstant.payment_intent_id] = paymentIntentId
            else {
                map[ApiConstant.payment_method_id] = cardStripeData.id!!
                map[ApiConstant.card_last4] = cardStripeData.card?.last4!!
                map[ApiConstant.card_brand] = cardStripeData.card?.brand!!
            }
            map[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            map[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            map[ApiConstant.price] = yearModelMakeData.price!!
            map[ApiConstant.timeZoneOffset] = getTimeZoneOffset()
            map[ApiConstant.zipCode] = yearModelMakeData.zipCode!!
            map[ApiConstant.searchRadius] =
                if (yearModelMakeData.radius!! == "ALL") "6000" else yearModelMakeData.radius!!.replace(
                    " mi",
                    ""
                )
            map[ApiConstant.loanType] = yearModelMakeData.loanType!!
            map[ApiConstant.initial] = yearModelMakeData.initials!!
            map[ApiConstant.promotionID] = yearModelMakeData.promotionId!!
            val arJsonPackage = JsonArray()
            for (i in 0 until yearModelMakeData.arPackages?.size!!) {
                arJsonPackage.add(yearModelMakeData.arPackages!![i].vehiclePackageID)
            }
            val arJsonAccessories = JsonArray()
            for (i in 0 until yearModelMakeData.arOptions?.size!!) {
                arJsonAccessories.add(yearModelMakeData.arOptions!![i].dealerAccessoryID)
            }

            map[ApiConstant.vehiclePackageIDs] = arJsonPackage
            map[ApiConstant.dealerAccessoryIDs] = arJsonAccessories
            Log.e("Request Deal", Gson().toJson(map))
            Log.e("submit Deal Req", Gson().toJson(map))
            Log.e("Submit Deal Request", Gson().toJson(map))
            submitDealViewModel.submitDealCall(this, map)!!
                .observe(
                    this
                ) { data ->
                    Log.e("Submit LYK resp", Gson().toJson(data))
                    Log.e("resp", Gson().toJson(data))
//                    Constant.dismissLoader()

                    if (data?.foundMatch!! && !data.isBadRequest!!) {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        data.successResult?.transactionInfo?.vehiclePromoCode =
                            yearModelMakeData.discount
                        data.successResult?.transactionInfo?.vehiclePrice =
                            yearModelMakeData.price!!
                        data.successResult?.transactionInfo?.remainingBalance =
                            (yearModelMakeData.price!! - (799.0f + yearModelMakeData.discount!!))
                        //    Log.e("SubmitStep2Response", Gson().toJson(data))
                        startActivity<SubmitDealSummaryActivity>(
                            Constant.ARG_SUBMIT_DEAL to Gson().toJson(
                                data
                            ),
                            ARG_TYPE_PRODUCT to "LetYouKnow",
                            Constant.ARG_CAL_TAX_DATA to Gson().toJson(calculateTaxData)
                        )
                        finish()

                    } else if (data.foundMatch && data.isBadRequest!!) {
                        var msgStr = ""
                        var isFirst = true

                        for (i in 0 until data?.messageList?.size!!) {
                            if (isFirst) {
                                isFirst = false
                                msgStr = data?.messageList[i]!!
                            } else {
                                msgStr = msgStr + ",\n" + data?.messageList[i]!!
                            }
                        }
                        if (!TextUtils.isEmpty(msgStr))
                            AppGlobal.alertError(
                                this,
                                msgStr
                            )
                    } else if (data.paymentResponse != null) {
                        if (!data.foundMatch && !data.isBadRequest!! && data.paymentResponse?.hasError!!) {
                            if (!TextUtils.isEmpty(data.paymentResponse.errorMessage))
                                AppGlobal.alertCardError(
                                    this,
                                    data.paymentResponse.errorMessage
                                )
                            if (!isGooglePay && !isSamsungPay)
                                setClearCardData()
                        } else if (!data.foundMatch && !data.paymentResponse?.hasError!!) {
                            if (data.paymentResponse.requires_action!!) {
                                initStripe(data.paymentResponse.payment_intent_client_secret!!)
                            } else {
                                startActivity<LYKNegativeActivity>(
                                    ARG_YEAR_MAKE_MODEL to Gson().toJson(
                                        yearModelMakeData
                                    ),
                                    ARG_IMAGE_ID to imageId,
                                    ARG_IMAGE_URL to Gson().toJson(arImage),
                                    ARG_SUBMIT_DEAL to Gson().toJson(
                                        data
                                    ),
                                    ARG_UCD_DEAL_PENDING to Gson().toJson(
                                        dataPendingDeal
                                    ),
                                    ARG_MSRP_RANGE to tvPriceMSRP.text.toString().trim(),
                                )
                                finish()
                            }
                        }
                    } else {
                        startActivity<LYKNegativeActivity>(
                            ARG_YEAR_MAKE_MODEL to Gson().toJson(
                                yearModelMakeData
                            ),
                            ARG_IMAGE_ID to imageId,
                            ARG_IMAGE_URL to Gson().toJson(arImage),
                            ARG_SUBMIT_DEAL to Gson().toJson(
                                data
                            ), ARG_UCD_DEAL_PENDING to Gson().toJson(
                                dataPendingDeal
                            ),
                            ARG_MSRP_RANGE to tvPriceMSRP.text.toString().trim()
                        )
                        finish()
                    }
                    dialogProgress.dismiss()
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClearCardData() {
        edtCardZipCode.setText("")
        edtCardNumber.setText("")
        edtCVV.setText("")
        edtExpiresDate.setText("")
    }

    private lateinit var cardStripeData: CardStripeData
    private fun callPaymentMethodAPI(isPayment: Boolean) {
        pref?.setPaymentToken(true)
        if (Constant.isOnline(this)) {
            if (!isPayment) {
                if (!Constant.isInitProgress()) {
                    Constant.showLoader(this)
                } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.showLoader(this)
                }
            }

            paymentMethodViewModel.callPayment(
                this,
                "card",
                edtCardNumber.text.toString().trim(),
                edtCVV.text.toString().trim(),
                edtExpiresDate.text.toString().trim().substring(0, 2),
                edtExpiresDate.text.toString().trim().substring(3, 5),
                edtCardZipCode.text.toString().trim(),
                "ab09ffc0-f83e-4ecc-b198-eb375bfbbc57b41768",
                "c25c4e63-970f-4c89-b9e1-9c983c4a99224e8f02",
                "a7908fc4-1b17-44f4-801c-0d404cee2f1ad7ad95",
                "899065",
                "pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA"
            )!!
                .observe(
                    this
                ) { data ->
                    cardStripeData = data
                    if (isPayment)
                        callBuyerAPI()
                    else
                        Constant.dismissLoader()
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callPromoCodeAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            promoCodeViewModel.getPromoCode(
                this,
                edtGiftCard.text.toString().trim(),
                dataPendingDeal.dealID
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    if (data.discount!! > 0) {
                        tvPromoData.visibility = View.VISIBLE
                        tvPromoData.text =
                            "-${NumberFormat.getCurrencyInstance(Locale.US).format(data.discount)}"
                        yearModelMakeData.discount = data.discount!!
                        yearModelMakeData.promotionId = data.promotionID!!
                        binding.data = yearModelMakeData
                        callCalculateTaxAPI()
                    } else {
                        tvPromoData.visibility = View.GONE
                        yearModelMakeData.discount = 0.0f
                        binding.data = yearModelMakeData
                        if (data.promotionID == "-1") {
                            tvErrorPromo.text = getString(R.string.enter_promo_code_is_not_valid)
                        } else {
                            tvErrorPromo.text =
                                getString(R.string.promo_code_cannot_be_applied_due_to_negative_balance)
                        }
                        tvErrorPromo.visibility = View.VISIBLE
                        callCalculateTaxAPI()
                    }
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }


    private fun cancelTimer() {
        if (cTimer != null)
            cTimer.cancel()
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
//        cancelTimer()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
//        cancelTimer()
    }

    override fun onStop() {
        super.onStop()
//        cancelTimer()
    }

    private fun initCardAdapter() {
        adapterCardList = CardListAdapter(R.layout.list_item_card, this)
        rvCard.adapter = adapterCardList

        if (pref?.getCardList()!!.size != 0) {
            llCardList.visibility = View.VISIBLE
            arCardList = pref?.getCardList()!!
            adapterCardList.addAll(arCardList)
//            llCardViewDetail.visibility = View.GONE
            for (i in 0 until arCardList.size) {
                if (arCardList[i].isSelect!!) {
                    selectCardPos = i
                }
            }
        } else {
            llCardList.visibility = View.GONE
//            llCardViewDetail.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvApplyPromo -> {
                tvErrorPromo.visibility = View.GONE
                callPromoCodeAPI()
            }
            R.id.tvViewOptions -> {
                popupOption()
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.cardMain -> {
                val pos = v.tag as Int
                if (selectCardPos != -1) {
                    val data = adapterCardList.getItem(selectCardPos)
                    data.isSelect = false
                    adapterCardList.update(selectCardPos, data)
                }

                val data = adapterCardList.getItem(pos)
                data.isSelect = true
                adapterCardList.update(pos, data)

                selectCardPos = pos;
            }
            R.id.llDebitCreditCard -> {
                selectPaymentType = 1
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.llPayPal -> {
                selectPaymentType = 2
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.llBankAccount -> {
                selectPaymentType = 3
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.tvHavePromoCode -> {
                lightBindData.isShowPromo = !lightBindData.isShowPromo!!
                binding.lightDealBindData = lightBindData
            }
            R.id.tvAddMore -> {
//                llCardViewDetail.visibility = View.VISIBLE
            }
            R.id.btnSave -> {
                val card = cardInputWidget.cardParams
                if (card == null) {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.invalid_card_data),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val cardSourceParams = SourceParams.createCardParams(card!!)

                stripe.createSource(
                    cardSourceParams,
                    callback = object : ApiResultCallback<Source> {
                        override fun onSuccess(source: Source) {
                            //  Log.e("Success", Gson().toJson(source))
//                            callPaymentMethodAPI(card)
                        }

                        override fun onError(error: Exception) {
                            //   Log.e("error", Gson().toJson(error))
                        }
                    }
                )
            }
            R.id.ivBackDeal -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
            R.id.btnProceedDeal -> {
                setErrorVisible()
                if (isGooglePay || isSamsungPay) {
                    if (!TextUtils.isEmpty(cardStripeData.id)) {
                        if (spDeliveryPreference.selectedItemPosition == 1 && !chkSameAsBuyer.isChecked) {
                            if (isValidShipping() && isValid()) {
                                callPaymentMethodAPI(true)
                            }
                        } else {
                            if (isValid()) {
                                callBuyerAPI()
                            }
                        }
                    } else {
                        alertPaymentError(this, "Select Proper Card")
                    }
                } else {
                    if (spDeliveryPreference.selectedItemPosition == 1 && !chkSameAsBuyer.isChecked) {
                        if (isValidCard()) {
                            if (isValidShipping() && isValid()) {
                                callPaymentMethodAPI(true)
                            }
                        }
                    } else {
                        if (isValidCard()) {
                            if (isValid()) {
                                callPaymentMethodAPI(true)
                            }
                        }
                    }
                }
            }
            R.id.btnGooglePayProceedDeal -> {
                setErrorVisible()
                if (isGooglePay || isSamsungPay) {
                    if (!TextUtils.isEmpty(cardStripeData.id)) {
                        if (spDeliveryPreference.selectedItemPosition == 1 && !chkSameAsBuyer.isChecked) {
                            if (isValidShipping() && isValid()) {
                                callPaymentMethodAPI(true)
                            }
                        } else {
                            if (isValid()) {
                                callBuyerAPI()
                            }
                        }
                    } else {
                        alertPaymentError(this, "Select Proper Card")
                    }
                }
            }
            R.id.llGallery -> {
                startActivity<Gallery360TabActivity>(
                    Constant.ARG_TYPE_VIEW to 0,
                    Constant.ARG_IMAGE_ID to imageId
                )
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(
                    Constant.ARG_TYPE_VIEW to 2,
                    Constant.ARG_IMAGE_ID to imageId
                )
            }
            R.id.llCreditCard -> {
                isStripe = true
                isGooglePay = false
                isSamsungPay = false
                setGoogleSamsung()
            }
            R.id.llAndroidPay -> {
                isStripe = false
                isGooglePay = true
                isSamsungPay = false
                setGoogleSamsung()
                onClickGooglePayment()
            }
            R.id.llSamsungPay -> {
                isStripe = false
                isGooglePay = false
                isSamsungPay = true
                setGoogleSamsung()
            }
            R.id.tvRebatesDisc -> {
                if (arRebate.isNullOrEmpty()) {
                    AppGlobal.alertError(
                        this,
                        getString(R.string.no_rebates_found, " " + yearModelMakeData.zipCode)
                    )
                } else {
                    strRebate = Gson().toJson(arRebate)
                    dialogRebate.show()
                }
            }

            R.id.llRebate -> {
                val pos = v.tag as Int
                val data = adapterRebateDisc.getItem(pos)
                data.isSelect = !data.isSelect!!
                adapterRebateDisc.update(pos, data)
                callCheckedRebateAPI()
            }
            R.id.tvCancelRebate -> {
                val type: Type = object : TypeToken<ArrayList<RebateListData>?>() {}.type
                arRebate = Gson().fromJson(strRebate, type)
                for (i in 0 until arRebate.size) {
                    adapterRebateDisc.update(i, arRebate[i])
                }
                dialogRebate.dismiss()
            }
            R.id.tvResetRebate -> {
                callRebateResetAPI()

            }
            R.id.tvApplyRebate -> {
                val arData = adapterRebateDisc.getAll()
                val arFilter = arData.filter { data -> data.isSelect == true }
                if (arFilter.isNullOrEmpty()) {
                    showApplyEmptyDialog()
                } else {
                    callApplyRebateAPI()
                }
            }
        }
    }

    private fun setGoogleSamsung() {
        binding.isStripe = isStripe
        binding.isGooglePay = isGooglePay
        binding.isSamsungPay = isSamsungPay
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    private fun filterPhoneNo(edtPhoneNumber: EditText): InputFilter {
        var filter = InputFilter { source, start, end, dest, dstart, dend ->
            var source = source

            if (source.length > 0) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (source.toString().length > 1) {
                        val number = source.toString()
                        val digits1 = number.toCharArray()
                        val digits2 = number.split("(?<=.)").toTypedArray()
                        source = digits2[digits2.size - 1]
                    }
                    if (edtPhoneNumber.text.toString().length < 1) {
                        return@InputFilter "($source"
                    } else if (edtPhoneNumber.text.toString().length > 1 && edtPhoneNumber.text.toString()
                            .length <= 3
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 3 && edtPhoneNumber.text.toString()
                            .length <= 5
                    ) {
                        val isContain = dest.toString().contains(")")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            ")$source"
                        }
                    } else if (edtPhoneNumber.text.toString().length > 5 && edtPhoneNumber.text.toString()
                            .length <= 7
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 7) {
                        val isContain = dest.toString().contains("-")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            "-$source"
                        }
                    }
                }
            } else {
            }
            source
        }
        return filter
    }

    private fun isValid(): Boolean {
        when {
            edtCardZipCode.text.toString()
                .trim().length != 5 && (!isGooglePay && !isSamsungPay) -> {
                tvErrorCardZip.visibility = View.VISIBLE
                tvErrorCardZip.text = getString(R.string.zipcode_must_be_valid_digits)
                return false
            }

            TextUtils.isEmpty(edtFirstName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                tvErrorFirstName.text = getString(R.string.first_name_required)
                return false
            }
            (Constant.firstNameValidator(edtFirstName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                tvErrorFirstName.text = getString(R.string.enter_valid_first_name)
                return false
            }
            TextUtils.isEmpty(edtLastName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                tvErrorLastName.text = getString(R.string.last_name_required)
                return false
            }
            (Constant.lastNameValidator(edtLastName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                tvErrorLastName.text = getString(R.string.enter_valid_last_name)
                return false
            }
            TextUtils.isEmpty(edtEmail.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_email_address_vali)
                Constant.setErrorBorder(edtEmail, tvErrorEmailAddress)
                return false
            }
            !Constant.emailValidator(edtEmail.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_valid_email)
                Constant.setErrorBorder(edtEmail, tvErrorEmailAddress)
                return false
            }
            TextUtils.isEmpty(edtAddress1.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_addressline1)
                Constant.setErrorBorder(edtAddress1, tvErrorAddress1)
                return false
            }
            edtAddress1.text.toString().trim().length < 3 -> {
                tvErrorEmailAddress.text =
                    getString(R.string.address1_must_be_minimum_three_characters)
                Constant.setErrorBorder(edtEmail, tvErrorEmailAddress)
                return false
            }

            TextUtils.isEmpty(edtCity.text.toString().trim()) -> {
                Constant.setErrorBorder(edtCity, tvErrorCity)
                tvErrorCity.text = getString(R.string.city_required)
                return false
            }
            (Constant.cityValidator(edtCity.text.toString().trim())) -> {
                Constant.setErrorBorder(edtCity, tvErrorCity)
                tvErrorCity.text = getString(R.string.enter_valid_City)
                return false
            }

            TextUtils.isEmpty(edtPhoneNumber.text.toString().trim()) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                return false
            }

            (edtPhoneNumber.text.toString().length != 13) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
                return false
            }
            state == "State" -> {
                tvErrorState.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                return false
            }
            (edtZipCode.text.toString().length != 5) -> {
                Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                tvErrorZipCode.text = getString(R.string.enter_valid_zipcode)
                return false
            }
            else -> return true
        }
    }


    private fun popupOption() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_option_accessories)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            yearModelMakeData.run {
                tvDialogTitle.text =
                    vehicleYearStr + " " + vehicleMakeStr + " " + vehicleModelStr + " " + vehicleTrimStr
                tvDialogExteriorColor.text = vehicleExtColorStr
                tvDialogInteriorColor.text = vehicleIntColorStr
                var accessoriesStr = ""
                var isFirstAcce = true
                val arAccId: ArrayList<String> = ArrayList()
                for (i in 0 until arOptions?.size!!) {

                    arAccId.add(arOptions!![i].dealerAccessoryID!!)
                    if (isFirstAcce) {
                        isFirstAcce = false
                        accessoriesStr = arOptions!![i].accessory!!
                    } else
                        accessoriesStr += ",\n" + arOptions!![i].accessory!!

                }
                var packageStr = ""
                var isFirstPackage = true

                for (i in 0 until arPackages?.size!!) {
                    if (isFirstPackage) {
                        isFirstPackage = false
                        packageStr = arPackages!![i].packageName!!
                    } else {
                        packageStr = packageStr + ",\n" + arPackages!![i].packageName!!
                    }
                }
                tvDialogPackage.text = packageStr
                tvDialogOptions.text = accessoriesStr
            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    override fun onItemSelected(v: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (v?.id) {
            R.id.spState -> {
                val data = adapterState.getItem(position) as String
                state = data
                binding.selectState = state
                isState = true
                callCalculateTaxAPI()
            }
            R.id.spShippingState -> {
                val data = adapterShippingState.getItem(position) as String
                shippingState = data
            }
            R.id.spDeliveryPreference -> {
                val data = adapterDeliveryPref.getItem(position) as String
                deliveryPrefStr = data
                chkSameAsBuyer.isChecked = true
                binding.isShowShippingCheckBox = position != 0
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(this, request)!!.observe(this, { data ->
                Constant.dismissLoader()
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                callDollarAPI()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setErrorVisible() {
        tvErrorFirstName.visibility = View.GONE
        tvErrorLastName.visibility = View.GONE
        tvErrorAddress1.visibility = View.GONE
        tvErrorAddress2.visibility = View.GONE
        tvErrorCity.visibility = View.GONE
        tvErrorState.visibility = View.GONE
        tvErrorZipCode.visibility = View.GONE
        tvErrorPhoneNo.visibility = View.GONE
        tvErrorEmailAddress.visibility = View.GONE
    }

    private fun setOnChangeCard() {
        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtExpiresDate.text.toString().length
                if (inputLength == 5 && edtExpiresDate.isDateValid) {
                    tvErrorCardDate.visibility = View.GONE
                    edtCVV.requestFocus()
                } else {
                    tvErrorCardDate.visibility = View.VISIBLE
                    tvErrorCardDate.text = getString(R.string.bt_expiration_invalid)
                }
            }
        })
        edtCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtCVV.text.toString().length
                if (inputLength < 3) {
                    tvErrorCardCVV.visibility = View.VISIBLE
                    tvErrorCardCVV.text = getString(R.string.cvv_must_be_valid_digits)
                } else if (inputLength == 3) {
                    tvErrorCardCVV.visibility = View.GONE
                    edtCardZipCode.requestFocus()
                }
            }
        })
        edtCardZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtCardZipCode.text.toString().length
                if (isValidCard()) {
                    when {
                        inputLength == 5 -> {
                            tvErrorCardZip.visibility = View.GONE
                            if (isValidCard()) {
                                callPaymentMethodAPI(false)
                            }
                        }
                        inputLength < 5 -> {
                            tvErrorCardZip.visibility = View.VISIBLE
                            tvErrorCardZip.text = getString(R.string.zipcode_must_be_valid_digits)
                        }
                        else -> {
                            tvErrorCardZip.visibility = View.GONE
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(edtCardZipCode.text.toString().trim()))
                        edtCardZipCode.setText("")
                }
            }
        })
    }

    private fun isValidCard(): Boolean {
        when {
            TextUtils.isEmpty(edtCardNumber.text.toString().trim()) -> {
                tvErrorCardNumber.visibility = View.VISIBLE
                tvErrorCardNumber.text = getString(R.string.enter_card_number)
                edtCardNumber.requestFocus()
                return false
            }
            (tvErrorCardNumber.visibility == View.VISIBLE) -> {
                edtCardNumber.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtExpiresDate.text.toString().trim()) -> {
                tvErrorCardDate.visibility = View.VISIBLE
                tvErrorCardDate.text = getString(R.string.bt_expiration_required)
                edtExpiresDate.requestFocus()
                return false
            }
            tvErrorCardDate.visibility == View.VISIBLE -> {
                edtExpiresDate.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtCVV.text.toString().trim()) -> {
                tvErrorCardCVV.visibility = View.VISIBLE
                tvErrorCardCVV.text = getString(R.string.cvv_required)
                edtCVV.requestFocus()
                return false
            }
            tvErrorCardCVV.visibility == View.VISIBLE -> {
                edtCVV.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtCardZipCode.text.toString().trim()) -> {
                tvErrorCardZip.visibility = View.VISIBLE
                tvErrorCardZip.text = getString(R.string.zip_code_required)
                edtCardZipCode.requestFocus()
                return false
            }
        }
        return true
    }

    private fun initStripe(key: String) {
        stripe = Stripe(this, getString(R.string.stripe_publishable_key))
        val uiCustomization =
            PaymentAuthConfig.Stripe3ds2UiCustomization.Builder.createWithAppTheme(this)
                .setLabelCustomization(
                    PaymentAuthConfig.Stripe3ds2LabelCustomization.Builder()
                        .setTextFontSize(12)
                        .build()
                )
                .build()
        PaymentAuthConfig.init(
            PaymentAuthConfig.Builder()
                .set3ds2Config(
                    PaymentAuthConfig.Stripe3ds2Config.Builder()
                        .setTimeout(5)
                        .setUiCustomization(uiCustomization)
                        .build()
                )
                .build()
        )
        stripe.handleNextActionForPayment(this@LYKStep2Activity, key)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Log.e("Stripe", "requestCode" + requestCode)
        stripe.onPaymentResult(requestCode, data, this@LYKStep2Activity)
    }

    override fun onError(e: Exception) {
        //  Log.e("PaymentFailed", Gson().toJson(e).toString())
        AppGlobal.alertCardError(
            this,
            getString(R.string.we_are_unable_to_authenticate_your_payment)
        )
        setClearCardData()
    }

    private var paymentIntentId = ""
    override fun onSuccess(result: PaymentIntentResult) {
        val paymentIntent: PaymentIntent = result.intent
        when (paymentIntent.status) {
            StripeIntent.Status.Succeeded -> {
                val gson = GsonBuilder().setPrettyPrinting().create()
                Log.e("completed", gson.toJson(paymentIntent))
                paymentIntentId = paymentIntent.id!!
                callSubmitDealAPI(true)
            }
            StripeIntent.Status.RequiresPaymentMethod -> {
                AppGlobal.alertCardError(
                    this,
                    getString(R.string.we_are_unable_to_authenticate_your_payment)
                )
                setClearCardData()
            }
            StripeIntent.Status.Canceled -> {
                AppGlobal.alertCardError(
                    this,
                    getString(R.string.we_are_unable_to_authenticate_your_payment)
                )
                setClearCardData()
            }
            StripeIntent.Status.Processing -> {
                if (!TextUtils.isEmpty(paymentIntent.id)) {
                    paymentIntentId = paymentIntent.id!!
                    callSubmitDealAPI(true)
                } else {
                    Toast.makeText(this, "Payment Processing", Toast.LENGTH_LONG).show()
                }
            }
            StripeIntent.Status.RequiresConfirmation -> {
                if (!TextUtils.isEmpty(paymentIntent.id)) {
                    paymentIntentId = paymentIntent.id!!
                    callSubmitDealAPI(true)
                } else {
                    Toast.makeText(this, "Requires Payment Confirmation", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private var a = 0
    private val handlerDealPro: Handler = Handler()
    private lateinit var dialogProgress: Dialog
    private fun showProgressDialog() {
        dialogProgress = Dialog(this, R.style.FullScreenDialog)
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogProgress.setCancelable(false)
        dialogProgress.setContentView(R.layout.dialog_deal_progress_bar)
        setProgressData(dialogProgress)
        setLayoutParam(dialogProgress)
        dialogProgress.show()
    }

    private fun setProgressData(dialogProgress: Dialog) {
        a = dialogProgress.proBar.progress
        Thread {
            while (a < 100) {
                a += 1
                handlerDealPro.post(Runnable {
                    dialogProgress.proBar.progress = a
                    dialogProgress.tvProgressPr.text = a.toString() + "%"
                    if (a == 100) {
                        // responseDealSuccess(dialogProgress,data)
                    }
                })
                try {
                    // Sleep for 50 ms to show progress you can change it as well.
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private var calculateTaxData = CalculateTaxData()
    private fun callCalculateTaxAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            calculateTaxViewModel.getCalculateTax(
                this,
                yearModelMakeData.price!!.toDouble(),
                yearModelMakeData.discount!!.toDouble(),
                dollar,
                state
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    if (calculateTaxData.estimatedRebates != 0.0)
                        data.estimatedRebates = calculateTaxData.estimatedRebates
                    binding.taxData = data
                    calculateTaxData = data
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmptyData() {
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.firstName)) {
            isFirstName = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.middleName)) {
            isMiddleName = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.lastName)) {
            isLastName = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.address1)) {
            isAddress1 = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.city)) {
            isCity = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.zipcode)) {
            isZipCode = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.phoneNumber)) {
            isBuyerMNo = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.email)) {
            isBuyerEmail = true
        }
        if (!TextUtils.isEmpty(dataPendingDeal.buyer?.state)) {
            isState = true
        }
    }

    private fun onTextChangeFirstName1(
        edtText: EditText,
        errorText: TextView,
    ) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    if (Constant.firstNameValidator(str)) {
                        Constant.setErrorBorder(edtText, errorText)
                        errorText.text = getString(R.string.enter_valid_first_name)
                        errorText.visibility = View.VISIBLE
                    } else {
                        edtText.setBackgroundResource(R.drawable.bg_edittext)
                        errorText.visibility = View.GONE
                    }
                    isFirstName = true
                    setDisableVar()
                } else {
                    isFirstName = false
                    setDisableVar()
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangeMiddleName1(edtText: EditText) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*  val str = s?.toString()
                  if (str?.length!! >= 0) {
                      isMiddleName = true
                      setDisableVar()
                  } else {
                      isMiddleName = false
                      setDisableVar()
                  }*/

            }

            override fun afterTextChanged(s: Editable?) {
                val str = edtText.text.toString().trim()

                if (s != null) {
                    if (str.length > 0) {
                        if (!str.substring(s.length - 1)
                                .isDigitsOnly() && Constant.middleNameValidatorText(str)
                        ) {
                            if (s.length > 1) {
                                edtText.setText(s.subSequence(1, s.length))
                                edtText.setSelection(1)
                            }
                        } else {
                            edtText.setText("")
                        }
                        /*  isMiddleName = true
                          setDisableVar()*/
                    } else {
                        /*isMiddleName = false
                        setDisableVar()*/
                    }
                }
            }

        })
    }

    private fun onTextChangeLastName1(
        edtText: EditText,
        errorText: TextView
    ) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    if (Constant.lastNameValidator(str)) {
                        Constant.setErrorBorder(edtText, errorText)
                        errorText.text = getString(R.string.enter_valid_last_name)
                        errorText.visibility = View.VISIBLE
                    } else {
                        edtText.setBackgroundResource(R.drawable.bg_edittext)
                        errorText.visibility = View.GONE
                    }
                    isLastName = true
                    setDisableVar()
                } else {
                    isLastName = false
                    setDisableVar()
                    //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangeAddress11(
        edtText: EditText,
        errorText: TextView
    ) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    if (str.length == 0) {
                        Constant.setErrorBorder(edtText, errorText)
                        errorText.text = getString(R.string.enter_addressline1)
                    } else if (str.length > 1 && str.length < 3) {
                        Constant.setErrorBorder(edtText, errorText)
                        errorText.text =
                            getString(R.string.address1_must_be_minimum_three_characters)
                        errorText.visibility = View.VISIBLE
                    } else {
                        edtText.setBackgroundResource(R.drawable.bg_edittext)
                        errorText.visibility = View.GONE
                    }
                    isAddress1 = true
                    setDisableVar()
                    //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                } else {
                    isAddress1 = false
                    setDisableVar()
                    Constant.setErrorBorder(edtText, errorText)
                    errorText.text = getString(R.string.enter_addressline1)
                    //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangeCity1(
        edtText: EditText,
        errorText: TextView
    ) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    if (Constant.cityValidator(str)) {
                        Constant.setErrorBorder(edtText, errorText)
                        errorText.text = getString(R.string.enter_valid_City)
                        errorText.visibility = View.VISIBLE
                    } else {
                        edtText.setBackgroundResource(R.drawable.bg_edittext)
                        errorText.visibility = View.GONE
                    }
                    isCity = true
                    setDisableVar()
                } else {
                    isCity = false
                    setDisableVar()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangeEmail(edtText: EditText, errorText: TextView) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    edtText.setBackgroundResource(R.drawable.bg_edittext)
                    errorText.visibility = View.GONE
                    isBuyerEmail = true
                    setDisableVar()
                } else {
                    isBuyerEmail = false
                    setDisableVar()
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangePhoneNo(edtText: EditText, errorText: TextView) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    edtText.setBackgroundResource(R.drawable.bg_edittext)
                    errorText.visibility = View.GONE
                    isBuyerMNo = true
                    setDisableVar()
                } else {
                    isBuyerMNo = false
                    setDisableVar()
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onTextChangeZipCode(edtText: EditText, errorText: TextView) {
        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s?.toString()
                if (str?.length!! > 0) {
                    edtText.setBackgroundResource(R.drawable.bg_edittext)
                    errorText.visibility = View.GONE
                    isZipCode = true
                    setDisableVar()
                } else {
                    isZipCode = false
                    setDisableVar()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    fun setDisableVar() {
        if (isFirstName && isLastName && isAddress1 && isCity && isBuyerEmail && isBuyerMNo && isZipCode && isState) {
            tvRebatesDisc.isEnabled = true
            tvRebatesDisc.setBackgroundResource(R.drawable.bg_button)
        } else {
            tvRebatesDisc.isEnabled = false
            tvRebatesDisc.setBackgroundResource(R.drawable.bg_button_disable)
        }
    }

    private lateinit var adapterRebateDisc: RebateDiscAdapter
    private lateinit var dialogRebate: Dialog
    private var arRebate: ArrayList<RebateListData> = ArrayList()
    private var strRebate = ""

    private fun dialogRebateDisc(arData: ArrayList<RebateListData>) {
        dialogRebate = Dialog(this, R.style.FullScreenDialog)
        dialogRebate.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogRebate.setCancelable(false)
        dialogRebate.setCanceledOnTouchOutside(false)
        dialogRebate.setContentView(R.layout.dialog_rebate_disc)
        dialogRebate.run {
            adapterRebateDisc =
                RebateDiscAdapter(R.layout.list_item_rebate_disc, this@LYKStep2Activity)
            dialogRebate.rvRebate.adapter = adapterRebateDisc
            adapterRebateDisc.addAll(arData)

            tvCancelRebate.setOnClickListener(this@LYKStep2Activity)
            tvResetRebate.setOnClickListener(this@LYKStep2Activity)
            tvApplyRebate.setOnClickListener(this@LYKStep2Activity)
        }
        setLayoutParam(dialogRebate)
    }


    private fun showApplyEmptyDialog() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_inventory_availability)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }


    //rebate api
    private fun callApplyRebateAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val jsonRebate = JsonArray()
            if (::adapterRebateDisc.isInitialized) {
                for (i in 0 until adapterRebateDisc.itemCount) {
                    if (adapterRebateDisc.getItem(i).isSelect!! || adapterRebateDisc.getItem(i).isOtherSelect!!) {
                        jsonRebate.add(adapterRebateDisc.getItem(i).rebateId)
                    }
                }
            }
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.Zipcode1] = yearModelMakeData.zipCode!!
            map[ApiConstant.VehicleYearId1] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.VehicleMakeId1] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.VehicleModelId1] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.VehicleTrimId1] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"
            map[ApiConstant.RebateList] = jsonRebate
            map[ApiConstant.priceBid] = yearModelMakeData.price!!.toDouble()
            map[ApiConstant.promocodeDiscount] = yearModelMakeData.discount!!.toDouble()
            map[ApiConstant.lykDollars] = dollar
            map[ApiConstant.stateAbbr] = state
            rebateViewModel.rebateApi(
                this,
                map
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    binding.taxData = data
                    calculateTaxData = data
                    strRebate = Gson().toJson(arRebate)
                    dialogRebate.dismiss()
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    //reset rebate Reset api
    private fun callRebateResetAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val jsonRebate = JsonArray()
            for (i in 0 until adapterRebateDisc.itemCount) {
                if (adapterRebateDisc.getItem(i).isSelect!! || adapterRebateDisc.getItem(i).isOtherSelect!!) {
                    jsonRebate.add(adapterRebateDisc.getItem(i).rebateId)
                }
            }
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.Zipcode1] = yearModelMakeData.zipCode!!
            map[ApiConstant.VehicleYearId1] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.VehicleMakeId1] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.VehicleModelId1] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.VehicleTrimId1] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"
            map[ApiConstant.RebateList] = jsonRebate
            map[ApiConstant.priceBid] = yearModelMakeData.price!!.toDouble()
            map[ApiConstant.promocodeDiscount] = yearModelMakeData.discount!!.toDouble()
            map[ApiConstant.lykDollars] = dollar
            map[ApiConstant.stateAbbr] = state
            rebateResetViewModel.rebateResetApi(
                this,
                map
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    binding.taxData = data
                    calculateTaxData = data
                    strRebate = Gson().toJson(adapterRebateDisc.getAll())
                    for (i in 0 until arRebate.size) {
                        arRebate[i].isSelect = false
                        arRebate[i].isOtherSelect = false
                        arRebate[i].isGray = false
                        adapterRebateDisc.update(i, arRebate[i])
                    }
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    //rebate list api
    private fun callRebateListAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.Zipcode1] = yearModelMakeData.zipCode!!
            map[ApiConstant.VehicleYearId1] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.VehicleMakeId1] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.VehicleModelId1] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.VehicleTrimId1] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"

            rebateListViewModel.rebateListApi(
                this,
                map
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    arRebate = ArrayList()
                    if (data.isNotEmpty()) {
                        arRebate = data
                        dialogRebateDisc(data)
                    }
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    //checked rebate api
    private fun callCheckedRebateAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

            val jsonRebate = JsonArray()
            for (i in 0 until adapterRebateDisc.itemCount) {
                if (adapterRebateDisc.getItem(i).isSelect!! || adapterRebateDisc.getItem(i).isOtherSelect!!) {
                    jsonRebate.add(adapterRebateDisc.getItem(i).rebateId)
                }
            }
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.Zipcode1] = yearModelMakeData.zipCode!!
            map[ApiConstant.VehicleYearId1] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.VehicleMakeId1] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.VehicleModelId1] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.VehicleTrimId1] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"
            map[ApiConstant.RebateList] = jsonRebate
            rebateCheckedViewModel.rebateCheckApi(
                this,
                map
            )!!
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    if (!data.autoCheckList.isNullOrEmpty()) {
                        for (i in 0 until data.autoCheckList.size) {
                            for (j in 0 until adapterRebateDisc.itemCount) {
                                if (adapterRebateDisc.getItem(j).rebateId == data.autoCheckList[i]) {
                                    val dataCheck = adapterRebateDisc.getItem(j)
                                    dataCheck.isGray = false
                                    dataCheck.isOtherSelect = true
                                    adapterRebateDisc.update(j, dataCheck)
                                }
                            }
                        }
                    }
                    if (!data.grayOutList.isNullOrEmpty()) {
                        for (i in 0 until data.grayOutList.size) {
                            for (j in 0 until adapterRebateDisc.itemCount) {
                                if (adapterRebateDisc.getItem(j).rebateId == data.grayOutList[i]) {
                                    val dataGray = adapterRebateDisc.getItem(j)
                                    dataGray.isGray = true
                                    adapterRebateDisc.update(j, dataGray)
                                }
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private lateinit var adapterDeliveryPref: DeliveryPreferenceAdapter
    private var arDeliveryPref = arrayListOf("Pick up at dealer", "Ship it to me")
    private var deliveryPrefStr = "Pick up at dealer"

    private fun setDeliveryOptions() {
        setDeliveryPref()
        chkSameAsBuyer.isChecked = true
        binding.isCheck = true
        chkSameAsBuyer.setOnCheckedChangeListener(this)
        setShippingState()

        if (dataPendingDeal.buyer?.phoneNumber?.contains("(") == false)
            edtShippingPhoneNumber.setText(AppGlobal.formatPhoneNo(dataPendingDeal.buyer?.phoneNumber))
        else
            edtShippingPhoneNumber.setText(dataPendingDeal.buyer?.phoneNumber)
        edtShippingPhoneNumber.filters =
            arrayOf<InputFilter>(
                filterPhoneNo(edtShippingPhoneNumber),
                InputFilter.LengthFilter(13)
            )

        tvSaveShipping.setOnClickListener(this)
        onStateChangeShipping()
    }

    private fun setDeliveryPref() {
        adapterDeliveryPref = DeliveryPreferenceAdapter(
            this,
            arDeliveryPref
        )
        spDeliveryPreference.adapter = adapterDeliveryPref
        spDeliveryPreference.onItemSelectedListener = this
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.chkSameAsBuyer -> {
                binding.isCheck = isChecked
                if (!isChecked) {
                    binding.pendingUCDShippingData = dataPendingDeal
                    for (i in 0 until arState.size) {
                        if (arState[i] == dataPendingDeal.buyer?.state) {
                            spShippingState.setSelection(i)
                        }
                    }

                    edtShippingPhoneNumber.filters = arrayOf()
                    if (dataPendingDeal.buyer?.phoneNumber?.contains("(") == false)
                        edtShippingPhoneNumber.setText(AppGlobal.formatPhoneNo(dataPendingDeal.buyer?.phoneNumber))
                    else
                        edtShippingPhoneNumber.setText(dataPendingDeal.buyer?.phoneNumber)
                    edtShippingPhoneNumber.filters =
                        arrayOf<InputFilter>(
                            filterPhoneNo(edtShippingPhoneNumber),
                            InputFilter.LengthFilter(13)
                        )
                }
//                validShippingCheck()
            }
        }
    }

    private lateinit var adapterShippingState: StateSpinnerAdapter
    private var shippingState = "NC"
    private fun setShippingState() {
        adapterShippingState = StateSpinnerAdapter(
            this,
            arState
        )
        spShippingState.adapter = adapterShippingState
        spShippingState.onItemSelectedListener = this

        for (i in 0 until arState.size) {
            if (arState[i] == dataPendingDeal.buyer?.state) {
                spShippingState.setSelection(i)
            }
        }
    }

    private fun isValidShipping(): Boolean {
        when {
            TextUtils.isEmpty(edtShippingFirstName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtShippingFirstName, tvShippingErrorFirstName)
                tvShippingErrorFirstName.text = getString(R.string.first_name_required)
                return false
            }
            (Constant.firstNameValidator(edtShippingFirstName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtShippingFirstName, tvShippingErrorFirstName)
                tvShippingErrorFirstName.text = getString(R.string.enter_valid_first_name)
                return false
            }
            TextUtils.isEmpty(edtShippingLastName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtShippingLastName, tvShippingErrorLastName)
                tvShippingErrorLastName.text = getString(R.string.last_name_required)
                return false
            }
            (Constant.lastNameValidator(edtShippingLastName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtShippingLastName, tvShippingErrorLastName)
                tvShippingErrorLastName.text = getString(R.string.enter_valid_last_name)
                return false
            }
            TextUtils.isEmpty(edtShippingEmail.text.toString().trim()) -> {
                tvShippingErrorEmailAddress.text = getString(R.string.enter_email_address_vali)
                Constant.setErrorBorder(edtShippingEmail, tvShippingErrorEmailAddress)
                return false
            }
            !Constant.emailValidator(edtShippingEmail.text.toString().trim()) -> {
                tvShippingErrorEmailAddress.text = getString(R.string.enter_valid_email)
                Constant.setErrorBorder(edtShippingEmail, tvShippingErrorEmailAddress)
                return false
            }
            TextUtils.isEmpty(edtShippingAddress1.text.toString().trim()) -> {
                tvShippingErrorEmailAddress.text = getString(R.string.enter_addressline1)
                Constant.setErrorBorder(edtShippingAddress1, tvShippingErrorAddress1)
                return false
            }
            edtShippingAddress1.text.toString().trim().length < 3 -> {
                tvShippingErrorEmailAddress.text =
                    getString(R.string.address1_must_be_minimum_three_characters)
                Constant.setErrorBorder(edtShippingEmail, tvShippingErrorEmailAddress)
                return false
            }

            TextUtils.isEmpty(edtShippingCity.text.toString().trim()) -> {
                Constant.setErrorBorder(edtShippingCity, tvShippingErrorCity)
                tvShippingErrorCity.text = getString(R.string.city_required)
                return false
            }
            (Constant.cityValidator(edtShippingCity.text.toString().trim())) -> {
                Constant.setErrorBorder(edtShippingCity, tvShippingErrorCity)
                tvShippingErrorCity.text = getString(R.string.enter_valid_City)
                return false
            }

            TextUtils.isEmpty(edtShippingPhoneNumber.text.toString().trim()) -> {
                Constant.setErrorBorder(edtShippingPhoneNumber, tvShippingErrorPhoneNo)
                tvShippingErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                return false
            }

            (edtShippingPhoneNumber.text.toString().length != 13) -> {
                Constant.setErrorBorder(edtShippingPhoneNumber, tvShippingErrorPhoneNo)
                tvShippingErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
                return false
            }
            shippingState == "State" -> {
                tvShippingErrorState.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(edtShippingZipCode.text.toString().trim()) -> {
                Constant.setErrorBorder(edtShippingZipCode, tvShippingErrorZipCode)
                return false
            }
            (edtShippingZipCode.text.toString().length != 5) -> {
                Constant.setErrorBorder(edtShippingZipCode, tvShippingErrorZipCode)
                tvShippingErrorZipCode.text = getString(R.string.enter_valid_zipcode)
                return false
            }
            else -> return true
        }
    }

    private fun onStateChangeShipping() {
        Constant.onTextChangeFirstName(this, edtShippingFirstName, tvShippingErrorFirstName)
        Constant.onTextChangeMiddleName(this, edtShippingMiddleName)
        Constant.onTextChangeLastName(this, edtShippingLastName, tvShippingErrorLastName)
        Constant.onTextChangeAddress1(this, edtShippingAddress1, tvShippingErrorAddress1)
        Constant.onTextChange(this, edtShippingEmail, tvShippingErrorEmailAddress)
        Constant.onTextChange(this, edtShippingPhoneNumber, tvShippingErrorPhoneNo)
        Constant.onTextChange(this, edtShippingAddress2, tvShippingErrorAddress2)
        Constant.onTextChangeCity(this, edtShippingCity, tvShippingErrorCity)
        Constant.onTextChange(this, edtShippingZipCode, tvShippingErrorZipCode)
    }

    private lateinit var googlePayLauncher: GooglePayPaymentMethodLauncher
    private fun initLiveGoogle() {
        googlePayLauncher = GooglePayPaymentMethodLauncher(
            activity = this,
            config = GooglePayPaymentMethodLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "US",
                merchantName = "Widget Store",
                isEmailRequired = false,
                existingPaymentMethodRequired = false
            ),
            readyCallback = ::onGooglePayReady,
            resultCallback = ::onGooglePayResult
        )
    }

    private fun onClickGooglePayment() {
        googlePayLauncher.present(
            currencyCode = "USD",
            amount = 2500
        )
    }

    private fun onGooglePayReady(isReady: Boolean) {
        llAndroidPay.isEnabled = isReady
    }

    private fun onGooglePayResult(
        result: GooglePayPaymentMethodLauncher.Result
    ) {
        when (result) {
            is GooglePayPaymentMethodLauncher.Result.Completed -> {
                result.paymentMethod.id?.let { Log.e("PaymentId", it) }
                paymentIntentId = result.paymentMethod.id!!
                result.paymentMethod.card?.let {
                    cardStripeData = CardStripeData()
                    cardStripeData.id = paymentIntentId
                    cardStripeData.card?.last4 = result.paymentMethod.card?.last4
                    cardStripeData.card?.brand = result.paymentMethod.card?.brand?.name
                }
            }
            GooglePayPaymentMethodLauncher.Result.Canceled -> {
                // User canceled the operation
                Log.e("Canceled", "Canceled")
                alertPaymentError(this, getString(R.string.google_payment_canceled))
                cardStripeData = CardStripeData()
            }
            is GooglePayPaymentMethodLauncher.Result.Failed -> {
                result.error.message?.let { Log.e("Failed", it) }
                alertPaymentError(this, result.error.message)
                cardStripeData = CardStripeData()
                // Operation failed; inspect `result.error` for the exception
            }
        }
    }

}