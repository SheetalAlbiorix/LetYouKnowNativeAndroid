package com.letyouknow.view.lcd.summary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.*
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityLcdDealSummaryStep1Binding
import com.letyouknow.model.FindLCDDeaData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.retrofit.viewmodel.SubmitPendingLCDDealViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.AppGlobal.Companion.loadImageUrl
import com.letyouknow.utils.AppGlobal.Companion.setEmojiKeyBoard
import com.letyouknow.utils.AppGlobal.Companion.setWhiteSpinnerLayoutPos
import com.letyouknow.utils.AppGlobal.Companion.strikeThrough
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IS_LCD
import com.letyouknow.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_VIEW
import com.letyouknow.utils.Constant.Companion.makeLinks
import com.letyouknow.utils.Constant.Companion.setErrorBorder
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.spinneradapter.FinancingOptionSpinnerAdapter
import kotlinx.android.synthetic.main.activity_lcd_deal_summary_step1.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_deal_summary_hot_market.*
import kotlinx.android.synthetic.main.layout_lcd_deal_summary_step1.*
import kotlinx.android.synthetic.main.layout_lcd_deal_summary_step1.ivForwardDeal
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity


class LCDDealSummaryStep1Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arLoan = arrayListOf("Financing Option*", "Loan", "Cash")
    private var financingStr = "Financing Option*"

    private var arImageUrl: ArrayList<String> = ArrayList()

    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel
    private lateinit var tokenModel: RefreshTokenViewModel


    private lateinit var binding: ActivityLcdDealSummaryStep1Binding
    private lateinit var dataLCDDeal: FindLCDDeaData
    private lateinit var adapterLoan: FinancingOptionSpinnerAdapter
    private lateinit var submitPendingLCDDealViewModel: SubmitPendingLCDDealViewModel
    private var imageId = "0"
    private var isLCD = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lcd_deal_summary_step1)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_deal_summary_step1)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this)[ImageIdViewModel::class.java]
        imageUrlViewModel = ViewModelProvider(this)[ImageUrlViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        submitPendingLCDDealViewModel =
            ViewModelProvider(this)[SubmitPendingLCDDealViewModel::class.java]

        if (intent.hasExtra(ARG_LCD_DEAL_GUEST)) {
            dataLCDDeal = Gson().fromJson(
                intent.getStringExtra(ARG_LCD_DEAL_GUEST),
                FindLCDDeaData::class.java
            )
            binding.lcdDealData = dataLCDDeal
            if (dataLCDDeal.dealID == "0") {
                tvTitleTool.visibility = View.INVISIBLE
            }
        }

        if (intent.hasExtra(ARG_IS_LCD)) {
            isLCD = intent.getBooleanExtra(ARG_IS_LCD, false)
        }

        txtTerms.text =
            getString(R.string.i_certify_that, getString(R.string.app_name))
        strikeThrough(tvPrice)
        setLoan()
//        setInfoLink()
        setPrivacyPolicyLink()


        btnProceedDeal.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        ivEdit.visibility = View.VISIBLE
        ivForwardDeal.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)
        tvViewOptionsHot.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        btnGoBack.setOnClickListener(this)
//        MainActivity.getInstance().setVisibleEditImg
        backButton()
        tvInfo.text = Html.fromHtml(getString(R.string.if_there_is_match))
        scrollTouchListener()
//        callRefreshTokenApi()
        callImageIdAPI()
        onChangeInitials()
        setEmojiKeyBoard(edtInitials)
        edtInitials.filters = arrayOf(letterFilter, InputFilter.LengthFilter(3))
    }

    private fun onChangeInitials() {
        edtInitials.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isScrollable) {
                    tvErrorFullDisclouser.visibility = View.VISIBLE
                    tvErrorInitials.visibility = View.VISIBLE
                    edtInitials.isEnabled = false
                    if (!TextUtils.isEmpty(edtInitials.text.toString().trim()))
                        edtInitials.setText("")
                } else {
                    val str = s.toString()
                    when {
                        str.isEmpty() -> {
                            tvErrorInitials.text = getString(R.string.initials_required)
                            setErrorBorder(edtInitials, tvErrorInitials)
                        }
                        str.length == 1 -> {
                            tvErrorInitials.text =
                                getString(R.string.initials_must_be_valid_2_or_3_letters)
                            setErrorBorder(edtInitials, tvErrorInitials)
                        }
                        else -> {
                            edtInitials.setBackgroundResource(R.drawable.bg_edittext)
                            tvErrorInitials.visibility = View.GONE
                        }
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private var isScrollable = false

    private fun scrollTouchListener() {
//        tvInfo.movementMethod = ScrollingMovementMethod()
        nestedSc.setOnTouchListener { _, _ ->
            scrollInfo.parent.requestDisallowInterceptTouchEvent(false)
            false
        }
        scrollInfo.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        scrollInfo.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollInfo.getChildAt(0).bottom
                    <= scrollInfo.height + scrollInfo.scrollY
                ) {
                    isScrollable = true
                    tvErrorFullDisclouser.visibility = View.GONE
                    edtInitials.isEnabled = true
                    //  Log.e("bottom", "scroll view is at bottom")
                } else {
                    //   Log.e("Top", "scroll view is not at bottom")
                }
            }
    }


    private fun backButton() {
//        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

//        setSupportActionBar(toolbar)
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

    private fun setLoan() {
        adapterLoan = FinancingOptionSpinnerAdapter(this, arLoan)
        spLoan.adapter = adapterLoan
        setWhiteSpinnerLayoutPos(0, spLoan, this)
        spLoan.onItemSelectedListener = this
    }

    private fun setPrivacyPolicyLink() {
        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                AppGlobal.dialogWebView(this, Constant.TERMS_CONDITIONS_LINK)
            }),
            Pair("Privacy Policy", View.OnClickListener {
                /*Toast.makeText(this, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()*/
                AppGlobal.dialogWebView(this, Constant.PRIVACY_POLICY_LINK)
            })
        )
    }

    private fun callImageIdAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            dataLCDDeal.run {
                request[ApiConstant.vehicleYearID] = yearId!!
                request[ApiConstant.vehicleMakeID] = makeId!!
                request[ApiConstant.vehicleModelID] = modelId!!
                request[ApiConstant.vehicleTrimID] = trimId!!
            }

            imageIdViewModel.imageIdCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    imageId = data
                    callImageUrlAPI(data)
                    if (imageId == "0") {
                        llGallery.visibility = View.GONE
                        ll360.visibility = View.GONE
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(ImageId: String) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = ImageId!!
            if (dataLCDDeal.exteriorColorId!! == "0") {
                request[ApiConstant.ImageProduct] = "Splash"
            } else {
                request[ApiConstant.ImageProduct] = "MultiAngle"
                request[ApiConstant.ExteriorColor] = dataLCDDeal.exteriorColorStr!!
            }

            imageUrlViewModel.imageUrlCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data.isNotEmpty()) {

                        arImageUrl = data
                        loadImageUrl(this, ivMain, arImageUrl[0])
                        loadImageUrl(this, ivBg360, arImageUrl[0])
                        loadImageUrl(this, ivBgGallery, arImageUrl[0])
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callSubmitPendingLCDDealAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val jsonPkg = JsonArray()
            for (i in 0 until dataLCDDeal.arPackageId.size) {
                jsonPkg.add(dataLCDDeal.arPackageId[i])
            }
            val jsonAcc = JsonArray()
            for (i in 0 until dataLCDDeal.arAccessoriesId.size) {
                jsonAcc.add(dataLCDDeal.arAccessoriesId[i])
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = dataLCDDeal.yearId!!
            request[ApiConstant.vehicleMakeID] = dataLCDDeal.makeId!!
            request[ApiConstant.vehicleModelID] = dataLCDDeal.modelId!!
            request[ApiConstant.vehicleTrimID] = dataLCDDeal.trimId!!
            request[ApiConstant.vehicleExteriorColorID] = dataLCDDeal.exteriorColorId!!
            request[ApiConstant.vehicleInteriorColorID] = dataLCDDeal.interiorColorId!!
            request[ApiConstant.price] = dataLCDDeal.price!!
            request[ApiConstant.zipCode] = dataLCDDeal.zipCode!!
            request[ApiConstant.searchRadius] = "100"
            request[ApiConstant.loanType] = financingStr
            request[ApiConstant.initial] = edtInitials.text.toString().trim()
            request[ApiConstant.timeZoneOffset] = getTimeZoneOffset()
            request[ApiConstant.vehicleInventoryID] = dataLCDDeal.vehicleInventoryID!!
            request[ApiConstant.dealID] = dataLCDDeal.dealID!!
            request[ApiConstant.guestID] = dataLCDDeal.guestID!!
            request[ApiConstant.dealerAccessoryIDs] = jsonAcc
            request[ApiConstant.vehiclePackageIDs] = jsonPkg
            //  Log.e("requestsubmitpending ", Gson().toJson(request))
            submitPendingLCDDealViewModel.pendingDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    dataLCDDeal.loanType = financingStr
                    dataLCDDeal.initial = edtInitials.text.toString().trim()
                    startActivity<LCDDealSummaryStep2Activity>(
                        Constant.ARG_LCD_DEAL_GUEST to Gson().toJson(dataLCDDeal),
                        Constant.ARG_UCD_DEAL_PENDING to Gson().toJson(data),
                        Constant.ARG_IMAGE_URL to Gson().toJson(arImageUrl),
                        Constant.ARG_IMAGE_ID to imageId
                    )
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceedDeal -> {
                setErrorVisible()
                if (isValid()) {
                    callSubmitPendingLCDDealAPI()
                }
                //loadFragment(DealSummeryStep2Activity())
            }
            R.id.ivBackDeal -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
            R.id.ivForwardDeal -> {
                startActivity<LCDDealSummaryStep2Activity>()
                finish()
            }
            R.id.llGallery -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 0, ARG_IMAGE_ID to imageId)
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 2, ARG_IMAGE_ID to imageId)
            }
            R.id.tvViewOptions -> {
                popupOption()
            }
            R.id.tvViewOptionsHot -> {
                popupOption()
            }
            R.id.btnGoBack -> {
                finish()
            }

            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (isLCD) {
            startActivity(
                intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_ONE_DEAL_NEAR_YOU).clearTask()
                    .newTask()
            )
        }
        super.onBackPressed()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            R.id.spLoan -> {
                val data = adapterLoan.getItem(position) as String
                financingStr = data
                if (financingStr != "Financing Option*") {
                    tvErrorFinancingOption.visibility = View.GONE
                }
                setWhiteSpinnerLayoutPos(position, spLoan, this)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun isValid(): Boolean {
        if (financingStr == "Financing Option*" && !isScrollable && TextUtils.isEmpty(
                edtInitials.text.toString().trim()
            )
        ) {
            tvErrorFinancingOption.visibility = View.VISIBLE
            tvErrorFullDisclouser.visibility = View.VISIBLE
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        if (financingStr == "Financing Option*" && !isScrollable) {
            tvErrorFinancingOption.visibility = View.VISIBLE
            tvErrorFullDisclouser.visibility = View.VISIBLE
            return false
        }
        if (financingStr == "Financing Option*" && TextUtils.isEmpty(
                edtInitials.text.toString().trim()
            )
        ) {
            tvErrorFinancingOption.visibility = View.VISIBLE
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        if (!isScrollable && TextUtils.isEmpty(edtInitials.text.toString().trim())) {
            tvErrorFullDisclouser.visibility = View.VISIBLE
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        if (financingStr == "Financing Option*") {
            tvErrorFinancingOption.visibility = View.VISIBLE
            return false
        }
        if (!isScrollable) {
            tvErrorFullDisclouser.visibility = View.VISIBLE
            return false
        }
        if (TextUtils.isEmpty(edtInitials.text.toString().trim())) {
            tvErrorInitials.text = getString(R.string.initials_required)
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        if (edtInitials.text.toString().trim().length == 1) {
            tvErrorInitials.text = "Initials must be valid - 2 or 3 Letters"
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        return true
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

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = java.util.HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(this, request)!!.observe(this, { data ->
                Constant.dismissLoader()
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                callImageIdAPI()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setErrorVisible() {
        tvErrorFinancingOption.visibility = View.GONE
        tvErrorInitials.visibility = View.GONE
        tvErrorFullDisclouser.visibility = View.GONE
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
    }

    var letterFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
            var filtered = ""
            for (i in start until end) {
                val character = source[i]
                if (!Character.isWhitespace(character) && character != '??' && Character.isLetter(
                        character
                    )
                ) {
                    filtered += character
                }
            }
            filtered
        }
}