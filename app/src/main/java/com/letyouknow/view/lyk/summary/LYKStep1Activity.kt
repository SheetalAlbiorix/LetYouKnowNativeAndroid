package com.letyouknow.view.lyk.summary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
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
import com.google.gson.JsonArray
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityLykStep1Binding
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.AppGlobal.Companion.loadImageUrl
import com.letyouknow.utils.AppGlobal.Companion.setWhiteSpinnerLayoutPos
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.lcd.summary.LCDDealSummaryStep2Activity
import com.letyouknow.view.spinneradapter.FinancingOptionSpinnerAdapter
import com.letyouknow.view.spinneradapter.RadiusSpinnerBlackDropAdapter
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IS_BID
import com.pionymessenger.utils.Constant.Companion.ARG_TYPE_VIEW
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.pionymessenger.utils.Constant.Companion.makeLinks
import com.pionymessenger.utils.Constant.Companion.setErrorBorder
import kotlinx.android.synthetic.main.activity_lyk_step1.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.dialog_price_validation.*
import kotlinx.android.synthetic.main.layout_lyk_step1.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LYKStep1Activity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arRadius = arrayListOf(
        "Search Radius",
        "25 mi",
        "50 mi",
        "75 mi",
        "100 mi",
        "150 mi",
        "200 mi",
        "250 mi",
        "300 mi",
        "350 mi",
        "500 mi",
        "1000 mi",
        "ALL"
    )
    private var arLoan = arrayListOf("Financing Option*", "Loan", "Cash")
    private var financingStr = "Financing Option*"
    private var radiusId = "Search Radius"

    private var arImageUrl: ArrayList<String> = ArrayList()

    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel

    private lateinit var binding: ActivityLykStep1Binding
    private lateinit var tokenModel: RefreshTokenViewModel
    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var adapterLoan: FinancingOptionSpinnerAdapter
    private lateinit var adapterRadius: RadiusSpinnerBlackDropAdapter
    private lateinit var zipCodeModel: VehicleZipCodeViewModel
    private lateinit var submitPendingDealViewModel: SubmitPendingDealViewModel

    private var isValidZipCode = false
    private var isBid = false
    private var price = 0.0
    private var imageId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyk_step1)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lyk_step1)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(pref?.getRadius())) {
            Log.e("Data", pref?.getRadius()!!)
            callRadiusAPI()
        }
    }

    override fun onBackPressed() {
        if (isBid) {
            pref?.setBid(true)
        }
        super.onBackPressed()
    }

    private fun init() {
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        imageIdViewModel = ViewModelProvider(this)[ImageIdViewModel::class.java]
        imageUrlViewModel = ViewModelProvider(this)[ImageUrlViewModel::class.java]
        zipCodeModel = ViewModelProvider(this)[VehicleZipCodeViewModel::class.java]
        submitPendingDealViewModel =
            ViewModelProvider(this).get(SubmitPendingDealViewModel::class.java)

        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL)) {
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            callImageIdAPI()
            binding.data = yearModelMakeData
        }
        if (intent.hasExtra(ARG_IS_BID)) {
            isBid = intent.getBooleanExtra(ARG_IS_BID, false)
        }
        txtTerms.text =
            getString(R.string.i_certify_that, getString(R.string.app_name))
        setLoan()
//        setInfoLink()
        setPrivacyPolicyLink()


        btnProceedDeal.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        ivForwardDeal.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)

        ivBack.setOnClickListener(this)
        ivEdit.visibility = View.VISIBLE
//        MainActivity.getInstance().setVisibleEditImg
//        backButton()
        tvInfo.text = Html.fromHtml(getString(R.string.if_there_is_match_submit_price))
        scrollTouchListener()
        onChangeZipCodePrice()
        callRadiusAPI()
        if (isBid)
            setBidPreSelectionData()
    }

    private fun setBidPreSelectionData() {
        edtZipCode.setText(yearModelMakeData.zipCode)
        val formatter: DecimalFormat =
            NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        val formattedString: String = formatter.format(yearModelMakeData.price)

        //setting text after format to EditText
        edtPrice.setText(formattedString)
        edtPrice.setSelection(edtPrice.text.toString().trim().length)
        llViewPrice.visibility = View.GONE
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
    }

    private fun onChangeZipCodePrice() {
        edtZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.length == 5) {
                    callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    isValidZipCode = false
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext)
                }
                pref?.setRadius("")
                callRadiusAPI()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        edtPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()

                if (str.isNotEmpty()) {
                    price = edtPrice.text.toString().replace(",", "").trim().toDouble()
                    popupPrice(price)
                }

            }

            override fun afterTextChanged(s: Editable?) {
                edtPrice.removeTextChangedListener(this)
                try {
                    var originalString = s.toString()
                    val longval: Long
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = originalString.toLong()
                    val formatter: DecimalFormat =
                        NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString: String = formatter.format(longval)

                    //setting text after format to EditText
                    edtPrice.setText(formattedString)
                    edtPrice.setSelection(edtPrice.text.toString().trim().length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                edtPrice.addTextChangedListener(this)

            }

        })
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

    private fun popupPrice(price: Double) {
        llViewPrice.visibility = View.VISIBLE
        tvErrorPrice.visibility = View.GONE
        when {
            price == 0.0 -> {
                tvErrorDialogPrice.visibility = View.VISIBLE
                tvErrorDialogPrice.text = getString(R.string.price_required)

            }
            price < 799 -> {
                tvErrorDialogPrice.visibility = View.VISIBLE
                tvErrorDialogPrice.text = getString(R.string.price_must_be_799_00)

            }
            else -> {
                tvErrorDialogPrice.visibility = View.GONE
            }
        }
        ivPriceCorrect.setOnClickListener {
            edtPrice.setText(edtPrice.text.toString().trim().replace(".00", "") + ".00")
            llViewPrice.visibility = View.GONE
            edtPrice.setSelection(edtPrice.text.toString().length)
            priceError(price)

        }
        ivPriceClose.setOnClickListener {
            edtPrice.hint = "0"
            edtPrice.setText("")
            llViewPrice.visibility = View.GONE
            priceError(price)
        }
    }

    fun priceError(price: Double) {
        when {
            price == 0.0 -> {
                tvErrorPrice.visibility = View.VISIBLE
                tvErrorPrice.text = getString(R.string.price_required)
            }
            price < 799 -> {
                tvErrorPrice.visibility = View.VISIBLE
                tvErrorPrice.text = getString(R.string.price_must_be_799_00)
            }
            else -> {
                tvErrorPrice.visibility = View.GONE
            }
        }
    }

    private fun callVehicleZipCodeAPI(zipCode: String?) {
        if (Constant.isOnline(this)) {
            if (!Constant.progress.isShowing)
                Constant.showLoader(this)
            zipCodeModel.getZipCode(this, zipCode)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    Log.e("ZipCode Data", Gson().toJson(data))
                    if (!data) {
                        /* Toast.makeText(
                             requireActivity(),
                             getString(R.string.invalid_zip_code),
                             Toast.LENGTH_SHORT
                         ).show()*/
                        edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                        tvErrorZipCode.visibility = View.VISIBLE
                        isValidZipCode = false
                    } else {
                        isValidZipCode = true
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun callRadiusAPI() {
        if (!TextUtils.isEmpty(pref?.getRadius())) {
            val radius = pref?.getRadius() + " mi"
            val arData: ArrayList<String> = ArrayList()
            var isMatch = false
            for (i in 0 until arRadius.size) {
                if (radius != arRadius[i]) {
                    if (isMatch)
                        arData.add(arRadius[i])
                } else {
                    isMatch = true
                    arData.add("Search Radius")
                    arData.add(arRadius[i])
                }
            }
            if (isMatch) {
                adapterRadius = RadiusSpinnerBlackDropAdapter(this, arData)
                spRadius.adapter = adapterRadius
                spRadius.onItemSelectedListener = this
            } else {
                adapterRadius = RadiusSpinnerBlackDropAdapter(this, arRadius)
                spRadius.adapter = adapterRadius
                spRadius.onItemSelectedListener = this
            }
        } else {
            adapterRadius = RadiusSpinnerBlackDropAdapter(this, arRadius)
            spRadius.adapter = adapterRadius
            spRadius.onItemSelectedListener = this
            if (isBid) {
                val radius = yearModelMakeData.radius + " mi"
                for (i in 0 until arRadius.size) {
                    if (radius == arRadius[i]) {
                        spRadius.setSelection(i)
                    }
                }
            }
        }
    }

    private var isScrollable = false

    private fun scrollTouchListener() {
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
        for (i in 0 until arLoan.size) {
            if (isBid) {
                if (arLoan[i] == yearModelMakeData.loanType) {
                    spLoan.setSelection(i)
                }
            }
        }
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

    private fun callImageIdAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            yearModelMakeData.run {
                request[ApiConstant.vehicleYearID] = vehicleYearID!!
                request[ApiConstant.vehicleMakeID] = vehicleMakeID!!
                request[ApiConstant.vehicleModelID] = vehicleModelID!!
                request[ApiConstant.vehicleTrimID] = vehicleTrimID!!
            }

            imageIdViewModel.imageIdCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    imageId = data
                    callImageUrlAPI(data)
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(ImageId: String) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = ImageId!!
            request[ApiConstant.ImageProduct] = "Splash"

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

    private fun callSubmitPendingDealAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            val arJsonPackage = JsonArray()
            for (i in 0 until yearModelMakeData.arPackages?.size!!) {
                arJsonPackage.add(yearModelMakeData.arPackages!![i].vehiclePackageID)
            }

            val arJsonAccessories = JsonArray()
            for (i in 0 until yearModelMakeData.arOptions?.size!!) {
                arJsonAccessories.add(yearModelMakeData.arOptions!![i].dealerAccessoryID)
            }


            request[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.price] = edtPrice.text.toString().replace(",", "").trim()
            request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
            request[ApiConstant.searchRadius] =
                if (radiusId == "ALL") "6000" else radiusId.replace(" mi", "")
            request[ApiConstant.loanType] = financingStr
            request[ApiConstant.initial] = edtInitials.text.toString().trim()
            request[ApiConstant.timeZoneOffset] = getTimeZoneOffset()
            request[ApiConstant.dealerAccessoryIDs] = arJsonAccessories
            request[ApiConstant.vehiclePackageIDs] = arJsonPackage
            Log.e("Request pendingDeal", Gson().toJson(request))
            submitPendingDealViewModel.pendingDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    yearModelMakeData.zipCode = edtZipCode.text.toString().trim()
                    yearModelMakeData.price =
                        edtPrice.text.toString().replace(",", "").trim().toFloat()
                    yearModelMakeData.loanType = financingStr
                    yearModelMakeData.initials = edtInitials.text.toString().trim()
                    yearModelMakeData.radius = radiusId

                    startActivity<LYKStep2Activity>(
                        Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
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
                    pref?.setRadius("")
                if (isValid()) {
//                    callRefreshTokenApi()
                    callSubmitPendingDealAPI()
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
                startActivity<Gallery360TabActivity>(
                    ARG_TYPE_VIEW to 0,
                    Constant.ARG_IMAGE_ID to imageId
                )
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(
                    ARG_TYPE_VIEW to 2,
                    Constant.ARG_IMAGE_ID to imageId
                )
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
            R.id.spRadius -> {
                val data = adapterRadius.getItem(position) as String
                radiusId = data
                AppGlobal.setWhiteSpinnerLayoutPos(position, spRadius, this)
                if (data != "Search Radius") {
                    tvErrorRadius.visibility = View.GONE
                }


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
        if (TextUtils.isEmpty(edtZipCode.text.toString().trim())) {
            setErrorBorder(edtZipCode, tvErrorZipCode)
            return false
        }
        if (!isValidZipCode) {
            tvErrorZipCode.text = getString(R.string.invalid_zip_code)
            setErrorBorder(edtZipCode, tvErrorZipCode)
            return false
        }
        if (radiusId == "Search Radius") {
            tvErrorRadius.visibility = View.VISIBLE
            return false
        }
        if (TextUtils.isEmpty(edtPrice.text.toString().trim())) {
            setErrorBorder(edtPrice, tvErrorPrice)
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
            yearModelMakeData.run {

                tvDialogTitle.text =
                    vehicleYearStr + " " + vehicleMakeStr + " " + vehicleModelStr + " " + vehicleTrimStr
                tvDialogExteriorColor.text = vehicleExtColorStr
                tvDialogInteriorColor.text = vehicleIntColorStr
                var accessoriesStr = ""
                var isFirstAcce = true
                val arAccId: ArrayList<String> = ArrayList()
                for (i in 0 until arOptions?.size!!) {
                    if (arOptions!![i].isSelect!!) {
                        arAccId.add(arOptions!![i].dealerAccessoryID!!)
                        if (isFirstAcce) {
                            isFirstAcce = false
                            accessoriesStr = arOptions!![i].accessory!!
                        } else
                            accessoriesStr += ",\n" + arOptions!![i].accessory!!
                    }
                }
                var packageStr = ""
                var isFirstPackage = true

                for (i in 0 until arPackages?.size!!) {
                    if (arPackages!![i].isSelect!!) {
                        if (isFirstPackage) {
                            isFirstPackage = false
                            packageStr = arPackages!![i].packageName!!
                        } else {
                            packageStr = packageStr + ",\n" + arPackages!![i].packageName!!
                        }
                    }
                }
                tvDialogPackage.text = packageStr
                tvDialogOptions.text = accessoriesStr
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
                callSubmitPendingDealAPI()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setErrorVisible() {
        tvErrorInitials.visibility = View.GONE
//        tvErrorPrice.visibility = View.GONE
        tvErrorFinancingOption.visibility = View.GONE
        tvErrorFullDisclouser.visibility = View.GONE
        tvErrorRadius.visibility = View.GONE
        tvErrorZipCode.visibility = View.GONE
    }
}