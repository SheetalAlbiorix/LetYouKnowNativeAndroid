package com.letyouknow.view.unlockedcardeal.unlockeddealdetail

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUnlockedDealSummaryStep2Binding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.arState
import com.letyouknow.utils.CreditCardNumberTextWatcher
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummary.gallery360view.Gallery360TabActivity
import com.letyouknow.view.signup.CardListAdapter
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import com.letyouknow.view.unlockedcardeal.submitdealsummary.SubmitDealSummaryActivity
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TransportEnum
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_unlocked_deal_summary_step2.*
import kotlinx.android.synthetic.main.dialog_leave_my_deal.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_timer.*
import kotlinx.android.synthetic.main.layout_unlocked_deal_summary_step2.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UnlockedDealSummaryStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityUnlockedDealSummaryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer
    private var seconds = 300
    private var isFirst60 = true
    private var imageUrl = ""
    private var state = "NC"

    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var pendingUCDData: SubmitPendingUcdData
    private lateinit var lykDollarViewModel: LYKDollarViewModel
    private lateinit var promoCodeViewModel: PromoCodeViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodViewModel
    private lateinit var buyerViewModel: BuyerViewModel
    private lateinit var tokenModel: RefreshTokenViewModel
    private lateinit var submitDealUCDViewModel: SubmitDealUCDViewModel
    private lateinit var ucdData: FindUcdDealData
    private lateinit var arImage: ArrayList<String>
    private var isTimeOver = false
    private var imageId = "0"
    private lateinit var submitPendingUCDDealViewModel: SubmitPendingUCDDealViewModel
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel
    var hubConnection: HubConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_deal_summary_step2)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_unlocked_deal_summary_step2)
        init()
    }

    private fun init() {
        checkVehicleStockViewModel =
            ViewModelProvider(this).get(CheckVehicleStockViewModel::class.java)
        tokenModel = ViewModelProvider(this).get(RefreshTokenViewModel::class.java)
        lykDollarViewModel = ViewModelProvider(this).get(LYKDollarViewModel::class.java)
        promoCodeViewModel = ViewModelProvider(this).get(PromoCodeViewModel::class.java)
        paymentMethodViewModel = ViewModelProvider(this).get(PaymentMethodViewModel::class.java)
        buyerViewModel = ViewModelProvider(this).get(BuyerViewModel::class.java)
        submitDealUCDViewModel = ViewModelProvider(this).get(SubmitDealUCDViewModel::class.java)
        submitPendingUCDDealViewModel =
            ViewModelProvider(this).get(SubmitPendingUCDDealViewModel::class.java)

        if (intent.hasExtra(ARG_UCD_DEAL) && intent.hasExtra(ARG_UCD_DEAL_PENDING) && intent.hasExtra(
                ARG_YEAR_MAKE_MODEL
            ) && intent.hasExtra(
                ARG_IMAGE_URL
            ) && intent.hasExtra(
                ARG_IMAGE_ID
            )
        ) {
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            arImage = Gson().fromJson(
                intent.getStringExtra(ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }

            ucdData =
                Gson().fromJson(intent.getStringExtra(ARG_UCD_DEAL), FindUcdDealData::class.java)
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            pendingUCDData = Gson().fromJson(
                intent.getStringExtra(ARG_UCD_DEAL_PENDING),
                SubmitPendingUcdData::class.java
            )

            binding.ucdData = ucdData
            binding.pendingUcdData = pendingUCDData
            callRefreshTokenApi()

            val mNo = "(" + pendingUCDData.buyer?.phoneNumber
            val mno1 = AppGlobal.insertString(mNo, ")", 3)
            val mno2 = AppGlobal.insertString(mno1!!, "-", 7)
            if (pendingUCDData.buyer?.phoneNumber?.contains("(") == false)
                edtPhoneNumber.setText(AppGlobal.formatPhoneNo(pendingUCDData.buyer?.phoneNumber))
            else
                edtPhoneNumber.setText(pendingUCDData.buyer?.phoneNumber)

        }
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber, tvErrorCardNumber)
        edtCardNumber.addTextChangedListener(textWatcher)

        initCardAdapter()
        binding.lightDealBindData = lightBindData
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvHavePromoCode.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        btnProceedDeal.setOnClickListener(this)
        tvAddMin.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)
        tvApplyPromo.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ll360.setOnClickListener(this)
        llGallery.setOnClickListener(this)
//        startTimer()
//        backButton()
        onStateChange()
        setState()
        AppGlobal.strikeThrough(tvPrice)

        edtPhoneNumber.filters = arrayOf<InputFilter>(filter, InputFilter.LengthFilter(13))
        /*for(i in 0 until pendingUCDData.buyer?.phoneNumber?.length!!){
            val data = edtPhoneNumber.text.toString().trim()
        }*/
        initPayment()
        setOnChangeCard()

        initHub()
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
                Log.e("onSubscribe", "onSubscribe")
            }

            override fun onComplete() {
                Log.e("onComplete", "onComplete")
                if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                    handleHub()
                }
            }

            override fun onError(e: Throwable) {
                Log.e("onError", "onError")
            }

        })
    }

    private fun handleHub() {
        Log.e("dealId", ucdData.dealID!!)
        Log.e("buyerId", pref?.getUserData()?.buyerId.toString())

        hubConnection?.invoke("SubscribeOnDeal", ucdData.dealID!!.toInt())!!

        hubConnection!!.on(
            "CantSubscribeOnDeal",
            { vehicleId, message ->  // OK
                try {
                    Log.e("data", "$vehicleId: $message")
                } catch (e: Exception) {
                    Log.e("CantSubscribeOnDeal", e.message.toString())
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
                try {
                    Log.e("data", "$dealId: $timer")
                    /* val time = timer.toString()
                     seconds = time.toInt()*/
//                    timerVisible(timer.toInt())
                    seconds = timer.toInt()
                    tvTimer.text = (String.format("%02d", seconds / 60)
                            + ":" + String.format("%02d", seconds % 60))

                    if (seconds == 60 && isFirst60) {
                        Log.e("data", "aaaaaaa")
                        this@UnlockedDealSummaryStep2Activity.runOnUiThread {
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
                        this@UnlockedDealSummaryStep2Activity.runOnUiThread {
                            setPer()
                            tvAddMin.visibility = View.GONE
                            tvTimer.visibility = View.GONE
                            llExpired.visibility = View.VISIBLE
                            isTimeOver = true
                            tvSubmitStartOver.text = getString(R.string.try_again)
                        }
                        //cancelTimer()
//                        stopTimer()

                        //  return
                    }
                } catch (e: Exception) {
                    Log.e("UpdateDealTimer", e.message.toString())
                }
            },
            Any::class.java, Double::class.java
        )
    }


    private fun stopTimer() {
        hubConnection?.invoke("StopTimer")
    }

    private fun add2Min() {
        hubConnection?.invoke("Add2Minutes", ucdData.dealID!!.toInt())
    }

    private fun removeHubConnection() {
        hubConnection?.remove("UpdateDealTimer")
        hubConnection?.remove("UpdateVehicleState")
        hubConnection?.remove("CantSubscribeOnDeal")
    }

    private lateinit var stripe: Stripe

    private fun initPayment() {
        stripe =
            Stripe(this, Objects.requireNonNull(getString(R.string.stripe_publishable_key)))
    }

    private fun callDollarAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            lykDollarViewModel.getDollar(this, pendingUCDData.dealID)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data == "0.00") {
                        llDollar.visibility = View.GONE
                    }
                    tvDollar.text =
                        NumberFormat.getCurrencyInstance(Locale.US).format(data.toFloat())
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callBuyerAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.buyerId] = pendingUCDData.buyer?.buyerId!!
            map[ApiConstant.firstName] = edtFirstName.text.toString().trim()
            map[ApiConstant.middleName] = edtFirstName.text.toString().trim()
            map[ApiConstant.lastName] = edtLastName.text.toString().trim()
            map[ApiConstant.phoneNumber] = pendingUCDData.buyer?.phoneNumber!!
            map[ApiConstant.email] = edtEmail.text.toString().trim()
            map[ApiConstant.addressId] = pendingUCDData.buyer?.addressId!!
            map[ApiConstant.address1] = edtAddress1.text.toString().trim()
            map[ApiConstant.address2] = edtAddress2.text.toString().trim()
            map[ApiConstant.city] = edtCity.text.toString().trim()
            map[ApiConstant.state] = state
            map[ApiConstant.zipcode] = edtZipCode.text.toString().trim()
            map[ApiConstant.country] = "US"

            buyerViewModel.buyerCall(this, map)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    callSubmitDealLCDAPI()

                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callSubmitDealLCDAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.dealID] = pendingUCDData.dealID!!
            map[ApiConstant.buyerID] = pendingUCDData.buyer?.buyerId!!
            map[ApiConstant.payment_method_id] = cardStripeData.id!!
            map[ApiConstant.card_last4] = cardStripeData.card?.last4!!
            map[ApiConstant.card_brand] = cardStripeData.card?.brand!!
            map[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            map[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            map[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            map[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            map[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            map[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            map[ApiConstant.price] = ucdData.price!!
            map[ApiConstant.timeZoneOffset] = "-330"
            map[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
            map[ApiConstant.searchRadius] =
                if (yearModelMakeData.radius == "ALL") "6000" else yearModelMakeData.radius?.replace(
                    " mi",
                    ""
                )!!
            map[ApiConstant.loanType] = yearModelMakeData.loanType!!
            map[ApiConstant.initial] = yearModelMakeData.initials!!
//            map[ApiConstant.timeZoneOffset] = pendingUCDData.buyer?.buyerId!!
            map[ApiConstant.vehicleInventoryID] = ucdData.vehicleInventoryID!!
            map[ApiConstant.guestID] = ucdData.guestID!!
            val arJsonPackage = JsonArray()
            arJsonPackage.add("0")
            /* for (i in 0 until ucdData.vehiclePackages?.size!!) {
                 arJsonPackage.add(ucdData.vehiclePackages!![i].vehiclePackageID)
             }*/
            val arJsonAccessories = JsonArray()
            arJsonAccessories.add("0")
            /*for (i in 0 until ucdData.vehicleAccessories?.size!!) {
                arJsonAccessories.add(ucdData.vehicleAccessories!![i].dealerAccessoryID)
            }*/

            map[ApiConstant.dealerAccessoryIDs] = arJsonPackage
            map[ApiConstant.vehiclePackageIDs] = arJsonAccessories
Log.e("submitdealucd", Gson().toJson(map))
            submitDealUCDViewModel.submitDealLCDCall(this, map)!!
                .observe(this, { data ->
                    Constant.dismissLoader()

                    data.successResult?.transactionInfo?.vehiclePrice = ucdData.price!!
                    data.successResult?.transactionInfo?.remainingBalance =
                        (ucdData.price!! - (799.0f + ucdData.discount!!))
                    clearPrefSearchDealData()
                    if (data.foundMatch!!)
                        startActivity<SubmitDealSummaryActivity>(
                            ARG_SUBMIT_DEAL to Gson().toJson(
                                data
                            )
                        )
                    else
                    /* startActivity<UnlockedTryAnotherActivity>(
                         ARG_UCD_DEAL to Gson().toJson(ucdData),
                         ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                         ARG_IMAGE_URL to Gson().toJson(arImage),
                         ARG_IMAGE_ID to imageId
                     )*/
                        startActivity<FinalUnlockedDealSummaryActivity>(
                            ARG_SUBMIT_DEAL to Gson().toJson(data),
                            ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                            ARG_IMAGE_URL to Gson().toJson(arImage),
                            ARG_IMAGE_ID to imageId
                        )
                    finish()
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var cardStripeData: CardStripeData
    private fun callPaymentMethodAPI() {
        pref?.setPaymentToken(true)
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

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
                    Constant.dismissLoader()
                    cardStripeData = data
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun callPromoCodeAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            promoCodeViewModel.getPromoCode(
                this,
                edtGiftCard.text.toString().trim(),
                pendingUCDData.dealID
            )!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data.discount!! > 0) {
                        tvPromoData.visibility = View.VISIBLE
                        tvPromoData.text = "-$${data.discount}"
                        ucdData.discount = data.discount!!
                        binding.ucdData = ucdData
                    } else {
                        tvPromoData.visibility = View.GONE
                        ucdData.discount = 0.0f
                        binding.ucdData = ucdData
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

    private lateinit var adapterState: StateSpinnerAdapter
    private fun setState() {
        adapterState = StateSpinnerAdapter(
            applicationContext,
            arState
        )
//        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterState
        spState.onItemSelectedListener = this

        for (i in 0 until arState.size) {
            if (arState[i] == pendingUCDData.buyer?.state) {
                spState.setSelection(i)
            }
        }
    }

    private fun onStateChange() {
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



    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    private fun startTimer() {
        cTimer = object : CountDownTimer(((seconds * 1000)).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds -= 1
                tvTimer.text = (String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60))
                if (seconds == 60 && isFirst60) {
                    tvAddMin.visibility = View.VISIBLE
                    isFirst60 = false
                } else if (seconds < 60) {
                    tvTimer.setBackgroundResource(R.drawable.bg_round_border_red)
                    tvTimer.setTextColor(resources.getColor(R.color.colorB11D1D))
                } else {
                    tvTimer.setBackgroundResource(R.drawable.bg_round_border_blue)
                    tvTimer.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                if (seconds == 0 || seconds == 1) {
//                    tvDealGuaranteed.text = "Reserved Deal has expired"
                    tvAddMin.visibility = View.GONE
                    tvTimer.visibility = View.GONE
                    llExpired.visibility = View.VISIBLE
                    isTimeOver = true
                    tvSubmitStartOver.text = getString(R.string.try_again)
                    cancelTimer()
                    return
                }
            }

            override fun onFinish() {

            }
        }.start()
    }

    private lateinit var handlerPer: Handler
    private var countPer = 0
    private var arPer = arrayListOf("75%", "50%", " 25%", "0%")

    private fun setPer() {
        handlerPer = Handler()
        handlerPer.postDelayed(runnablePer, 30000)
    }

    private var runnablePer = object : Runnable {
        override fun run() {
            Log.e("Perc", arPer[countPer])
            tvPerc.text = arPer[countPer]
            countPer += 1
            if (tvPerc.text.toString().trim() != "0%")
                handlerPer.postDelayed(this, 30000)
            else
                tvSubmitStartOver.text = getString(R.string.start_over)
        }
    }


    private fun cancelTimer() {
        if (cTimer != null)
            cTimer.cancel()
    }

    override fun onDestroy() {
        removeHubConnection()
//        cancelTimer()
        hubConnection?.close()
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
            R.id.ivBack -> {
//                if (isTimeOver) {
                onBackPressed()
                /*} else {
                    popupLeaveDeal()
                }*/
            }
            R.id.tvViewOptions -> {
                popupOption()
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
                val card = cardInputWidget.card
                if (card == null) {
                    Toast.makeText(this, "Invalid card data", Toast.LENGTH_LONG).show()
                    return
                }
                val cardSourceParams = SourceParams.createCardParams(card!!)

                stripe.createSource(
                    cardSourceParams,
                    callback = object : ApiResultCallback<Source> {
                        override fun onSuccess(source: Source) {
                            Log.e("Success", Gson().toJson(source))
//                            callPaymentMethodAPI(card)
                        }

                        override fun onError(error: Exception) {
                            Log.e("error", Gson().toJson(error))
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
                removeHubConnection()
                setErrorVisible()
                if (tvSubmitStartOver.text == getString(R.string.try_again)) {
                    callCheckVehicleStockAPI()
                } else if (tvSubmitStartOver.text == getString(R.string.start_over)) {
                    callCheckVehicleStockAPI()
                } else {
                    if (isValidCard()) {
                        if (isValid()) {
                            callBuyerAPI()
                        }
                    }
                }
                /*if (isTimeOver) {
                    onBackPressed()x
                } else if (isValidCard()) {
                    if (isValid()) {
                        callBuyerAPI()
                    }
                }*/
//
            }
            R.id.tvAddMin -> {
                add2Min()
                tvAddMin.visibility = View.GONE
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

    private fun callSubmitPendingUCDDealAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.price] = ucdData.price!!
            request[ApiConstant.zipCode] = yearModelMakeData.zipCode!!
            request[ApiConstant.searchRadius] =
                if (yearModelMakeData.radius!! == "ALL") "6000" else yearModelMakeData.radius!!.replace(
                    " mi",
                    ""
                ).trim()
            request[ApiConstant.loanType] = yearModelMakeData.loanType!!
            request[ApiConstant.initial] = yearModelMakeData.initials!!
            request[ApiConstant.timeZoneOffset] = "-330"
            request[ApiConstant.vehicleInventoryID] = ucdData.vehicleInventoryID!!
            request[ApiConstant.dealID] = ucdData.dealID!!
            request[ApiConstant.guestID] = ucdData.guestID!!
            Log.e("Request", Gson().toJson(request))
            submitPendingUCDDealViewModel.pendingDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callCheckVehicleStockAPI() {
        if (Constant.isOnline(this)) {
            val pkgList = JsonArray()
            for (i in 0 until yearModelMakeData.arPackages?.size!!) {
                pkgList.add(yearModelMakeData.arPackages!![i].vehiclePackageID!!)
            }
            val accList = JsonArray()
            for (i in 0 until yearModelMakeData.arOptions!!.size) {
                accList.add(yearModelMakeData.arOptions!![i].dealerAccessoryID)
            }
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.ProductType] = 3
            request[ApiConstant.YearId1] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.MakeId1] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.ModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.TrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.ExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.InteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.ZipCode1] = yearModelMakeData.zipCode!!
            request[ApiConstant.SearchRadius1] = "6000"
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                                .newTask()
                        )
                    } else {
                        clearPrefSearchDealData()
                        /*startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                                .newTask()
                        )*/
                        startActivity<UnlockedTryAnotherActivity>(
                            ARG_UCD_DEAL to Gson().toJson(ucdData),
                            ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                            ARG_IMAGE_URL to Gson().toJson(arImage),
                            ARG_IMAGE_ID to imageId
                        )
                    }

                }
                )


        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearPrefSearchDealData() {
        pref?.setSearchDealData(Gson().toJson(PrefSearchDealData()))
        pref?.setSearchDealTime("")
    }

    private val REQUEST_CODE_LOCATION = 1001

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            // REQUEST_CODE_LOCATION should be defined on your app level
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.isNotEmpty()
            && grantResults[0] != PackageManager.PERMISSION_GRANTED
        ) {
            throw RuntimeException("Location services are required in order to " + "connect to a reader.")
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

    private fun isValid(): Boolean {
        if (!::cardStripeData.isInitialized) {
            Toast.makeText(this, "enter valid data", Toast.LENGTH_SHORT).show()
            return false
        }
        when {

            TextUtils.isEmpty(edtFirstName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                return false
            }
            TextUtils.isEmpty(edtLastName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                return false
            }
            TextUtils.isEmpty(edtAddress1.text.toString().trim()) -> {
                Constant.setErrorBorder(edtAddress1, tvErrorAddress1)
                return false
            }
            /*  TextUtils.isEmpty(edtAddress2.text.toString().trim()) -> {
                  Constant.setErrorBorder(edtAddress2, tvErrorAddress2)
                  return false
              }*/
            TextUtils.isEmpty(edtCity.text.toString().trim()) -> {
                Constant.setErrorBorder(edtCity, tvErrorCity)
                return false
            }

            TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                return false
            }
            TextUtils.isEmpty(edtPhoneNumber.text.toString().trim()) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                return false
            }
            state == "State" -> {
                tvErrorState.visibility = View.VISIBLE
                return false
            }
            (edtPhoneNumber.text.toString().length != 13) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
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
            ucdData.run {
                tvDialogTitle.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text = vehicleExteriorColor
                tvDialogInteriorColor.text = vehicleInteriorColor
                var accessories = ""
                for (i in 0 until vehicleAccessories?.size!!) {
                    accessories = if (i == 0) {
                        vehicleAccessories[i].accessory!!
                    } else {
                        accessories + ",\n" + vehicleAccessories[i].accessory!!
                    }
                }
                if (accessories.isEmpty())
                    tvDialogOptions.visibility = View.GONE

                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }
                if (packages.isEmpty())
                    tvDialogPackage.visibility = View.GONE

                tvDialogPackage.text = packages
                tvDialogOptions.text = accessories
                if (AppGlobal.isNotEmpty(miles)) {
                    tvDialogDisclosure.text =
                        getString(R.string.miles_approximate_odometer_reading, miles)
                    llDialogDisclosure.visibility = View.VISIBLE
                } else {
                    llDialogDisclosure.visibility = View.GONE
                }
            }
        }
        setLayoutParam(dialog)
        dialog.show()
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

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = java.util.HashMap<String, Any>()
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
                                callPaymentMethodAPI()
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
}