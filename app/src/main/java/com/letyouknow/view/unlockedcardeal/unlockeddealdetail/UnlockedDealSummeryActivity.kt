package com.letyouknow.view.unlockedcardeal.unlockeddealdetail

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUnlockedDealSummeryBinding
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.SubmitPendingUCDDealViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.loadImageUrl
import com.letyouknow.utils.AppGlobal.Companion.setWhiteSpinnerLayoutPos
import com.letyouknow.utils.AppGlobal.Companion.strikeThrough
import com.letyouknow.view.home.dealsummery.delasummreystep2.DealSummeryStep2Activity
import com.letyouknow.view.home.dealsummery.gallery360view.Gallery360TabActivity
import com.letyouknow.view.spinneradapter.FinancingOptionSpinnerAdapter
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import com.pionymessenger.utils.Constant.Companion.ARG_TYPE_VIEW
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL_PENDING
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.pionymessenger.utils.Constant.Companion.makeLinks
import com.pionymessenger.utils.Constant.Companion.setErrorBorder
import kotlinx.android.synthetic.main.activity_unlocked_deal_summery.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import kotlinx.android.synthetic.main.layout_unlocked_deal_summery.*
import org.jetbrains.anko.startActivity


class UnlockedDealSummeryActivity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arLoan = arrayListOf("Financing Option", "Loan", "Cash")
    private var financingStr = "Financing Option"

    private var arImageUrl: ArrayList<String> = ArrayList()

    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel


    private lateinit var binding: ActivityUnlockedDealSummeryBinding

    private lateinit var dataUCDDeal: FindUcdDealData
    private var yearModelMakeData = YearModelMakeData()

    private lateinit var adapterLoan: FinancingOptionSpinnerAdapter
    private lateinit var submitPendingUCDDealViewModel: SubmitPendingUCDDealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_deal_summery)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unlocked_deal_summery)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this).get(ImageIdViewModel::class.java)
        imageUrlViewModel = ViewModelProvider(this).get(ImageUrlViewModel::class.java)
        submitPendingUCDDealViewModel =
            ViewModelProvider(this).get(SubmitPendingUCDDealViewModel::class.java)

        if (intent.hasExtra(Constant.ARG_UCD_DEAL) && intent.hasExtra(Constant.ARG_YEAR_MAKE_MODEL)) {
            dataUCDDeal = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_UCD_DEAL),
                FindUcdDealData::class.java
            )
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            val imageId = intent.getStringExtra(ARG_IMAGE_ID)
            if (imageId?.length != 0)
                callImageUrlAPI(imageId!!)
            binding.lcdDealData = dataUCDDeal

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
        ivForwardDeal.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)
        ivBack.setOnClickListener(this)
//        MainActivity.getInstance().setVisibleEditImg
        backButton()
        tvInfo.text = Html.fromHtml(getString(R.string.if_there_is_match_search_deal))
        scrollTouchListener()
    }

    private var isScrollable = false

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scrollTouchListener() {
//        tvInfo.movementMethod = ScrollingMovementMethod()
        nestedSc.setOnTouchListener { v, event ->
            scrollInfo.parent.requestDisallowInterceptTouchEvent(false)
            false
        }
        scrollInfo.setOnTouchListener { v, event ->
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
                Toast.makeText(this, "Terms of Service Clicked", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(this, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }


    private fun callImageUrlAPI(ImageId: String) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            val request = HashMap<String, Any>()

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
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.vehicleMakeID] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.vehicleModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.vehicleTrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.vehicleExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.vehicleInteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.price] = dataUCDDeal.price!!
            request[ApiConstant.zipCode] = dataUCDDeal.zipCode!!
            request[ApiConstant.searchRadius] = yearModelMakeData.radius!!.replace(" mi", "").trim()
            request[ApiConstant.loanType] = financingStr
            request[ApiConstant.initial] = edtInitials.text.toString().trim()
            request[ApiConstant.timeZoneOffset] = dataUCDDeal.timeZoneOffset!!
            request[ApiConstant.vehicleInventoryID] = dataUCDDeal.vehicleInventoryID!!
            request[ApiConstant.dealID] = dataUCDDeal.dealID!!
            request[ApiConstant.guestID] = dataUCDDeal.guestID!!

            submitPendingUCDDealViewModel.pendingDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    startActivity<UnlockedDealSummeryStep2Activity>(
                        ARG_UCD_DEAL to Gson().toJson(dataUCDDeal),
                        ARG_UCD_DEAL_PENDING to Gson().toJson(data),
                        ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                        ARG_IMAGE_URL to Gson().toJson(arImageUrl)
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
//                startActivity<DealSummeryStep2Activity>()
                if (isValid()) {
                    callSubmitPendingUCDDealAPI()

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
                startActivity<DealSummeryStep2Activity>()
                finish()
            }
            R.id.llGallery -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 0)
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 2)
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
                if (financingStr != "Financing Option") {
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
        if (financingStr == "Financing Option" && !isScrollable && TextUtils.isEmpty(
                edtInitials.text.toString().trim()
            )
        ) {
            tvErrorFinancingOption.visibility = View.VISIBLE
            tvErrorFullDisclouser.visibility = View.VISIBLE
            setErrorBorder(edtInitials, tvErrorInitials)
            return false
        }
        if (financingStr == "Financing Option" && !isScrollable) {
            tvErrorFinancingOption.visibility = View.VISIBLE
            tvErrorFullDisclouser.visibility = View.VISIBLE
            return false
        }
        if (financingStr == "Financing Option" && TextUtils.isEmpty(
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
        if (financingStr == "Financing Option") {
            tvErrorFinancingOption.visibility = View.VISIBLE
            return false
        }
        if (!isScrollable) {
            tvErrorFullDisclouser.visibility = View.VISIBLE
            return false
        }
        if (TextUtils.isEmpty(edtInitials.text.toString().trim())) {
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

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }
}