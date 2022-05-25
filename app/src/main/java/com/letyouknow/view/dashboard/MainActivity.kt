package com.letyouknow.view.dashboard

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.fcm.MyFirebaseMessageService
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_DEAL_ID
import com.letyouknow.utils.Constant.Companion.ARG_IS_LCD
import com.letyouknow.utils.Constant.Companion.ARG_IS_NOTIFICATION
import com.letyouknow.utils.Constant.Companion.ARG_SEL_TAB
import com.letyouknow.utils.Constant.Companion.ARG_TITLE
import com.letyouknow.utils.Constant.Companion.ARG_TRANSACTION_CODE
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DATA
import com.letyouknow.utils.Constant.Companion.ARG_WEB_URL
import com.letyouknow.utils.Constant.Companion.TYPE_ONE_DEAL_NEAR_YOU
import com.letyouknow.utils.Constant.Companion.TYPE_SEARCH_DEAL
import com.letyouknow.utils.Constant.Companion.TYPE_SUBMIT_PRICE
import com.letyouknow.view.account.AccountFragment
import com.letyouknow.view.bidhistory.BidHistoryActivity
import com.letyouknow.view.dashboard.drawer.DrawerListAdapter
import com.letyouknow.view.dealnearyou.OneDealNearYouFragment
import com.letyouknow.view.howitworkhelp.HowItWorkHelpWebViewActivity
import com.letyouknow.view.login.LoginActivity
import com.letyouknow.view.lyk.LYKFragment
import com.letyouknow.view.lyk.summary.LYKStep1Activity
import com.letyouknow.view.referreferral.ReferReferralActivity
import com.letyouknow.view.transaction_history.TransactionCodeDetailActivity
import com.letyouknow.view.transaction_history.TransactionHistoryActivity
import com.letyouknow.view.ucd.UCDDealListStep1Activity
import com.letyouknow.view.ucd.UCDDealListStep1NewActivity
import com.letyouknow.view.ucd.UCDFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_logout.*
import kotlinx.android.synthetic.main.dialog_mobile_no.*
import kotlinx.android.synthetic.main.layout_nav_drawer.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity(),
    View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var adapterDrawer: DrawerListAdapter
    private lateinit var userProfileViewModel: UserProfileViewModel
    private var arDrawer: ArrayList<DrawerData> = ArrayList()
    lateinit var socialMobileViewModel: SocialMobileViewModel
    lateinit var removePushTokenViewModel: RemovePushTokenViewModel
    private lateinit var findUCDDealGuestViewModel: FindUCDDealViewModel
    private lateinit var activeMatchingDealViewModel: ActiveMatchingDealViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(ARG_IS_NOTIFICATION)) {
                showNotification(intent)
            }
        }
    }

    companion object {
        private lateinit var main: MainActivity
        fun getInstance(): MainActivity {
            return main
        }
    }

    fun setTitle(title: String) {
        tvMainTitle.text = title
    }

    fun setVisibleEditImg(isVisible: Boolean) {
        if (isVisible) {
            ivEdit.visibility = View.GONE
            toolbar.backgroundColor = resources.getColor(R.color.colord3e6ff)
            toolbar.elevation = 0f
        } else {
            ivEdit.visibility = View.INVISIBLE
            toolbar.backgroundColor = resources.getColor(R.color.white)
            toolbar.elevation = 8f
        }
    }

    fun setVisibleLogoutImg(isVisible: Boolean) {
        ivLogOut.visibility = if (isVisible) View.GONE else View.GONE
    }

    private fun init() {
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        socialMobileViewModel = ViewModelProvider(this)[SocialMobileViewModel::class.java]
        removePushTokenViewModel = ViewModelProvider(this)[RemovePushTokenViewModel::class.java]
        activeMatchingDealViewModel =
            ViewModelProvider(this)[ActiveMatchingDealViewModel::class.java]
        findUCDDealGuestViewModel =
            ViewModelProvider(this)[FindUCDDealViewModel::class.java]
        userData = pref?.getUserData()!!
        main = this
        setSupportActionBar(toolbar)
        ivMenu.setOnClickListener(this)
        ivCloseDrawer.setOnClickListener(this)
        ivLogOut.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        setDrawerData()
        setNavDrawerData()
        if (intent.hasExtra(Constant.ARG_IS_LYK_SHOW)) {
            showWarningDialog()
        }
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        AppGlobal.getTimeZoneOffset()
        setPowerSaving()
        callUserProfileAPI()
    }

    private fun showNotification(intent: Intent) {
        if (intent.hasExtra(ARG_IS_NOTIFICATION)) {
            when {
                intent.hasExtra(ARG_TRANSACTION_CODE) -> {
                    startActivity<TransactionCodeDetailActivity>(
                        ARG_TRANSACTION_CODE to intent.getStringExtra(
                            ARG_TRANSACTION_CODE
                        )
                    )
                }
                intent.hasExtra(ARG_UCD_DATA) -> {
                    val data = Gson().fromJson(
                        intent.getStringExtra(ARG_UCD_DATA),
                        NotificationUcdDealData::class.java
                    )
                    //                callSearchFindDealAPI(data)
                    setUCDData(data)
                }
                intent.hasExtra(ARG_DEAL_ID) -> {
                    val dealId = intent.getStringExtra(ARG_DEAL_ID)
                    activeMatchingDealAPI(dealId!!)
                }
            }
            MyFirebaseMessageService.clearNotifications(this)
        }
    }

    private fun setUCDData(dataUCDNoti: NotificationUcdDealData) {
        val dataYear = YearModelMakeData()
        dataYear.vehicleYearID = dataUCDNoti.UCDCriteria?.VehicleYearID!!
        dataYear.vehicleMakeID = dataUCDNoti.UCDCriteria.VehicleMakeID!!
        dataYear.vehicleModelID = dataUCDNoti.UCDCriteria.VehicleModelID!!
        dataYear.vehicleTrimID = dataUCDNoti.UCDCriteria.VehicleTrimID!!
        dataYear.vehicleExtColorID = dataUCDNoti.UCDCriteria.VehicleExteriorColorID!!
        dataYear.vehicleIntColorID = dataUCDNoti.UCDCriteria.VehicleInteriorColorID!!
        dataYear.vehicleYearStr = dataUCDNoti.VehicleYear
        dataYear.vehicleMakeStr = dataUCDNoti.VehicleMake
        dataYear.vehicleModelStr = dataUCDNoti.VehicleModel
        dataYear.vehicleTrimStr = dataUCDNoti.VehicleTrim
        dataYear.vehicleExtColorStr = "ANY"
        dataYear.vehicleIntColorStr = "ANY"
        dataYear.radius = dataUCDNoti.UCDCriteria.SearchRadius!!.replace("miles", "").trim()
        dataYear.zipCode = dataUCDNoti.UCDCriteria.ZipCode!!

        val dataUcd = PrefSearchDealData()
        dataUcd.zipCode = dataUCDNoti.UCDCriteria.ZipCode!!
        dataUcd.isZipCode = true
        dataUcd.yearId = dataUCDNoti.UCDCriteria.VehicleYearID!!
        dataUcd.makeId = dataUCDNoti.UCDCriteria.VehicleMakeID!!
        dataUcd.modelId = dataUCDNoti.UCDCriteria.VehicleModelID!!
        dataUcd.trimId = dataUCDNoti.UCDCriteria.VehicleTrimID!!
        dataUcd.extColorId = dataUCDNoti.UCDCriteria.VehicleExteriorColorID!!
        dataUcd.intColorId = dataUCDNoti.UCDCriteria.VehicleInteriorColorID!!
        dataUcd.yearStr = dataUCDNoti.VehicleYear
        dataUcd.makeStr = dataUCDNoti.VehicleMake
        dataUcd.modelStr = dataUCDNoti.VehicleModel
        dataUcd.trimStr = dataUCDNoti.VehicleTrim
        dataUcd.extColorStr = "ANY"
        dataUcd.intColorStr = "ANY"
        dataUcd.searchRadius =
            dataUCDNoti.Distance!!.replace("miles", "mi").trim()
        pref?.setSearchDealData(Gson().toJson(dataUcd))
        setCurrentTime()

        startActivity<UCDDealListStep1NewActivity>(
            Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
            Constant.ARG_RADIUS to dataUCDNoti.Distance!!.replace("miles", "mi")
                .trim(),
            Constant.ARG_ZIPCODE to dataUCDNoti.UCDCriteria.ZipCode!!,
            Constant.ARG_IS_NOTIFICATION to true
        )

    }

    private fun callSearchFindDealAPI(dataUCDNoti: NotificationUcdDealData) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = dataUCDNoti.UCDCriteria?.VehicleYearID!!
            request[ApiConstant.vehicleMakeID] = dataUCDNoti.UCDCriteria.VehicleMakeID!!
            request[ApiConstant.vehicleModelID] = dataUCDNoti.UCDCriteria.VehicleModelID!!
            request[ApiConstant.vehicleTrimID] = dataUCDNoti.UCDCriteria.VehicleTrimID!!
            request[ApiConstant.vehicleExteriorColorID] =
                dataUCDNoti.UCDCriteria.VehicleExteriorColorID!!
            request[ApiConstant.vehicleInteriorColorID] =
                dataUCDNoti.UCDCriteria.VehicleInteriorColorID!!
            request[ApiConstant.zipCode] = dataUCDNoti.UCDCriteria.ZipCode!!
            request[ApiConstant.searchRadius] =
                dataUCDNoti.UCDCriteria.SearchRadius!!.replace("miles", "").trim()
            Log.e("Request Find Deal", Gson().toJson(request))
            findUCDDealGuestViewModel.findDeal(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Response", Gson().toJson(data))
                    val dataYear = YearModelMakeData()
                    dataYear.vehicleYearID = dataUCDNoti.UCDCriteria.VehicleYearID!!
                    dataYear.vehicleMakeID = dataUCDNoti.UCDCriteria.VehicleMakeID!!
                    dataYear.vehicleModelID = dataUCDNoti.UCDCriteria.VehicleModelID!!
                    dataYear.vehicleTrimID = dataUCDNoti.UCDCriteria.VehicleTrimID!!
                    dataYear.vehicleExtColorID = dataUCDNoti.UCDCriteria.VehicleExteriorColorID!!
                    dataYear.vehicleIntColorID = dataUCDNoti.UCDCriteria.VehicleInteriorColorID!!
                    dataYear.vehicleYearStr = dataUCDNoti.VehicleYear
                    dataYear.vehicleMakeStr = dataUCDNoti.VehicleMake
                    dataYear.vehicleModelStr = dataUCDNoti.VehicleModel
                    dataYear.vehicleTrimStr = dataUCDNoti.VehicleTrim
                    dataYear.vehicleExtColorStr = "ANY"
                    dataYear.vehicleIntColorStr = "ANY"
                    dataYear.radius =
                        dataUCDNoti.UCDCriteria.SearchRadius!!.replace("miles", "mi").trim()
                    dataYear.zipCode = dataUCDNoti.UCDCriteria.ZipCode!!

                    val dataUcd = PrefSearchDealData()
                    dataUcd.zipCode = dataUCDNoti.UCDCriteria.ZipCode!!
                    dataUcd.isZipCode = true
                    dataUcd.yearId = dataUCDNoti.UCDCriteria.VehicleYearID!!
                    dataUcd.makeId = dataUCDNoti.UCDCriteria.VehicleMakeID!!
                    dataUcd.modelId = dataUCDNoti.UCDCriteria.VehicleModelID!!
                    dataUcd.trimId = dataUCDNoti.UCDCriteria.VehicleTrimID!!
                    dataUcd.extColorId = dataUCDNoti.UCDCriteria.VehicleExteriorColorID!!
                    dataUcd.intColorId = dataUCDNoti.UCDCriteria.VehicleInteriorColorID!!
                    dataUcd.yearStr = dataUCDNoti.VehicleYear
                    dataUcd.makeStr = dataUCDNoti.VehicleMake
                    dataUcd.modelStr = dataUCDNoti.VehicleModel
                    dataUcd.trimStr = dataUCDNoti.VehicleTrim
                    dataUcd.extColorStr = "ANY"
                    dataUcd.intColorStr = "ANY"
                    dataUcd.searchRadius =
                        dataUCDNoti.UCDCriteria.SearchRadius!!.replace("miles", "mi").trim()
                    pref?.setSearchDealData(Gson().toJson(dataUcd))
                    setCurrentTime()

                    startActivity<UCDDealListStep1Activity>(
                        Constant.ARG_UCD_DEAL to Gson().toJson(
                            data
                        ),
                        Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
                        Constant.ARG_RADIUS to dataUCDNoti.UCDCriteria.SearchRadius!!.replace(
                            "miles",
                            "mi"
                        ).trim(),
                        Constant.ARG_ZIPCODE to dataUCDNoti.UCDCriteria.ZipCode!!,
                        Constant.ARG_IS_NOTIFICATION to true
                    )
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun activeMatchingDealAPI(dealID: String) {
        if (Constant.isOnline(this)) {

            activeMatchingDealViewModel.matchingDeal(this, dealID)!!
                .observe(
                    this
                ) { data ->
                    Log.e("Response", Gson().toJson(data))
                    val pkgList: ArrayList<VehiclePackagesData> = ArrayList()
                    if (data.vehicleInStockCheckInput?.accessoryList.isNullOrEmpty()) {
                        val pkgData = VehiclePackagesData()
                        pkgData.packageName = "ANY"
                        pkgData.isSelect = true
                        pkgData.vehiclePackageID = "0"
                        pkgList.add(pkgData)
                    } else {
                        if (!data.vehicleCriteria?.packages.isNullOrEmpty()) {
                            for (i in 0 until data.vehicleCriteria?.packages?.size!!) {
                                data.vehicleCriteria.packages[i].isSelect = true
                                pkgList.add(data.vehicleCriteria.packages[i])
                            }
                        }
                    }

                    val accList: ArrayList<VehicleAccessoriesData> = ArrayList()
                    if (data.vehicleInStockCheckInput?.accessoryList.isNullOrEmpty()) {
                        val accData = VehicleAccessoriesData()
                        accData.accessory = "ANY"
                        accData.isSelect = true
                        accData.dealerAccessoryID = "0"
                        accList.add(accData)
                    } else {
                        if (!data.vehicleCriteria?.packages.isNullOrEmpty()) {
                            for (i in 0 until data.vehicleCriteria?.dealerAccessories?.size!!) {
                                data.vehicleCriteria.dealerAccessories[i].isSelect = true
                                accList.add(data.vehicleCriteria.dealerAccessories[i])
                            }
                        }
                    }

                    val dataYear = YearModelMakeData()
                    dataYear.vehicleYearID = data.deal?.vehicleYearID!!
                    dataYear.vehicleMakeID = data.deal.vehicleMakeID!!
                    dataYear.vehicleModelID = data.deal.vehicleModelID!!
                    dataYear.vehicleTrimID = data.deal.vehicleTrimID!!
                    dataYear.vehicleExtColorID = data.vehicleInStockCheckInput?.exteriorColorId!!
                    dataYear.vehicleIntColorID = data.vehicleInStockCheckInput.interiorColorId!!
                    dataYear.vehicleYearStr = data.vehicleCriteria?.year
                    dataYear.vehicleMakeStr = data.vehicleCriteria?.make
                    dataYear.vehicleModelStr = data.vehicleCriteria?.model
                    dataYear.vehicleTrimStr = data.vehicleCriteria?.trim
                    dataYear.vehicleExtColorStr =
                        if (TextUtils.isEmpty(data.vehicleCriteria?.exteriorColor)) "ANY" else data.vehicleCriteria?.exteriorColor
                    dataYear.vehicleIntColorStr =
                        if (TextUtils.isEmpty(data.vehicleCriteria?.interiorColor)) "ANY" else data.vehicleCriteria?.interiorColor
                    dataYear.radius =
                        data.vehicleInStockCheckInput.searchRadius!!
                    dataYear.zipCode = data.vehicleInStockCheckInput.zipcode!!
                    dataYear.loanType = data.deal.loanType!!
                    dataYear.price = data.deal.price!!
                    dataYear.arOptions = accList
                    dataYear.arPackages = pkgList

                    val dataLYK = PrefSubmitPriceData()
                    dataLYK.yearId = data.deal.vehicleYearID
                    dataLYK.makeId = data.deal.vehicleMakeID
                    dataLYK.modelId = data.deal.vehicleModelID
                    dataLYK.trimId = data.deal.vehicleTrimID
                    dataLYK.extColorId = data.vehicleInStockCheckInput.exteriorColorId
                    dataLYK.intColorId = data.vehicleInStockCheckInput.interiorColorId
                    dataLYK.yearStr = data.vehicleCriteria?.year
                    dataLYK.makeStr = data.vehicleCriteria?.make
                    dataLYK.modelStr = data.vehicleCriteria?.model
                    dataLYK.trimStr = data.vehicleCriteria?.trim
                    dataLYK.extColorStr =
                        if (TextUtils.isEmpty(data.vehicleCriteria?.exteriorColor)) "ANY" else data.vehicleCriteria?.exteriorColor
                    dataLYK.intColorStr =
                        if (TextUtils.isEmpty(data.vehicleCriteria?.interiorColor)) "ANY" else data.vehicleCriteria?.interiorColor
                    dataLYK.packagesData = pkgList
                    dataLYK.optionsData = accList
                    AppGlobal.pref?.setSubmitPriceData(Gson().toJson(dataLYK))
                    setCurrentTimeLYK()
                    loadFragment(LYKFragment(), getString(R.string.submit_your_price))
                    startActivity<LYKStep1Activity>(
                        Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
                        Constant.ARG_IS_BID to true
                    )
                }

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCurrentTimeLYK() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSubmitPriceTime(date)
    }

    private fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSearchDealTime(date)
    }

    private fun setSocialPhoneNo() {
        if (pref?.getUserData()?.isSocial!!) {
            if (!pref?.isUpdateSocialMobile()!!) {
                dialogPhoneNo(true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onResume() {
        super.onResume()
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        if (pref?.isBid()!!) {
            pref?.setBid(false)
            if (Constant.isInitProgress() && Constant.progress.isShowing)
                Constant.dismissLoader()
            loadFragment(LYKFragment(), getString(R.string.submit_your_price))
            val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom1)
            item.isChecked = true
        }
    }

    private fun setDrawerData() {
//        arDrawer.add(DrawerData(1, R.drawable.ic_account, R.drawable.ic_account_white, "Account"))
        arDrawer.add(
            DrawerData(
                2,
                R.drawable.ic_bid_history,
                R.drawable.ic_bid_history_white,
                resources.getString(R.string.price_bid_history)
            )
        )
        arDrawer.add(
            DrawerData(
                3,
                R.drawable.ic_transaction_history_white,
                R.drawable.ic_transaction_history,
                resources.getString(R.string.transaction_history)
            )
        )
        /* arDrawer.add(
             DrawerData(
                 4,
                 R.drawable.ic_fav,
                 R.drawable.ic_fav_white,
                 "Favourite Searches"
             )
         )*/
        arDrawer.add(
            DrawerData(
                5,
                R.drawable.ic_how_work,
                R.drawable.ic_how_work_white,
                resources.getString(R.string.how_it_works)
            )
        )
        arDrawer.add(
            DrawerData(
                6,
                R.drawable.ic_contact,
                R.drawable.ic_contact_white,
                resources.getString(R.string.help_livechat)
            )
        )
        arDrawer.add(
            DrawerData(
                7,
                R.drawable.ic_terms_conditions,
                R.drawable.ic_terms_conditions_white,
                resources.getString(R.string.terms_conditions)
            )
        )
        arDrawer.add(
            DrawerData(
                8,
                R.drawable.ic_privacy_policy,
                R.drawable.ic_privacy_policy_white,
                resources.getString(R.string.privacy_policy)
            )
        )
//        arDrawer.add(DrawerData(8, R.drawable.ic_logout, R.drawable.ic_logout, "Logout"))
    }

    private lateinit var userData: LoginData

    private fun setNavDrawerData() {
        llRefer.setOnClickListener(this)
        rvNavigation.layoutManager = LinearLayoutManager(this)
        rvNavigation.setHasFixedSize(true)
        adapterDrawer = DrawerListAdapter(R.layout.list_item_drawer, this)
        rvNavigation.adapter = adapterDrawer
        adapterDrawer.addAll(arDrawer)

        tvUserName.text = userData.firstName + " " + userData.lastName
        tvUserEmail.text = userData.userName
//        loadFragment(HomeFragment(), getString(R.string.search_deals_title))
        if (intent.hasExtra(ARG_SEL_TAB)) {
            when (intent.getIntExtra(ARG_SEL_TAB, 0)) {
                TYPE_SUBMIT_PRICE -> {
                    if (Constant.isInitProgress() && Constant.progress.isShowing)
                        Constant.dismissLoader()
                    loadFragment(LYKFragment(), getString(R.string.submit_your_price))
                    val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom1)
                    item.isChecked = true

                }
                TYPE_ONE_DEAL_NEAR_YOU -> {
                    if (Constant.isInitProgress() && Constant.progress.isShowing)
                        Constant.dismissLoader()
                    loadFragment(OneDealNearYouFragment(), getString(R.string.one_deal_near_you))
                    val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom2)
                    item.isChecked = true
                    if (intent.hasExtra(ARG_IS_LCD)) {
                        showWarningDialog()
                    }
                }
                TYPE_SEARCH_DEAL -> {
                    if (Constant.isInitProgress() && Constant.progress.isShowing)
                        Constant.dismissLoader()
                    loadFragment(UCDFragment(), getString(R.string.search_deals))
                    val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom3)
                    item.isChecked = true
                }
            }
        } else {
            loadFragment(LYKFragment(), getString(R.string.submit_your_price))
        }

    }

    private fun showWarningDialog() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_car_not_available)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
        setTitle(title)
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT)
            return
        }
        val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom1)
        if (item.isChecked) {
            val fragment = supportFragmentManager.findFragmentById(R.id.flContainer)
            /* if (fragment is DealSummeryActivity || fragment is DealSummeryStep2Fragment) {
                 loadFragment(HomeFragment(), getString(R.string.search_deals_title))
             } else {*/
            super.onBackPressed()
//            }
        } else {
            if (selectDrawerPos != -1) {
                val data = adapterDrawer.getItem(selectDrawerPos)
                data.isSelect = false
                adapterDrawer.update(selectDrawerPos, data)
            }
            selectDrawerPos = -1
            loadFragment(LYKFragment(), getString(R.string.submit_your_price))
            item.isChecked = true
        }
    }

    var selectDrawerPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llRefer -> {
                drawer.closeDrawer(Gravity.RIGHT)
                startActivity<ReferReferralActivity>()
            }
            R.id.llDrawer -> {
                val pos = v.tag as Int
                if (selectDrawerPos != -1) {
                    val data = adapterDrawer.getItem(selectDrawerPos)
                    data.isSelect = false
                    adapterDrawer.update(selectDrawerPos, data)
                }
                val data = adapterDrawer.getItem(pos)
                data.isSelect = true
                adapterDrawer.update(pos, data)
                selectDrawerPos = pos
                when (pos) {

                    0 -> {
                        startActivity<BidHistoryActivity>()
//                        startActivity<GooglePaymentActivity>()
                    }
                    1 -> {
                        startActivity<TransactionHistoryActivity>()
                    }
                    2 -> {
                        startActivity<HowItWorkHelpWebViewActivity>(
                            ARG_TITLE to data.title,
                            ARG_WEB_URL to Constant.HOW_IT_WORKS
                        )
                    }
                    3 -> {
                        startActivity<HowItWorkHelpWebViewActivity>(
                            ARG_TITLE to data.title,
                            ARG_WEB_URL to Constant.HELP
                        )
                    }

                    4 -> {
                        AppGlobal.dialogWebView(this, Constant.TERMS_CONDITIONS_LINK)
                    }
                    5 -> {
                        AppGlobal.dialogWebView(this, Constant.PRIVACY_POLICY_LINK)
                    }

                }
                Handler().postDelayed({
                    drawer.closeDrawer(Gravity.RIGHT)
                }, 200)

            }
            R.id.ivMenu -> {
                drawer.openDrawer(Gravity.RIGHT)
                /* if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                 drawer.closeDrawer(Gravity.RIGHT)
             }*/
            }
            R.id.ivCloseDrawer -> {
                drawer.closeDrawer(Gravity.RIGHT)
            }
            R.id.ivEdit -> {
                loadFragment(UCDFragment(), getString(R.string.search_deals_title))
            }
            R.id.ivLogOut -> {
                popupLogout()
            }
            R.id.llLogout -> {
                drawer.closeDrawer(Gravity.RIGHT)
                popupLogout()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemBottom1 -> {
                loadFragment(LYKFragment(), getString(R.string.submit_your_price))
            }
            R.id.itemBottom2 -> {
                loadFragment(OneDealNearYouFragment(), getString(R.string.one_deal_near_you))
            }
            R.id.itemBottom3 -> {
                loadFragment(UCDFragment(), getString(R.string.search_deals_title))
            }
            R.id.itemBottom4 -> {
                loadFragment(AccountFragment(), getString(R.string.account))
            }
        }
        return true
    }

    private fun popupLogout() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_logout)

        dialog.run {
            tvLogOut.setOnClickListener {
                callRemovePushTokenAPI(dialog)
            }
            tvCancel.setOnClickListener {
                dialog.dismiss()
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

    private lateinit var dialogMobileNo: Dialog
    private fun dialogPhoneNo(isCancel: Boolean) {
        dialogMobileNo = Dialog(this, R.style.FullScreenDialog)
        dialogMobileNo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMobileNo.setCancelable(true)
        dialogMobileNo.setContentView(R.layout.dialog_mobile_no)
        Constant.onTextChange(this, dialogMobileNo.edtPhoneNumber, dialogMobileNo.tvErrorPhoneNo)
        dialogMobileNo.edtPhoneNumber.filters =
            arrayOf<InputFilter>(filterSocMob, InputFilter.LengthFilter(13))
        if (isCancel) {
            dialogMobileNo.ivSocClose.visibility = View.VISIBLE
        }
        dialogMobileNo.run {
            btnDialogSave.setOnClickListener {
                if (TextUtils.isEmpty(edtPhoneNumber.text.toString().trim())) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                } else if (edtPhoneNumber.text.toString().trim().length != 13) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
                } else {
                    tvErrorPhoneNo.visibility = View.GONE
                    callSocialMobileAPI(edtPhoneNumber.text.toString().trim())
                    dismiss()
                }
            }
            ivSocClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogMobileNo)
        dialogMobileNo.show()
    }

    private fun callSocialMobileAPI(phoneNo: String) {
        val data: LoginData = pref?.getUserData()!!
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.FirstNameSoc] = data.firstName!!
            request[ApiConstant.LastNameSoc] = data.lastName!!
            request[ApiConstant.UserNameSoc] = data.userName!!
            request[ApiConstant.EmailSoc] = data.userName
            request[ApiConstant.PhoneNumberSoc] = phoneNo

            socialMobileViewModel.getSocialMobile(this, request)!!
                .observe(this, Observer { dataSocial ->
                    Constant.dismissLoader()
//                    data.authToken = dataSocial.authToken
//                    data.refreshToken = dataSocial.refreshToken
                    data.message = dataSocial.message
                    if (data.buyerId != 0) {
                        pref?.setLogin(true)
                        data.isSocial = true
                        pref?.setUserData(Gson().toJson(data))
                        pref?.updateSocialMobile(true)
                    } else {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callRemovePushTokenAPI(dialog: Dialog) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

            removePushTokenViewModel.pushToken(this, pref?.getFirebaseToken())!!
                .observe(this, Observer { dataSocial ->
                    Constant.dismissLoader()
                    pref?.setLogOutData()
                    startActivity(intentFor<LoginActivity>().clearTask().newTask())
                    dialog.dismiss()
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private var filterSocMob = InputFilter { source, start, end, dest, dstart, dend ->
        dialogMobileNo.run {
            var source = source
            if (source.length > 0) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (source.toString().length > 1) {
                        val number = source.toString()
                        val digits1 = number.toCharArray()
                        val digits2 = number.split("(?<=.)").toTypedArray()
                        source = digits2[digits2.size - 1]
                    }
                    if (edtPhoneNumber.text.toString().isEmpty()) {
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

    private fun callUserProfileAPI() {
        try {
            if (Constant.isOnline(this)) {
                if (!Constant.isInitProgress()) {
                    Constant.showLoader(this)
                } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.showLoader(this)
                }
                userProfileViewModel.userProfileCall(this)!!
                    .observe(this, Observer { data ->
                        if (TextUtils.isEmpty(data.phoneNumber)) {
                            setSocialPhoneNo()
                        } else {
                            pref?.updateSocialMobile(true)
                        }
                    }
                    )

            } else {
                Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

}