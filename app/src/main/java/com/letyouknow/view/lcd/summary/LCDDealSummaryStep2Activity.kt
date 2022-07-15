package com.letyouknow.view.lcd.summary

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import android.widget.*
import androidx.core.text.isDigitsOnly
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
import com.letyouknow.utils.AppGlobal.Companion.alertPaymentError
import com.letyouknow.utils.AppGlobal.Companion.arState
import com.letyouknow.utils.AppGlobal.Companion.formatPhoneNo
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.Constant.Companion.ARG_CAL_TAX_DATA
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
import com.letyouknow.view.samsungpay.*
import com.letyouknow.view.signup.CardListAdapter
import com.letyouknow.view.spinneradapter.DeliveryPreferenceAdapter
import com.letyouknow.view.spinneradapter.RebateDiscAdapter
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import com.letyouknow.view.ucd.submitdealsummary.SubmitDealSummaryActivity
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TransportEnum
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
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_lcd_deal_summary_step2.*
import kotlinx.android.synthetic.main.dialog_deal_progress_bar.*
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.dialog_inventory_availability.*
import kotlinx.android.synthetic.main.dialog_leave_my_deal.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.dialog_rebate_disc.*
import kotlinx.android.synthetic.main.layout_card_google_samsung.*
import kotlinx.android.synthetic.main.layout_dealer_shipping_info.*
import kotlinx.android.synthetic.main.layout_lcd_deal_summary_step2.*
import kotlinx.android.synthetic.main.layout_toolbar_timer.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.*


class LCDDealSummaryStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, ApiResultCallback<PaymentIntentResult>,
    CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {
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
    private lateinit var calculateTaxViewModel: CalculateTaxViewModel
    private lateinit var rebateViewModel: RebateViewModel
    private lateinit var rebateResetViewModel: RebateResetViewModel
    private lateinit var rebateListViewModel: RebateListViewModel
    private lateinit var rebateCheckedViewModel: RebateCheckedViewModel
    var dollar = 0.0

    var hubConnection: HubConnection? = null

    var isPercentShow = false

    private var isStripe = true
    private var isGooglePay = false
    private var isSamsungPay = false
    private var isShowSamsungPay = false

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
        calculateTaxViewModel =
            ViewModelProvider(this)[CalculateTaxViewModel::class.java]
        rebateViewModel = ViewModelProvider(this)[RebateViewModel::class.java]
        rebateResetViewModel = ViewModelProvider(this)[RebateResetViewModel::class.java]
        rebateListViewModel = ViewModelProvider(this)[RebateListViewModel::class.java]
        rebateCheckedViewModel = ViewModelProvider(this)[RebateCheckedViewModel::class.java]

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
            binding.selectState = state
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
        tvRebatesDisc.setOnClickListener(this)

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

        binding.isStripe = isStripe
        binding.isGooglePay = isGooglePay
        binding.isSamsungPay = isSamsungPay
        binding.isShowSamsungPay = isShowSamsungPay
        llCreditCard.setOnClickListener(this)
        llAndroidPay.setOnClickListener(this)
        llSamsungPay.setOnClickListener(this)
        btnGooglePayProceedDeal.setOnClickListener(this)
        initLiveGoogle()
        setDeliveryOptions()
        checkEmptyData()
        callRebateListAPI()
        initSPay()
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
//                    callCalculateTaxAPI()
                    callApplyRebateAPI()
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
            map[ApiConstant.phoneNumber] = dataPendingDeal.buyer?.phoneNumber!!
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
            map[ApiConstant.ShipToCountry] = if (isShipping()) "US" else ""
            map[ApiConstant.ShipIt] = isShipping()

            buyerViewModel.buyerCall(this, map)!!
                .observe(
                    this
                ) { data ->
//                    Constant.dismissLoader()
                    if (TextUtils.isEmpty(data.buyerId)) {
                        alertError("Something went wrong. Please try again later.")
                    } else {
                        callSubmitDealLCDAPI(false)
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
                .observe(
                    this
                ) { data ->
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
                                ARG_TYPE_PRODUCT to "LightningCarDeals",
                                ARG_CAL_TAX_DATA to Gson().toJson(calculateTaxData),
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
                .observe(
                    this
                ) { data ->
                    Constant.dismissLoader()
                    if (data.discount!! > 0) {
                        tvPromoData.visibility = View.VISIBLE
                        tvPromoData.text =
                            "-${NumberFormat.getCurrencyInstance(Locale.US).format(data.discount)}"
                        dataLCDDeal.discount = data.discount!!
                        dataLCDDeal.promotionId = data.promotionID!!
                        binding.ucdData = dataLCDDeal
//                        callCalculateTaxAPI()
                        callApplyRebateAPI()
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
//                        callCalculateTaxAPI()
                        callApplyRebateAPI()
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
            }
            R.id.btnGooglePayProceedDeal -> {
                setErrorVisible()
                if (tvSubmitStartOver.text == getString(R.string.start_over)) {
                    removeHubConnection()
                    callCheckVehicleStockAPI()
                } else {
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
            R.id.tvRebatesDisc -> {
                if (arRebate.isNullOrEmpty()) {
                    AppGlobal.alertError(
                        this,
                        getString(R.string.no_rebates_found, " " + dataLCDDeal.zipCode)
                    )
                } else {
                    strRebate = Gson().toJson(arRebate)
                    setRebateSelection()
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
                setRebateSelection()
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
                    isShowRebateDis = true
                    callApplyRebateAPI()
                }
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
                startInAppPayWithCustomSheet()
            }
        }
    }

    private fun setGoogleSamsung() {
        binding.isStripe = isStripe
        binding.isGooglePay = isGooglePay
        binding.isSamsungPay = isSamsungPay
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
                binding.selectState = state
                isState = true
//                callCalculateTaxAPI()
                callApplyRebateAPI()
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
                    Toast.makeText(this, "Patment Processing", Toast.LENGTH_LONG).show()
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
                    ARG_TYPE_PRODUCT to "LightningCarDeals",
                    ARG_CAL_TAX_DATA to Gson().toJson(calculateTaxData)
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
                AppGlobal.alertPaymentError(this, getString(R.string.google_payment_canceled))
                cardStripeData = CardStripeData()
            }
            is GooglePayPaymentMethodLauncher.Result.Failed -> {
                result.error.message?.let { Log.e("Failed", it) }
                AppGlobal.alertPaymentError(this, result.error.message)
                cardStripeData = CardStripeData()
                // Operation failed; inspect `result.error` for the exception
            }
        }
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
                dataLCDDeal.price!!.toDouble(),
                dataLCDDeal.discount!!.toDouble(),
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
                /* val str = s?.toString()
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
                        /* isMiddleName = true
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
                RebateDiscAdapter(R.layout.list_item_rebate_disc, this@LCDDealSummaryStep2Activity)
            dialogRebate.rvRebate.adapter = adapterRebateDisc
            adapterRebateDisc.addAll(arData)

            tvCancelRebate.setOnClickListener(this@LCDDealSummaryStep2Activity)
            tvResetRebate.setOnClickListener(this@LCDDealSummaryStep2Activity)
            tvApplyRebate.setOnClickListener(this@LCDDealSummaryStep2Activity)
        }
        setLayoutParam(dialogRebate)
    }


    private fun showApplyEmptyDialog() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_inventory_availability)
        dialog.run {
            tvMessageEmpty.text = getString(R.string.one_or_more_rebates_need_to_be_applied)
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    //rebate api
    private var isShowRebateDis = false
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
            map[ApiConstant.Zipcode1] = dataLCDDeal.zipCode!!
            map[ApiConstant.VehicleYearId1] = dataLCDDeal.yearId!!
            map[ApiConstant.VehicleMakeId1] = dataLCDDeal.makeId!!
            map[ApiConstant.VehicleModelId1] = dataLCDDeal.modelId!!
            map[ApiConstant.VehicleTrimId1] = dataLCDDeal.trimId!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"
            map[ApiConstant.RebateList] = jsonRebate
            map[ApiConstant.priceBid] = dataLCDDeal.price!!.toDouble()
            map[ApiConstant.promocodeDiscount] = dataLCDDeal.discount!!.toDouble()
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
                    if (!isShowRebateDis) {
                        data.estimatedRebates = 0.0
                        if (::dialogRebate.isInitialized)
                            dialogRebate.tvDesc.text = Html.fromHtml(
                                getString(
                                    R.string.subject_to_eligibility_verification
                                )
                            )
                    } else {
                        if (::dialogRebate.isInitialized)
                            dialogRebate.tvDesc.text = Html.fromHtml(
                                if (!TextUtils.isEmpty(data.rebateDetails)) getString(
                                    R.string.subject_to_eligibility_verification_by_the_dealership_rebates_amp_discounts_may_be_taxable,
                                    data.rebateDetails
                                ) else getString(
                                    R.string.subject_to_eligibility_verification
                                )
                            )
                    }
                    binding.taxData = data
                    calculateTaxData = data
                    strRebate = Gson().toJson(arRebate)
                    if (::dialogRebate.isInitialized)
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
            map[ApiConstant.Zipcode1] = dataLCDDeal.zipCode!!
            map[ApiConstant.VehicleYearId1] = dataLCDDeal.yearId!!
            map[ApiConstant.VehicleMakeId1] = dataLCDDeal.makeId!!
            map[ApiConstant.VehicleModelId1] = dataLCDDeal.modelId!!
            map[ApiConstant.VehicleTrimId1] = dataLCDDeal.trimId!!
            map[ApiConstant.GuestId] = "0"
            map[ApiConstant.UserId] = pref?.getUserData()?.buyerId!!
            map[ApiConstant.DealId] = dataPendingDeal.dealID!!
            map[ApiConstant.ProductId1] = "1"
            map[ApiConstant.RebateList] = jsonRebate
            map[ApiConstant.priceBid] = dataLCDDeal.price!!.toDouble()
            map[ApiConstant.promocodeDiscount] = dataLCDDeal.discount!!.toDouble()
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
                    setRebateSelection()
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
            map[ApiConstant.Zipcode1] = dataLCDDeal.zipCode!!
            map[ApiConstant.VehicleYearId1] = dataLCDDeal.yearId!!
            map[ApiConstant.VehicleMakeId1] = dataLCDDeal.makeId!!
            map[ApiConstant.VehicleModelId1] = dataLCDDeal.modelId!!
            map[ApiConstant.VehicleTrimId1] = dataLCDDeal.trimId!!
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
            map[ApiConstant.Zipcode1] = dataLCDDeal.zipCode!!
            map[ApiConstant.VehicleYearId1] = dataLCDDeal.yearId!!
            map[ApiConstant.VehicleMakeId1] = dataLCDDeal.makeId!!
            map[ApiConstant.VehicleModelId1] = dataLCDDeal.modelId!!
            map[ApiConstant.VehicleTrimId1] = dataLCDDeal.trimId!!
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

                    setRebateSelection()
                }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRebateSelection() {
        var selectStr = ""
        for (i in 0 until adapterRebateDisc.itemCount) {
            if (adapterRebateDisc.getItem(i).isSelect == true || adapterRebateDisc.getItem(
                    i
                ).isOtherSelect == true
            ) {
                selectStr =
                    selectStr + " " + adapterRebateDisc.getItem(i).rebateName + "(" + NumberFormat.getCurrencyInstance(
                        Locale.US
                    ).format(adapterRebateDisc.getItem(i).rebatePrice)!!
                        .replace(".00", "") + ")"
            }
        }

        if (::dialogRebate.isInitialized)
            dialogRebate.tvDesc.text = Html.fromHtml(
                if (!TextUtils.isEmpty(selectStr)) getString(
                    R.string.subject_to_eligibility_verification_by_the_dealership_rebates_amp_discounts_may_be_taxable,
                    selectStr
                ) else getString(
                    R.string.subject_to_eligibility_verification
                )
            )
    }

    //SamsungPayment
    var mSampleAppPartnerInfoHolder: SampleAppPartnerInfoHolder? = null
    private var mPaymentManager: PaymentManager? = null
    private var mActivityResumed = false

    private var mAmountDetailControls: AmountDetailNewControls? = null
    private var mBillingAddressControls: BillingAddressControls? = null
    private var mShippingAddressControls: ShippingAddressControls? = null


    fun initSPay() {
        mSampleAppPartnerInfoHolder = SampleAppPartnerInfoHolder(this)

        val orderDetailsListener = OrderDetailsListener { bool ->
            if (bool) {
                enableSamsungPayButton()
            } else {
                disableSamsungPayButton()
            }
        }

        //   mAmountDetailControls = AmountDetailControls(mContext, mBinding!!.amountDetails, orderDetailsListener)
        mAmountDetailControls = AmountDetailNewControls(
            this,
            dataLCDDeal.price!!.toDouble() - (799.0f + dataLCDDeal.discount!!.toDouble() + dollar),
            orderDetailsListener
        )

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
        updateSamsungPayButton()
    }

    private fun enableSamsungPayButton() {
        binding.isShowSamsungPay = true
    }

    private fun disableSamsungPayButton() {
        binding.isShowSamsungPay = false
    }

    protected fun updateSamsungPayButton() {
        val samsungPay = SamsungPay(this, mSampleAppPartnerInfoHolder!!.partnerInfo)
        try {
            samsungPay.getSamsungPayStatus(object : StatusListener {
                override fun onSuccess(status: Int, bundle: Bundle) {
                    when (status) {
                        SpaySdk.SPAY_READY -> {
                            binding.isShowSamsungPay = true
                            if (mPaymentManager == null) {
                                mPaymentManager = PaymentManager(
                                    this@LCDDealSummaryStep2Activity,
                                    mSampleAppPartnerInfoHolder!!.partnerInfo
                                )
                                // Get Card List.
                            }
                        }
                        SpaySdk.SPAY_NOT_SUPPORTED, SpaySdk.SPAY_NOT_READY, SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> binding.isShowSamsungPay =
                            false
                        else -> binding.isShowSamsungPay = false
                    }
                    showOnSuccessLog(status, bundle) // Print log
                    showOnSuccessMessage(status, bundle) // Print messages.
                }

                override fun onFail(errorCode: Int, bundle: Bundle) {
                    binding.isShowSamsungPay = false
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
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        } else if (!isRetry) {
            Handler(Looper.getMainLooper()).postDelayed({
                displayToastMessageIfRequired(
                    msg,
                    true
                )
            }, 1000)
        }
    }

    private fun doActivateSamsungPay() {
        val samsungPay = SamsungPay(this, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.activateSamsungPay()
    }

    private fun doUpdateSamsungPay() {
        val samsungPay = SamsungPay(this, mSampleAppPartnerInfoHolder!!.partnerInfo)
        samsungPay.goToUpdatePage()
    }

    protected fun startInAppPayWithCustomSheet() {
        Log.d(
            TAG, "startInAppPayWithCustomSheet"
        )
        // PaymentManager.startInAppPayWithCustomSheet method to show custom payment sheet.
//        disableSamsungPayButton()

        val orderDetailsListener = OrderDetailsListener { bool ->
            if (bool) {
                enableSamsungPayButton()
            } else {
                disableSamsungPayButton()
            }
        }

        mAmountDetailControls = AmountDetailNewControls(
            this,
            dataLCDDeal.price!!.toDouble() - (799.0f + dataLCDDeal.discount!!.toDouble() + dollar),
            orderDetailsListener
        )
        mPaymentManager = PaymentManager(this, mSampleAppPartnerInfoHolder!!.partnerInfo)

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
                val fragmentActivity: Activity = this@LCDDealSummaryStep2Activity
                if (fragmentActivity == null || fragmentActivity.isFinishing || fragmentActivity.isDestroyed) {
                    return
                }
                val mPaymentResultDialog = PaymentResultDialog(this@LCDDealSummaryStep2Activity)
                mPaymentResultDialog.onSuccessDialog(response, paymentCredential, extraPaymentData)
                displayToastMessageIfRequired("Transaction : onSuccess")
                enableSamsungPayButton()
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
                    enableSamsungPayButton()
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