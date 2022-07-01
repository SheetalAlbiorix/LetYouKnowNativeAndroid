package com.letyouknow.view.lcd.summary

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.*
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityLcdDealSummaryStep2Binding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.*
import com.letyouknow.utils.AppGlobal.Companion.alertCardError
import com.letyouknow.utils.AppGlobal.Companion.arState
import com.letyouknow.utils.AppGlobal.Companion.formatPhoneNo
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_URL
import com.letyouknow.utils.Constant.Companion.ARG_IS_SHOW_PER
import com.letyouknow.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import com.letyouknow.utils.Constant.Companion.ARG_SEL_TAB
import com.letyouknow.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_PRODUCT
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.letyouknow.utils.Constant.Companion.TYPE_ONE_DEAL_NEAR_YOU
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.lcd.negative.LCDNegativeActivity
import com.letyouknow.view.signup.CardListAdapter
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import com.letyouknow.view.ucd.submitdealsummary.SubmitDealSummaryActivity
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TransportEnum
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentAuthConfig
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import com.stripe.android.model.StripeIntent
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_lcd_deal_summary_step2.*
import kotlinx.android.synthetic.main.dialog_deal_progress_bar.*
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.dialog_leave_my_deal.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_lcd_deal_summary_step2.*
import kotlinx.android.synthetic.main.layout_toolbar_timer.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*


class LCDDealSummaryStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, ApiResultCallback<PaymentIntentResult> {
    lateinit var myReceiver: MyReceiver
    lateinit var binding: ActivityLcdDealSummaryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer

    private var seconds = 300
//    private var seconds = 10

    private lateinit var dataLCDDeal: FindLCDDeaData
    private lateinit var dataPendingDeal: SubmitPendingUcdData
    private lateinit var lykDollarViewModel: LYKDollarViewModel
    private lateinit var promoCodeViewModel: PromoCodeViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodViewModel
    private lateinit var buyerViewModel: BuyerViewModel
    private lateinit var submitDealLCDViewModel: SubmitDealLCDViewModel

    private lateinit var arImage: ArrayList<String>

    private var isTimeOver = false

    private var isFirst60 = true
    private var state = "NC"
    private var imageId = "0"

    private var arPer = arrayListOf("75%", "50%", " 25%", "0%")
    private lateinit var submitPendingLCDDealViewModel: SubmitPendingLCDDealViewModel
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel

    var hubConnection: HubConnection? = null

    var isPercentShow = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lcd_deal_summary_step2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_deal_summary_step2)
        init()
    }

    private fun init() {

        checkVehicleStockViewModel =
            ViewModelProvider(this)[CheckVehicleStockViewModel::class.java]
        lykDollarViewModel = ViewModelProvider(this)[LYKDollarViewModel::class.java]
        promoCodeViewModel = ViewModelProvider(this)[PromoCodeViewModel::class.java]
        paymentMethodViewModel = ViewModelProvider(this)[PaymentMethodViewModel::class.java]
        buyerViewModel = ViewModelProvider(this)[BuyerViewModel::class.java]
        submitDealLCDViewModel = ViewModelProvider(this)[SubmitDealLCDViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        submitPendingLCDDealViewModel =
            ViewModelProvider(this)[SubmitPendingLCDDealViewModel::class.java]

        if (intent.hasExtra(ARG_LCD_DEAL_GUEST) && intent.hasExtra(ARG_UCD_DEAL_PENDING) && intent.hasExtra(
                ARG_IMAGE_URL
            ) && intent.hasExtra(
                ARG_IMAGE_ID
            )
        ) {
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            dataLCDDeal = Gson().fromJson(
                intent.getStringExtra(ARG_LCD_DEAL_GUEST),
                FindLCDDeaData::class.java
            )
            dataPendingDeal = Gson().fromJson(
                intent.getStringExtra(ARG_UCD_DEAL_PENDING),
                SubmitPendingUcdData::class.java
            )
            arImage = Gson().fromJson(
                intent.getStringExtra(ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            binding.ucdData = dataLCDDeal
            binding.pendingUcdData = dataPendingDeal
            binding.pendingUCDShippingData = dataPendingDeal
            binding.lightDealBindData = lightBindData
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }
//            callRefreshTokenApi()
            callDollarAPI()
            if (dataPendingDeal.buyer?.phoneNumber?.contains("(") == false)
                edtPhoneNumber.setText(formatPhoneNo(dataPendingDeal.buyer?.phoneNumber))
            else
                edtPhoneNumber.setText(dataPendingDeal.buyer?.phoneNumber)

            if (imageId == "0") {
                llGallery.visibility = View.GONE
                ll360.visibility = View.GONE
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
        tvAddMin.setOnClickListener(this)
        tvApplyPromo.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
//        ivEdit.setOnClickListener(this)
        ivBack.setOnClickListener(this)

        // startTimer()
        setState()
        AppGlobal.strikeThrough(tvPrice)

        edtPhoneNumber.filters =
            arrayOf<InputFilter>(
                filterPhoneNo(edtPhoneNumber),
                InputFilter.LengthFilter(13)
            )//        backButton()
        onStateChange()
        initPayment()
        setOnChangeCard()
        initHub()
        setEmojiOnEditText()
        setPowerSaving()
        broadcastIntent()
        edtZipCode.inputType = InputType.TYPE_CLASS_NUMBER
        edtCardZipCode.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun broadcastIntent() {
        myReceiver = MyReceiver()
        registerReceiver(myReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    private fun setPowerSaving() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val powerSaveMode = powerManager.isPowerSaveMode
//            if (powerSaveMode) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
//            }
        }
    }

    private fun setEmojiOnEditText() {
        AppGlobal.setEmojiKeyBoard(edtGiftCard)
        AppGlobal.setEmojiKeyBoard(edtFirstName)
        AppGlobal.setEmojiKeyBoard(edtMiddleName)
        AppGlobal.setEmojiKeyBoard(edtLastName)
        AppGlobal.setEmojiKeyBoard(edtAddress1)
        AppGlobal.setEmojiKeyBoard(edtAddress2)
        AppGlobal.setEmojiKeyBoard(edtCity)
    }

    private fun initHub() {
        val token = pref?.getUserData()?.authToken
        hubConnection = HubConnectionBuilder.create(Constant.HUB_CONNECTION_URL)
            .withTransport(TransportEnum.LONG_POLLING)
            .withAccessTokenProvider(Single.defer {
                Single.just(
                    token
                )
            }).build()

        hubConnection?.start()?.subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                // Log.e("onSubscribe", "onSubscribe")
            }

            override fun onComplete() {
                // Log.e("onComplete", "onComplete")
                if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                    handleHub()
                }
            }

            override fun onError(e: Throwable) {
                //  Log.e("onError", "onError")
                initHub()
            }

        })
    }

    private fun handleHub() {
        //  Log.e("dealId", dataLCDDeal.dealID!!)
        //   Log.e("buyerId", pref?.getUserData()?.buyerId.toString())

        hubConnection?.invoke("SubscribeOnDeal", dataLCDDeal.dealID!!.toInt())!!

        hubConnection!!.on(
            "CantSubscribeOnDeal",
            { vehicleId, message ->  // OK
                try {
                    // Log.e("data", "$vehicleId: $message")
                } catch (e: Exception) {
                    //  Log.e("CantSubscribeOnDeal", e.message.toString())
                }
            },
            Any::class.java, Any::class.java
        )
        startTimerOn()

    }

    private fun startTimerOn() {
        hubConnection!!.on(
            "UpdateDealTimer",
            { dealId, timer: Double ->
                // OK
                this@LCDDealSummaryStep2Activity.runOnUiThread {
                    try {
                        //  Log.e("data", "$dealId: $timer")
                        /* val time = timer.toString()
                         seconds = time.toInt()*/
//                    timerVisible(timer.toInt())
                        seconds = timer.toInt()
                        tvTimer.text = (String.format("%02d", seconds / 60)
                                + ":" + String.format("%02d", seconds % 60))

                        if (seconds == 60 && isFirst60) {
                            Log.e("data", "aaaaaaa")
                            this@LCDDealSummaryStep2Activity.runOnUiThread {
                                Log.e("data", "aaaaaaa")
//                           binding.toolbarIsFirst60 = true
                                tvAddMin.visibility = View.VISIBLE
                                isFirst60 = false
                            }
                        }
                        if (seconds < 60) {
                            tvTimer.setTextColor(resources.getColor(R.color.colorB11D1D))
                            tvTimer.setBackgroundResource(R.drawable.bg_round_border_red)
                        } else {
                            tvTimer.setBackgroundResource(R.drawable.bg_round_border_blue)
                            tvTimer.setTextColor(resources.getColor(R.color.colorPrimary))
                        }
                        if (seconds == 0) {
                            this@LCDDealSummaryStep2Activity.runOnUiThread {
                                isPercentShow = true
                                setPer()
                                tvAddMin.visibility = View.GONE
                                tvTimer.visibility = View.GONE
                                llExpired.visibility = View.VISIBLE
                                isTimeOver = true
//                            tvSubmitStartOver.text = getString(R.string.try_again)
                            }
                            //  return
                        }
                    } catch (e: Exception) {
                        //  Log.e("UpdateDealTimer", e.message.toString())
                    }
                }
            },
            Any::class.java, Double::class.java
        )
    }


    private fun stopTimer() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            hubConnection?.invoke("StopTimer")
            removeHubConnection()
        }
    }

    private fun add2Min() {
        hubConnection?.invoke("Add2Minutes", dataLCDDeal.dealID!!.toInt())
    }

    private fun removeHubConnection() {
        try {
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                hubConnection?.invoke("StopTimer")
                hubConnection?.remove("UpdateDealTimer")
                hubConnection?.remove("UpdateVehicleState")
                hubConnection?.remove("CantSubscribeOnDeal")
                hubConnection?.close()
            }
        } catch (e: Exception) {

        }
    }

    private lateinit var stripe: Stripe

    private fun initPayment() {
        stripe =
            Stripe(this, Objects.requireNonNull(getString(R.string.stripe_publishable_key)))
    }

    private fun onStateChange() {
        Constant.onTextChangeFirstName(this, edtFirstName, tvErrorFirstName)
        Constant.onTextChangeMiddleName(this, edtMiddleName)
        Constant.onTextChangeLastName(this, edtLastName, tvErrorLastName)
        Constant.onTextChangeAddress1(this, edtAddress1, tvErrorAddress1)
        Constant.onTextChange(this, edtEmail, tvErrorEmailAddress)
        Constant.onTextChange(this, edtPhoneNumber, tvErrorPhoneNo)
//        Constant.onTextChange(this, edtAddress1, tvErrorAddress1)
        Constant.onTextChange(this, edtAddress2, tvErrorAddress2)
        Constant.onTextChangeCity(this, edtCity, tvErrorCity)
        Constant.onTextChange(this, edtZipCode, tvErrorZipCode)

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
            applicationContext,
            arState
        )
//        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterState

        for (i in 0 until arState.size) {
            if (arState[i] == dataPendingDeal.buyer?.state) {
                spState.setSelection(i)
            }
        }
        spState.onItemSelectedListener = this
    }

    private fun callDollarAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            lykDollarViewModel.getDollar(this, dataPendingDeal.dealID)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data == "0.00") {
                        llDollar.visibility = View.GONE
                    }
                    tvDollar.text =
                        "-" + NumberFormat.getCurrencyInstance(Locale.US).format(data.toFloat())
                    binding.dollar = data.toFloat()
                }
                )
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
            map[ApiConstant.phoneNumber] = dataPendingDeal.buyer?.phoneNumber!!
            map[ApiConstant.email] = edtEmail.text.toString().trim()
            map[ApiConstant.addressId] = dataPendingDeal.buyer?.addressId!!
            map[ApiConstant.address1] = edtAddress1.text.toString().trim()
            map[ApiConstant.address2] = edtAddress2.text.toString().trim()
            map[ApiConstant.city] = edtCity.text.toString().trim()
            map[ApiConstant.state] = state
            map[ApiConstant.zipcode] = edtZipCode.text.toString().trim()
            map[ApiConstant.country] = "US"

            buyerViewModel.buyerCall(this, map)!!
                .observe(this, { data ->
//                    Constant.dismissLoader()
                    if (TextUtils.isEmpty(data.buyerId)) {
                        alertError("Something went wrong. Please try again later.")
                    } else {
                        callSubmitDealLCDAPI(false)
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
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

    private var lcdSuccResponseData: SubmitDealLCDData = SubmitDealLCDData()
    private fun callSubmitDealLCDAPI(isStripe: Boolean) {
        if (Constant.isOnline(this)) {
            showProgressDialog()
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.dealID] = dataPendingDeal.dealID!!
            map[ApiConstant.buyerID] = dataPendingDeal.buyer?.buyerId!!
            if (isStripe) {
                map[ApiConstant.payment_intent_id] = paymentIntentId
            } else {
                map[ApiConstant.payment_method_id] = cardStripeData.id!!
                map[ApiConstant.card_last4] = cardStripeData.card?.last4!!
                map[ApiConstant.card_brand] = cardStripeData.card?.brand!!
            }
            map[ApiConstant.vehicleYearID] = dataLCDDeal.yearId!!
            map[ApiConstant.vehicleMakeID] = dataLCDDeal.makeId!!
            map[ApiConstant.vehicleModelID] = dataLCDDeal.modelId!!
            map[ApiConstant.vehicleTrimID] = dataLCDDeal.trimId!!
            map[ApiConstant.vehicleExteriorColorID] = dataLCDDeal.exteriorColorId!!
            map[ApiConstant.vehicleInteriorColorID] = dataLCDDeal.interiorColorId!!
            map[ApiConstant.price] = dataLCDDeal.price!!
            map[ApiConstant.timeZoneOffset] = getTimeZoneOffset()
            map[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
            map[ApiConstant.searchRadius] = "100"
            map[ApiConstant.loanType] = dataLCDDeal.loanType!!
            map[ApiConstant.promotionID] = dataLCDDeal.promotionId!!
            map[ApiConstant.initial] = dataLCDDeal.initial!!
//            map[ApiConstant.timeZoneOffset] = pendingUCDData.buyer?.buyerId!!
            map[ApiConstant.vehicleInventoryID] = dataLCDDeal.vehicleInventoryID!!
            map[ApiConstant.guestID] = dataLCDDeal.guestID!!
            val arJsonPackage = JsonArray()
//            arJsonPackage.add("0")
            for (i in 0 until dataLCDDeal.arPackageId.size) {
                arJsonPackage.add(dataLCDDeal.arPackageId[i])
            }
            val arJsonAccessories = JsonArray()
//            arJsonAccessories.add("0")
            for (i in 0 until dataLCDDeal.arAccessoriesId.size) {
                arJsonAccessories.add(dataLCDDeal.arAccessoriesId[i])
            }

            map[ApiConstant.dealerAccessoryIDs] = arJsonAccessories
            map[ApiConstant.vehiclePackageIDs] = arJsonPackage

            submitDealLCDViewModel.submitDealLCDCall(this, map)!!
                .observe(this, { data ->
//                    Constant.dismissLoader()
                    data.successResult?.transactionInfo?.vehiclePromoCode = dataLCDDeal.discount
                    data.successResult?.transactionInfo?.vehiclePrice = dataLCDDeal.price!!
                    data.successResult?.transactionInfo?.remainingBalance =
                        (dataLCDDeal.price!! - (799.0f + dataLCDDeal.discount!!))
                    //Log.e("data Deal", Gson().toJson(data))
                    if (!data.isDisplayedPriceValid!! && data.somethingWentWrong!!) {
                        removeHubConnection()
                        startActivity<LCDNegativeActivity>(
                            ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                            ARG_IMAGE_ID to imageId,
                            ARG_IMAGE_URL to Gson().toJson(arImage),
                            ARG_IS_SHOW_PER to isPercentShow
                        )
                        finish()
                    } else if (data.isDisplayedPriceValid && !data.somethingWentWrong!!) {
                        if (data.foundMatch!!) {
                            removeHubConnection()
                            clearOneDealNearData()
                            startActivity<SubmitDealSummaryActivity>(
                                ARG_SUBMIT_DEAL to Gson().toJson(
                                    data
                                ),
                                ARG_TYPE_PRODUCT to "LightningCarDeals"
                            )
                            finish()
                        } else if (!data.foundMatch && data.isBadRequest!! && !data.isDisplayedPriceValid && data.somethingWentWrong) {
                            removeHubConnection()
                            startActivity<LCDNegativeActivity>(
                                ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                                ARG_IMAGE_ID to imageId,
                                ARG_IMAGE_URL to Gson().toJson(arImage),
                                ARG_IS_SHOW_PER to isPercentShow
                            )
                            finish()
                        } else if (!data.foundMatch && !data.isBadRequest!! && data.paymentResponse?.hasError!!) {
                            if (!TextUtils.isEmpty(data.paymentResponse.errorMessage))
                                AppGlobal.alertCardError(
                                    this,
                                    data.paymentResponse.errorMessage
                                )
                            setClearCardData()
                        } else if (!data.foundMatch && !data.paymentResponse?.hasError!!) {
                            if (data.paymentResponse.requires_action!!)
                                initStripe(data.paymentResponse.payment_intent_client_secret!!)
                            else {
                                removeHubConnection()
                                startActivity<LCDNegativeActivity>(
                                    ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                                    ARG_IMAGE_ID to imageId,
                                    ARG_IMAGE_URL to Gson().toJson(arImage),
                                    ARG_IS_SHOW_PER to isPercentShow
                                )
                                finish()
                            }
                        }
                    }
                    dialogProgress?.dismiss()
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClearCardData() {
        edtCardNumber.setText("")
        edtCVV.setText("")
        edtCardZipCode.setText("")
        edtExpiresDate.setText("")
    }

    private fun clearOneDealNearData() {
        pref?.setOneDealNearYouData(Gson().toJson(PrefOneDealNearYouData()))
        pref?.setOneDealNearYou("")
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
                .observe(this, { data ->

                    cardStripeData = data
                    if (isPayment)
                        callBuyerAPI()
                    else
                        Constant.dismissLoader()
                }
                )
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
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data.discount!! > 0) {
                        tvPromoData.visibility = View.VISIBLE
                        tvPromoData.text =
                            "-${NumberFormat.getCurrencyInstance(Locale.US).format(data.discount)}"
                        dataLCDDeal.discount = data.discount!!
                        dataLCDDeal.promotionId = data.promotionID!!
                        binding.ucdData = dataLCDDeal
                    } else {
                        tvPromoData.visibility = View.GONE
                        dataLCDDeal.discount = 0.0f
                        binding.ucdData = dataLCDDeal
                        if (data.promotionID == "-1") {
                            tvErrorPromo.text = getString(R.string.enter_promo_code_is_not_valid)
                        } else {
                            tvErrorPromo.text =
                                getString(R.string.promo_code_cannot_be_applied_due_to_negative_balance)
                        }
                        tvErrorPromo.visibility = View.VISIBLE
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    private fun startTimer() {
        cTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds -= 1
                tvTimer.text = (String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60))
                if (seconds == 60 && isFirst60) {
                    tvAddMin.visibility = View.VISIBLE
                    isFirst60 = false
                } else if (seconds < 60) {
                    tvTimer.setTextColor(resources.getColor(R.color.colorB11D1D))
                    tvTimer.setBackgroundResource(R.drawable.bg_round_border_red)
                } else {
                    tvTimer.setBackgroundResource(R.drawable.bg_round_border_blue)
                    tvTimer.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                if (seconds == 0 || seconds == 1) {
                    tvAddMin.visibility = View.GONE
                    tvTimer.visibility = View.GONE
                    llExpired.visibility = View.VISIBLE
                    isTimeOver = true
                    tvSubmitStartOver.text = getString(R.string.try_again)
                    cancelTimer()
                    setPer()
                    return
                }
            }

            override fun onFinish() {
            }

        }.start()
    }

    private lateinit var handlerPer: Handler
    private var countPer = 0

    private fun setPer() {
        handlerPer = Handler()
        handlerPer.postDelayed(runnablePer, 30000)
    }

    private var runnablePer = object : Runnable {
        override fun run() {
            tvPerc.text = arPer[countPer]
            countPer += 1
            if (tvPerc.text.toString().trim() != "0%")
                handlerPer.postDelayed(this, 30000)
            else {
                isPercentShow = false
                tvSubmitStartOver.text = getString(R.string.start_over)
            }
        }
    }

    private fun cancelTimer() {
        if (cTimer != null)
            cTimer.cancel()
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        unregisterReceiver(myReceiver)
//        removeHubConnection()
//        cancelTimer()
//        hubConnection?.close()
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

    override fun onBackPressed() {
        if (isTimeOver) {
            removeHubConnection()
            super.onBackPressed()
        } else {
            popupLeaveDeal()
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
//                if (isTimeOver) {
                onBackPressed()
                /*} else {
                    popupLeaveDeal()
                }*/
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
                    Toast.makeText(this, "Invalid card data", Toast.LENGTH_LONG).show()
                    return
                }
                val cardSourceParams = SourceParams.createCardParams(card!!)

                stripe.createSource(
                    cardSourceParams,
                    callback = object : ApiResultCallback<Source> {
                        override fun onSuccess(source: Source) {
                            //    Log.e("Success", Gson().toJson(source))
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
                if (tvSubmitStartOver.text == getString(R.string.start_over)) {
                    removeHubConnection()
                    callCheckVehicleStockAPI()
                } else {
                        if (isValidCard()) {
                            if (isValid()) {
                                callPaymentMethodAPI(true)
                            }
                        }
                }
            }
            R.id.tvAddMin -> {
//                seconds += 120;
                //seconds = ((60 * ((2 + minutes) + (second / 60))).toDouble())
                add2Min()
//                startTimerOn()
                tvAddMin.visibility = View.GONE
//                cancelTimer()
//                startTimer()
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
        }
    }


    private fun callCheckVehicleStockAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val pkgList = JsonArray()
            for (i in 0 until dataLCDDeal.arPackageId.size) {
                pkgList.add(dataLCDDeal.arPackageId[i])
            }
            val accList = JsonArray()
            for (i in 0 until dataLCDDeal.arAccessoriesId.size) {
                accList.add(dataLCDDeal.arAccessoriesId[i])
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = 2
            request[ApiConstant.YearId1] = dataLCDDeal.yearId!!
            request[ApiConstant.MakeId1] = dataLCDDeal.makeId!!
            request[ApiConstant.ModelID] = dataLCDDeal.modelId!!
            request[ApiConstant.TrimID] = dataLCDDeal.trimId!!
            request[ApiConstant.ExteriorColorID] = dataLCDDeal.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = dataLCDDeal.interiorColorId!!
            request[ApiConstant.ZipCode1] = dataLCDDeal.zipCode!!
            request[ApiConstant.SearchRadius1] = "6000"
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            // Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        startActivity(
                            intentFor<MainActivity>(ARG_SEL_TAB to TYPE_ONE_DEAL_NEAR_YOU).clearTask()
                                .newTask()
                        )
                    } else {
                        clearOneDealNearData()
                        startActivity(
                            intentFor<MainActivity>(ARG_SEL_TAB to TYPE_ONE_DEAL_NEAR_YOU).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClearData() {
        edtCardNumber.setText("")
//        edtCardHolder.setText("")
        edtExpiresDate.setText("")
        edtCVV.setText("")
//        llCardViewDetail.visibility = View.GONE
    }

    private fun getDetectedCreditCardImage(): String {
        val type: CreditCardType = CreditCardType.detect(edtCardNumber.text.toString().trim())
        return if (type != null) {
            type.imageResourceName
        } else {
            "ic_camera"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun popupLeaveDeal() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_leave_my_deal)
        dialog.run {
            tvCancel.setOnClickListener {
                dismiss()
            }
            tvLeaveDeal.setOnClickListener {
                stopTimer()
                dismiss()
                finish()
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
            edtCardZipCode.text.toString().trim().length != 5 -> {
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
            /*  TextUtils.isEmpty(edtAddress2.text.toString().trim()) -> {
                  Constant.setErrorBorder(edtAddress2, tvErrorAddress2)
                  return false
              }*/
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
            tvDialogTitle.text =
                dataLCDDeal.yearStr + " " + dataLCDDeal.makeStr + " " + dataLCDDeal.modelStr + " " + dataLCDDeal.trimStr
            tvDialogExteriorColor.text = dataLCDDeal.exteriorColorStr
            tvDialogInteriorColor.text = dataLCDDeal.interiorColorStr
            tvDialogPackage.text = dataLCDDeal.arPackage
            tvDialogOptions.text = dataLCDDeal.arAccessories
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    override fun onItemSelected(v: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (v?.id) {
            R.id.spState -> {
                val data = adapterState.getItem(position) as String
                state = data
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private lateinit var tokenModel: RefreshTokenViewModel

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
        stripe.handleNextActionForPayment(this@LCDDealSummaryStep2Activity, key)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Log.e("Stripe", "requestCode " + requestCode)
        cardInputWidget.setCardNumber(edtCardNumber.text.toString().trim())
        cardInputWidget.setCvcCode(edtCVV.text.toString().trim())
        cardInputWidget.setExpiryDate(1, 2022)
        val params = cardInputWidget.paymentMethodCreateParams

        // stripe.confirmPayment(this@LCDDealSummaryStep2Activity, ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params!!,secretKey))
        stripe.onPaymentResult(requestCode, data, this@LCDDealSummaryStep2Activity)
    }


    override fun onError(e: Exception) {

        // Log.e("PaymentFailed", Gson().toJson(e).toString())
        alertCardError(
            this,
            getString(R.string.we_are_unable_to_authenticate_your_payment)
        )
        setClearCardData()
    }

    private var paymentIntentId = ""
    override fun onSuccess(result: PaymentIntentResult) {
        val paymentIntent: PaymentIntent = result.intent
        val status: StripeIntent.Status? = paymentIntent.status
        //Log.e("Status", Gson().toJson(paymentIntent))
        when (status) {
            StripeIntent.Status.Succeeded -> {
                // Payment completed successfully
                val gson = GsonBuilder().setPrettyPrinting().create()
                //  Log.e("completed", gson.toJson(paymentIntent))
                paymentIntentId = paymentIntent.id!!
                callSubmitDealLCDAPI(true)
            }
            StripeIntent.Status.RequiresPaymentMethod -> {
                //Log.e("RequiresPaymentMethod", Objects.requireNonNull(paymentIntent.lastPaymentError).toString())
                /* if(!TextUtils.isEmpty(paymentIntent.id)) {
                     paymentIntentId = paymentIntent.id!!
                     callSubmitDealLCDAPI(true)
                 }else{
                     Toast.makeText(this,"Requires Payment Method",Toast.LENGTH_LONG).show()
                 }*/
                AppGlobal.alertCardError(
                    this,
                    getString(R.string.we_are_unable_to_authenticate_your_payment)
                )
                setClearCardData()
            }
            StripeIntent.Status.Canceled -> {
                // Log.e("Canceled", "Payment Canceled")
//                Toast.makeText(this,"Payment Canceled",Toast.LENGTH_LONG).show()
                alertCardError(
                    this,
                    getString(R.string.we_are_unable_to_authenticate_your_payment)
                )
                setClearCardData()
            }
            StripeIntent.Status.Processing -> {
                //   Log.e("Processing", "Payment Processing")
                if (!TextUtils.isEmpty(paymentIntent.id)) {
                    paymentIntentId = paymentIntent.id!!
                    callSubmitDealLCDAPI(true)
                } else {
                    Toast.makeText(this, "Payment Processing", Toast.LENGTH_LONG).show()
                }
            }
            StripeIntent.Status.RequiresConfirmation -> {
                //   Log.e("RequiresConfirmation", "Payment Confirmation")
                if (!TextUtils.isEmpty(paymentIntent.id)) {
                    paymentIntentId = paymentIntent.id!!
                    callSubmitDealLCDAPI(true)
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

    private fun responseDealSuccess(dialogProgress: Dialog, data: SubmitDealLCDData) {
        data.successResult?.transactionInfo?.vehiclePromoCode = dataLCDDeal.discount
        data.successResult?.transactionInfo?.vehiclePrice = dataLCDDeal.price!!
        data.successResult?.transactionInfo?.remainingBalance =
            (dataLCDDeal.price!! - (799.0f + dataLCDDeal.discount!!))
        //  Log.e("data Deal", Gson().toJson(data))
        if (!data.isDisplayedPriceValid!! && data.somethingWentWrong!!) {
            removeHubConnection()
            startActivity<LCDNegativeActivity>(
                ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                ARG_IMAGE_ID to imageId,
                ARG_IMAGE_URL to Gson().toJson(arImage),
                ARG_IS_SHOW_PER to isPercentShow
            )
            finish()
        } else if (data.isDisplayedPriceValid && !data.somethingWentWrong!!) {
            if (data.foundMatch!!) {
                removeHubConnection()
                clearOneDealNearData()
                startActivity<SubmitDealSummaryActivity>(
                    ARG_SUBMIT_DEAL to Gson().toJson(
                        data
                    ),
                    ARG_TYPE_PRODUCT to "LightningCarDeals"
                )
                finish()
            } else if (!data.foundMatch && data.isBadRequest!! && !data.isDisplayedPriceValid && data.somethingWentWrong) {
                removeHubConnection()
                startActivity<LCDNegativeActivity>(
                    ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                    ARG_IMAGE_ID to imageId,
                    ARG_IMAGE_URL to Gson().toJson(arImage),
                    ARG_IS_SHOW_PER to isPercentShow
                )
                finish()
            } else if (!data.foundMatch && !data.isBadRequest!! && data.paymentResponse?.hasError!!) {
                if (!TextUtils.isEmpty(data.paymentResponse.errorMessage))
                    AppGlobal.alertCardError(
                        this,
                        data.paymentResponse.errorMessage
                    )
                setClearCardData()
            } else if (!data.foundMatch && !data.paymentResponse?.hasError!!) {
                if (data.paymentResponse.requires_action!!)
                    initStripe(data.paymentResponse.payment_intent_client_secret!!)
                else {
                    removeHubConnection()
                    startActivity<LCDNegativeActivity>(
                        ARG_SUBMIT_DEAL to Gson().toJson(dataLCDDeal),
                        ARG_IMAGE_ID to imageId,
                        ARG_IMAGE_URL to Gson().toJson(arImage),
                        ARG_IS_SHOW_PER to isPercentShow
                    )
                    finish()
                }
            }
        }

//        dialogProgress.dismiss()
    }

}