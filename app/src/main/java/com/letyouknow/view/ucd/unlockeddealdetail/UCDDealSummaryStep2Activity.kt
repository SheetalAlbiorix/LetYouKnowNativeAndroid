package com.letyouknow.view.ucd.unlockeddealdetail

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUcdDealSummaryStep2Binding
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.retrofit.viewmodel.SubmitPendingUCDDealViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.AppGlobal.Companion.loadImageUrl
import com.letyouknow.utils.AppGlobal.Companion.setWhiteSpinnerLayoutPos
import com.letyouknow.utils.AppGlobal.Companion.strikeThrough
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_URL
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_VIEW
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.utils.Constant.Companion.makeLinks
import com.letyouknow.utils.Constant.Companion.setErrorBorder
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.lcd.summary.LCDDealSummaryStep2Activity
import com.letyouknow.view.spinneradapter.FinancingOptionSpinnerAdapter
import kotlinx.android.synthetic.main.activity_ucd_deal_summary_step2.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import kotlinx.android.synthetic.main.layout_ucd_deal_summary_step2.*
import org.jetbrains.anko.startActivity


class UCDDealSummaryStep2Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arLoan = arrayListOf("Financing Option*", "Loan", "Cash")
    private var financingStr = "Financing Option*"

    private var arImageUrl: ArrayList<String> = ArrayList()

    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel


    private lateinit var binding: ActivityUcdDealSummaryStep2Binding

    private lateinit var dataUCDDeal: FindUcdDealData
    private var yearModelMakeData = YearModelMakeData()
    private var tokenModel = RefreshTokenViewModel()

    private lateinit var adapterLoan: FinancingOptionSpinnerAdapter
    private lateinit var submitPendingUCDDealViewModel: SubmitPendingUCDDealViewModel

    private var imageId = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucd_deal_summary_step2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ucd_deal_summary_step2)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this)[ImageIdViewModel::class.java]
        imageUrlViewModel = ViewModelProvider(this)[ImageUrlViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        submitPendingUCDDealViewModel =
            ViewModelProvider(this)[SubmitPendingUCDDealViewModel::class.java]

        if (intent.hasExtra(Constant.ARG_UCD_DEAL) && intent.hasExtra(Constant.ARG_YEAR_MAKE_MODEL)) {
            dataUCDDeal = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_UCD_DEAL),
                FindUcdDealData::class.java
            )
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            if (imageId?.length != 0)
                callImageUrlAPI(imageId!!)

            if (imageId == "0") {
                ll360.visibility = View.GONE
                llGallery.visibility = View.GONE
            }
            binding.lcdDealData = dataUCDDeal

            if (AppGlobal.isNotEmpty(dataUCDDeal.miles) || AppGlobal.isNotEmpty(dataUCDDeal.condition)) {
                if (AppGlobal.isNotEmpty(dataUCDDeal.miles))
                    tvDisclosure.text =
                        getString(R.string.miles_approximate_odometer_reading, dataUCDDeal.miles)

                if (AppGlobal.isNotEmpty(dataUCDDeal.condition)) {
                    if (AppGlobal.isEmpty(dataUCDDeal.miles)) {
                        tvDisclosure.text = dataUCDDeal.condition
                    } else {
                        tvDisclosure.text =
                            tvDisclosure.text.toString().trim() + ", " + dataUCDDeal.condition
                    }
                }
                llDisc.visibility = View.VISIBLE
            } else {
                llDisc.visibility = View.GONE
            }
        }
        txtTerms.text =
            getString(R.string.i_certify_that, getString(R.string.app_name))
        strikeThrough(tvMSRP)
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
        ivBack.setOnClickListener(this)
//        MainActivity.getInstance().setVisibleEditImg
        backButton()
        tvInfo.text = Html.fromHtml(getString(R.string.if_there_is_match_search_deal))
        scrollTouchListener()
        onChangeInitials()
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
                            tvErrorInitials.text = "Initials must be valid - 2 or 3 Letters"
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
        nestedSc.setOnTouchListener { v, event ->
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
                    Log.e("bottom", "scroll view is at bottom")
                } else {
                    Log.e("Top", "scroll view is not at bottom")
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
                AppGlobal.dialogWebView(this, Constant.PRIVACY_POLICY_LINK)
            })
        )
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
            /*if(yearModelMakeData.vehicleExtColorID!! == "0"){
                request[ApiConstant.ImageProduct] = "Splash"
            }else {
                request[ApiConstant.ImageProduct] = "MultiAngle"
                request[ApiConstant.ExteriorColor] = dataUCDDeal.vehicleExteriorColor!!
            }*/
            request[ApiConstant.ImageId] = ImageId!!
            request[ApiConstant.ImageProduct] = "MultiAngle"
            request[ApiConstant.ExteriorColor] = dataUCDDeal.vehicleExteriorColor!!

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

    private fun callSubmitPendingUCDDealAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.price] = dataUCDDeal.price!!
            request[ApiConstant.zipCode] = yearModelMakeData.zipCode!!
            request[ApiConstant.searchRadius] =
                if (yearModelMakeData.radius!! == "ALL") "6000" else yearModelMakeData.radius!!.replace(
                    " mi",
                    ""
                ).trim()
            request[ApiConstant.loanType] = financingStr
            request[ApiConstant.initial] = edtInitials.text.toString().trim()
            request[ApiConstant.timeZoneOffset] = getTimeZoneOffset()
            request[ApiConstant.vehicleInventoryID] = dataUCDDeal.vehicleInventoryID!!
            request[ApiConstant.dealID] = dataUCDDeal.dealID!!
            request[ApiConstant.guestID] = dataUCDDeal.guestID!!
            Log.e("Request", Gson().toJson(request))
            submitPendingUCDDealViewModel.pendingDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    yearModelMakeData.loanType = financingStr
                    yearModelMakeData.initials = edtInitials.text.toString().trim()

                    startActivity<UCDDealSummaryStep3Activity>(
                        ARG_UCD_DEAL to Gson().toJson(dataUCDDeal),
                        ARG_UCD_DEAL_PENDING to Gson().toJson(data),
                        ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                        ARG_IMAGE_URL to Gson().toJson(arImageUrl),
                        ARG_IMAGE_ID to imageId
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
                    callSubmitPendingUCDDealAPI()
//                    callRefreshTokenApi()
                }
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
            R.id.ivBack -> {
                onBackPressed()
            }
        }
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
            dataUCDDeal.run {


                tvDialogTitle.text =
                    dataUCDDeal.vehicleYear + " " + vehicleMake + " " + vehicleModel + " " + vehicleTrim
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
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDialogDisclosure.text =
                            getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDialogDisclosure.text = condition
                        } else {
                            tvDialogDisclosure.text =
                                tvDialogDisclosure.text.toString().trim() + ", " + condition
                        }
                    }
                    llDialogDisclosure.visibility = View.VISIBLE
                } else {
                    llDialogDisclosure.visibility = View.GONE
                }

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

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (!Constant.progress.isShowing) {
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
                callSubmitPendingUCDDealAPI()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setErrorVisible() {
        tvErrorInitials.visibility = View.GONE
        tvErrorFullDisclouser.visibility = View.GONE
        tvErrorFinancingOption.visibility = View.GONE
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