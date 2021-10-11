package com.letyouknow.view.unlockedcardeal.unlockeddealdetail

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUnlockedDealSummeryStep2Binding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.LYKDollarViewModel
import com.letyouknow.retrofit.viewmodel.PaymentMethodViewModel
import com.letyouknow.retrofit.viewmodel.PromoCodeViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.CreditCardNumberTextWatcher
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.signup.CardListAdapter
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.stripe.android.Stripe
import kotlinx.android.synthetic.main.activity_unlocked_deal_summery_step2.*
import kotlinx.android.synthetic.main.dialog_leave_my_deal.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_timer.*
import kotlinx.android.synthetic.main.layout_unlocked_deal_summery_step2.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UnlockedDealSummeryStep2Activity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityUnlockedDealSummeryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer
    private var seconds = 300
    private var isFirst60 = true
    private var imageUrl = ""

    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var pendingUCDData: SubmitPendingUcdData
    private lateinit var lykDollarViewModel: LYKDollarViewModel
    private lateinit var promoCodeViewModel: PromoCodeViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodViewModel
    private lateinit var ucdData: FindUcdDealData
    private lateinit var arImage: ArrayList<String>
    private var isTimeOver = false
    private var arState = arrayListOf("State")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_deal_summery_step2)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_unlocked_deal_summery_step2)
        init()
    }

    private fun init() {
        stripe = Stripe(this, getString(R.string.stripe_publishable_key))
        lykDollarViewModel = ViewModelProvider(this).get(LYKDollarViewModel::class.java)
        promoCodeViewModel = ViewModelProvider(this).get(PromoCodeViewModel::class.java)
        paymentMethodViewModel = ViewModelProvider(this).get(PaymentMethodViewModel::class.java)
        if (intent.hasExtra(ARG_UCD_DEAL) && intent.hasExtra(ARG_UCD_DEAL_PENDING) && intent.hasExtra(
                ARG_YEAR_MAKE_MODEL
            ) && intent.hasExtra(
                ARG_IMAGE_URL
            )
        ) {
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
            callDollarAPI()

        }
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber)
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

        setOnChange()
        startTimer()
//        backButton()
        onStateChange()
        setState()
        AppGlobal.strikeThrough(tvPrice)

        edtPhoneNumber.filters = arrayOf<InputFilter>(filter, InputFilter.LengthFilter(13))
        edtPhoneNumber.setText(pendingUCDData.buyer?.phoneNumber)
        stripPayment()
    }

    private var stripe: Stripe? = null

    private fun stripPayment() {
        val paymentMethodCreateParams =
            cardInputWidget
        /* if (paymentMethodCreateParams != null)
             addStripeCard(paymentMethodCreateParams)*/
    }

    /* private fun addStripeCard(paymentMethodCreateParams: PaymentMethodCreateParams) {
         stripe!!.createPaymentMethod(
             paymentMethodCreateParams, null,
             object : ApiResultCallback<PaymentMethod> {
                 override fun onSuccess(result: PaymentMethod) {

                     // add a call to own server to save the details
                     Log.e("Payment Result", Gson().toJson(result))
                 }

                 override fun onError(e: java.lang.Exception) {
                     Log.e("Payment Error", e.message!!)
                 }

             })
     }
 */
    private fun callDollarAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            lykDollarViewModel.getDollar(this, pendingUCDData.dealID)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    tvDollar.text = "$$data"
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callPaymentMethodAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request: HashMap<String, Any> = HashMap()
            request[ApiConstant.type] = "card"
            request[ApiConstant.cardnumber] = "4242424242424242"
            request[ApiConstant.cardcvc] = "542"
            request[ApiConstant.card_exp_month] = "12"
            request[ApiConstant.card_exp_year] = "22"
            request[ApiConstant.billing_details] = "27519"
            request[ApiConstant.guid] = "ab09ffc0-f83e-4ecc-b198-eb375bfbbc57b41768"
            request[ApiConstant.muid] = "c25c4e63-970f-4c89-b9e1-9c983c4a99224e8f02"
            request[ApiConstant.sid] = "a7908fc4-1b17-44f4-801c-0d404cee2f1ad7ad95"
            request[ApiConstant.time_on_page] = "899065"
            request[ApiConstant.key] =
                "pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA"

            paymentMethodViewModel.callPayment(this, request)!!
                .observe(this, { data ->
                    Constant.dismissLoader()
                    setClearData()
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

    private fun setState() {
        val adapterYear = ArrayAdapter<String?>(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arState as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterYear
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
                }
                if (seconds == 0) {
//                    tvDealGuaranteed.text = "Reserved Deal has expired"
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

    private fun setOnChange() {
        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var working = edtExpiresDate.text.toString()
                var isValid = true
                if (working.length == 2 && before == 0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                        isValid = false
                    } else {
                        working += "/"
                        edtExpiresDate.setText(working)
                        edtExpiresDate.setSelection(working.length)
                    }
                } else if (working.length == 5 && before == 0) {
                    val enteredYear = working.substring(3)
                    val currentYear = Calendar.getInstance()
                        .get(Calendar.YEAR) % 100//getting last 2 digits of current year i.e. 2018 % 100 = 18
                    if (Integer.parseInt(enteredYear) < currentYear) {
                        isValid = false
                    }
                } else if (working.length != 5) {
                    isValid = false
                }

                if (!isValid) {
                    edtExpiresDate.error = getString(R.string.enter_valid_date_mm_yy)
                } else {
                    edtExpiresDate.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val inputLength = edtExpiresDate.text.toString().length
                /* if (inputLength == 2) {
                     edtExpiresDate.setText(edtExpiresDate.text.toString().trim() + "/")
                     edtExpiresDate.setSelection(edtExpiresDate.text.toString().length)
                 }*/

                /*if (inputLength == 5) {
                    edtCVV.requestFocus()
                }*/
            }
        })
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
            R.id.ivBack -> {
                if (isTimeOver) {
                    onBackPressed()
                } else {
                    popupLeaveDeal()
                }
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
                /*arCardList.add(
                    CardListData(
                        getDetectedCreditCardImage(),
                        edtCardNumber.text.toString().trim(),
                        edtCardHolder.text.toString().trim(),
                        edtExpiresDate.text.toString().trim(),
                        edtCVV.text.toString().trim(),
                        false
                    )
                )
                adapterCardList.addAll(arCardList)
                pref?.setCardList(Gson().toJson(arCardList))
                llCardList.visibility = View.VISIBLE*/
//                checkPermission()

                callPaymentMethodAPI()
            }
            R.id.ivBackDeal -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
            R.id.btnProceedDeal -> {
                if (isTimeOver) {
                    onBackPressed()
                } else if (isValid()) {
                    startActivity(
                        intentFor<MainActivity>().clearTask().newTask()
                    )
                }
//
            }
            R.id.tvAddMin -> {
                seconds += 120;
                //seconds = ((60 * ((2 + minutes) + (second / 60))).toDouble())
                tvAddMin.visibility = View.GONE
                cancelTimer()
                startTimer()
            }
        }
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
        edtCardHolder.setText("")
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
                dismiss()
                startActivity(
                    intentFor<MainActivity>().clearTask().newTask()
                )
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
}