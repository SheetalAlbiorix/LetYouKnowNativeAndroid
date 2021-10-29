package com.letyouknow.view.home.dealsummary.delasummarystep2

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityDealSummaryStep2Binding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.arState
import com.letyouknow.utils.AppGlobal.Companion.formatPhoneNo
import com.letyouknow.utils.CreditCardNumberTextWatcher
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.home.dealsummary.gallery360view.Gallery360TabActivity
import com.letyouknow.view.signup.CardListAdapter
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import com.letyouknow.view.unlockedcardeal.submitdealsummary.SubmitDealSummaryActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import com.pionymessenger.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import kotlinx.android.synthetic.main.activity_deal_summary_step2.*
import kotlinx.android.synthetic.main.dialog_leave_my_deal.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_deal_summary_step2.*
import kotlinx.android.synthetic.main.layout_toolbar_timer.*
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.ArrayList


class DealSummaryStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityDealSummaryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer
    private var seconds = 300

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_summary_step2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_summary_step2)
        init()
    }

    private fun init() {
        lykDollarViewModel = ViewModelProvider(this).get(LYKDollarViewModel::class.java)
        promoCodeViewModel = ViewModelProvider(this).get(PromoCodeViewModel::class.java)
        paymentMethodViewModel = ViewModelProvider(this).get(PaymentMethodViewModel::class.java)
        buyerViewModel = ViewModelProvider(this).get(BuyerViewModel::class.java)
        submitDealLCDViewModel = ViewModelProvider(this).get(SubmitDealLCDViewModel::class.java)
        tokenModel = ViewModelProvider(this).get(RefreshTokenViewModel::class.java)

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
            binding.lightDealBindData = lightBindData
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }
            callRefreshTokenApi()
            if (dataPendingDeal.buyer?.phoneNumber?.contains("(") == false)
                edtPhoneNumber.setText(formatPhoneNo(dataPendingDeal.buyer?.phoneNumber))
            else
                edtPhoneNumber.setText(dataPendingDeal.buyer?.phoneNumber)
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
//        ivEdit.setOnClickListener(this)
        ivBack.setOnClickListener(this)

        startTimer()
        setState()
        AppGlobal.strikeThrough(tvPrice)

        edtPhoneNumber.filters =
            arrayOf<InputFilter>(filter, InputFilter.LengthFilter(13))//        backButton()
        onStateChange()
        initPayment()
        setOnChangeCard()
    }

    private lateinit var stripe: Stripe

    private fun initPayment() {
        stripe =
            Stripe(this, Objects.requireNonNull(getString(R.string.stripe_publishable_key)))
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
            if (arState[i] == dataPendingDeal.buyer?.state) {
                spState.setSelection(i)
            }
        }
    }


    private fun callDollarAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            lykDollarViewModel.getDollar(this, dataPendingDeal.dealID)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data == "0.00") {
                        llDollar.visibility = View.GONE
                    }
                    tvDollar.text = "$$data"
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
            map[ApiConstant.buyerId] = dataPendingDeal.buyer?.buyerId!!
            map[ApiConstant.firstName] = edtFirstName.text.toString().trim()
            map[ApiConstant.middleName] = edtFirstName.text.toString().trim()
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
            map[ApiConstant.dealID] = dataPendingDeal.dealID!!
            map[ApiConstant.buyerID] = dataPendingDeal.buyer?.buyerId!!
            map[ApiConstant.payment_method_id] = cardStripeData.id!!
            map[ApiConstant.card_last4] = cardStripeData.card?.last4!!
            map[ApiConstant.card_brand] = cardStripeData.card?.brand!!
            map[ApiConstant.vehicleYearID] = dataLCDDeal.yearId!!
            map[ApiConstant.vehicleMakeID] = dataLCDDeal.makeId!!
            map[ApiConstant.vehicleModelID] = dataLCDDeal.modelId!!
            map[ApiConstant.vehicleTrimID] = dataLCDDeal.trimId!!
            map[ApiConstant.vehicleExteriorColorID] = dataLCDDeal.exteriorColorId!!
            map[ApiConstant.vehicleInteriorColorID] = dataLCDDeal.interiorColorId!!
            map[ApiConstant.price] = dataLCDDeal.price!!
            map[ApiConstant.timeZoneOffset] = "-330"
            map[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
            map[ApiConstant.searchRadius] = "6000"
            map[ApiConstant.loanType] = dataLCDDeal.loanType!!
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

            map[ApiConstant.dealerAccessoryIDs] = arJsonPackage
            map[ApiConstant.vehiclePackageIDs] = arJsonAccessories

            submitDealLCDViewModel.submitDealLCDCall(this, map)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    data.successResult.transactionInfo.vehiclePrice = dataLCDDeal.price!!
                    data.successResult.transactionInfo.remainingBalance =
                        (dataLCDDeal.price!! - (799.0f + dataLCDDeal.discount!!))
                    Log.e("data Deal", Gson().toJson(data))
                    startActivity<SubmitDealSummaryActivity>(ARG_SUBMIT_DEAL to Gson().toJson(data))
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
                dataPendingDeal.dealID
            )!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    if (data.discount!! > 0) {
                        tvPromoData.visibility = View.VISIBLE
                        tvPromoData.text = "-$${data.discount}"
                        dataLCDDeal.discount = data.discount!!
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
        cTimer = object : CountDownTimer(((seconds * 1000)).toLong(), 1000) {
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
                if (seconds == 0) {
                    tvAddMin.visibility = View.GONE
                    tvTimer.visibility = View.GONE
                    llExpired.visibility = View.VISIBLE
                    isTimeOver = true
                    tvSubmitStartOver.text = getString(R.string.start_over)
                    cancelTimer()
                    return
                }
            }

            override fun onFinish() {

            }
        }.start()
    }

    private fun cancelTimer() {
        if (cTimer != null)
            cTimer.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
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
                if (isTimeOver) {
                    onBackPressed()
                } else {
                    popupLeaveDeal()
                }
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
                setErrorVisible()
                if (isTimeOver) {
                    onBackPressed()
                } else if (isValidCard()) {
                    if (isValid()) {
                        callBuyerAPI()
                    }
                }
            }
            R.id.tvAddMin -> {
                seconds += 120;
                //seconds = ((60 * ((2 + minutes) + (second / 60))).toDouble())
                tvAddMin.visibility = View.GONE
                cancelTimer()
                startTimer()
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

    private fun isValid(): Boolean {
        /*if (!::cardStripeData.isInitialized) {
            Toast.makeText(this, "enter valid data", Toast.LENGTH_SHORT).show()
            return false
        }*/
        when {
            edtCardZipCode.text.toString().trim().length != 5 -> {
                tvErrorCardZip.visibility = View.VISIBLE
                tvErrorCardZip.text = getString(R.string.zipcode_must_be_valid_digits)
                return false
            }
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
            Constant.showLoader(this)
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