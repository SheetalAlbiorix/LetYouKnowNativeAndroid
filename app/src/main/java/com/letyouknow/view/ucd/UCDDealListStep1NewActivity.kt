package com.letyouknow.view.ucd

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUcdDealListStep1Binding
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.FindUCDDealViewModel
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IS_NOTIFICATION
import com.letyouknow.utils.Constant.Companion.ARG_RADIUS
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.utils.Constant.Companion.ARG_ZIPCODE
import com.letyouknow.utils.OnLoadMoreListener
import com.letyouknow.utils.RecyclerViewLoadMoreScroll
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.ucd.unlockeddealdetail.UCDDealSummaryStep2Activity
import kotlinx.android.synthetic.main.activity_ucd_deal_list_step1.*
import kotlinx.android.synthetic.main.dialog_option_accessories_unlocked.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*

class UCDDealListStep1NewActivity : BaseActivity(), View.OnClickListener {
    private var arUnlocked: ArrayList<FindUcdDealData?> = ArrayList()
    private var yearModelMakeData = YearModelMakeData()
    private var searchRadius = ""
    private var zipCode = ""
    private var imageId = ""
    private var isNotification = false

    private lateinit var binding: ActivityUcdDealListStep1Binding

    private lateinit var tokenModel: RefreshTokenViewModel
    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel
    private lateinit var findUCDDealGuestViewModel: FindUCDDealViewModel
    private var arImageUrl: ArrayList<String> = ArrayList()

    lateinit var loadMoreItemsCells: ArrayList<FindUcdDealData?>
    lateinit var adapterLinear: Items_LinearRVAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucd_deal_list_step1)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ucd_deal_list_step1)
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this)[ImageIdViewModel::class.java]
        imageUrlViewModel = ViewModelProvider(this)[ImageUrlViewModel::class.java]
        findUCDDealGuestViewModel =
            ViewModelProvider(this)[FindUCDDealViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(
                ARG_RADIUS
            ) && intent.hasExtra(ARG_ZIPCODE)
        ) {
            /* arUnlocked = Gson().fromJson(
                 intent.getStringExtra(ARG_UCD_DEAL),
                 object : TypeToken<ArrayList<FindUcdDealData>?>() {}.type
             )*/
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            searchRadius = intent.getStringExtra(ARG_RADIUS)!!
            zipCode = intent.getStringExtra(ARG_ZIPCODE)!!
            binding.searchRadius = searchRadius.trim()
            binding.zipCode = zipCode.trim()
            rvUnlockedCar.setHasFixedSize(true)

            /*if (arUnlocked.isNullOrEmpty()) {
                tvNotFound.visibility = View.VISIBLE
            }else {
//                adapterUnlockedCarDeal.addAll(arUnlocked)
            }*/
//            callImageIdAPI()

            callSearchFindDealAPI()
//            callRefreshTokenApi()

        }

        if (intent.hasExtra(ARG_IS_NOTIFICATION)) {
            isNotification = true
        }
//        backButton()
        ivEdit.visibility = View.VISIBLE
        ivEdit.setOnClickListener(this)


        ivBack.setOnClickListener(this)
    }

    private fun callSearchFindDealAPI() {
        if (Constant.isOnline(this)) {
            if (Constant.isInitProgress() && !Constant.progress.isShowing)
                Constant.dismissLoader()
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
            request[ApiConstant.zipCode] = yearModelMakeData.zipCode!!
            request[ApiConstant.searchRadius] =
                if (yearModelMakeData.radius == "ALL") "6000" else yearModelMakeData.radius!!.replace(
                    "mi",
                    ""
                ).trim()
            Log.e("Request Find Deal", Gson().toJson(request))
            findUCDDealGuestViewModel.findDeal(this, request)!!
                .observe(this, Observer { data ->
                    if (data.isNullOrEmpty()) {
                        Constant.dismissLoader()
                        tvNotFound.visibility = View.VISIBLE
                        callImageIdAPI()
                    } else {
                        Log.e("data_length", data.size.toString());
                        arUnlocked.addAll(data);
                        adapterLinear = Items_LinearRVAdapter(arUnlocked, this)
                        adapterLinear.notifyDataSetChanged()
                        rvUnlockedCar.adapter = adapterLinear
                        callImageIdAPI()
                        setRVScrollListener()
                        //  loadData(data)
                        // Constant.dismissLoader()
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRVScrollListener() {
        mLayoutManager = LinearLayoutManager(this)
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                //LoadMoreData()
            }
        })
        rvUnlockedCar.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        //Add the Loading View
        adapterLinear.addLoadingView()
        //Create the loadMoreItemsCells Arraylist
        loadMoreItemsCells = ArrayList()
        //Get the number of the current Items of the main Arraylist
        val start = adapterLinear.itemCount
        //Load 16 more items
        val end = start + 10
        //Use Handler if the items are loading too fast.
        //If you remove it, the data will load so fast that you can't even see the LoadingView
        Handler().postDelayed({
            for (i in start..end) {
                //Get data and add them to loadMoreItemsCells ArrayList
                loadMoreItemsCells.add(arUnlocked[i])
            }
            //Remove the Loading View
            adapterLinear.removeLoadingView()
            //We adding the data to our main ArrayList
            adapterLinear.addData(loadMoreItemsCells)
            //Change the boolean isLoading to false
            scrollListener.setLoaded()
            //Update the recyclerView in the main thread
            rvUnlockedCar.post {
                adapterLinear.notifyDataSetChanged()
            }
        }, 3000)

    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    //    private var selectPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSelectDeal -> {
                // if(!Constant.isInitProgress()){
//                Constant.showLoader(this)

                val pos = v.tag as Int
                /*  if (selectPos != -1) {
                      val data = adapterUnlockedCarDeal.getItem(selectPos)
                      data.isSelect = false
                      adapterUnlockedCarDeal.update(selectPos, data)
                  }

                  val data = adapterUnlockedCarDeal.getItem(pos)
                  data.isSelect = true
                  adapterUnlockedCarDeal.update(pos, data)
                  selectPos = pos
  */
                val data = adapterLinear.getItem(pos)
                startActivity<UCDDealSummaryStep2Activity>(
                    ARG_UCD_DEAL to Gson().toJson(data),
                    ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                    ARG_IMAGE_ID to imageId
                )
//                Constant.dismissLoader()
            }
            R.id.tvViewOptions -> {
                val pos = v.tag as Int
                popupOption(adapterLinear.getItem(pos))
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (isNotification) {
            startActivity(
                intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                    .newTask()
            )
        } else {
            super.onBackPressed()
        }
    }

    private fun popupOption(data: FindUcdDealData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_option_accessories_unlocked)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            data.run {
                tvDialogVehicle.text = "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text = vehicleExteriorColor
                tvDialogInterior.text = vehicleInteriorColor
                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }
                if (packages.isEmpty())
                    tvDialogPackages.visibility = View.GONE
                tvDialogPackages.text = packages

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
                tvDialogOptions.text = accessories
                tvPrice.text = "Price: " + NumberFormat.getCurrencyInstance(Locale.US).format(price)

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
                    tvDialogDisclosure.visibility = View.VISIBLE
                    tvTitleDisclosure.visibility = View.VISIBLE
                } else {
                    tvDialogDisclosure.visibility = View.GONE
                    tvTitleDisclosure.visibility = View.GONE
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

    private fun callImageIdAPI() {
        if (Constant.isOnline(this)) {
            /*  if (!Constant.isInitProgress()) {
                  Constant.showLoader(this)
              } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                  Constant.showLoader(this)
              }*/
            val request = HashMap<String, Any>()
            yearModelMakeData.run {
                request[ApiConstant.vehicleYearID] = vehicleYearID!!
                request[ApiConstant.vehicleMakeID] = vehicleMakeID!!
                request[ApiConstant.vehicleModelID] = vehicleModelID!!
                request[ApiConstant.vehicleTrimID] = vehicleTrimID!!
            }
            Log.e("ImageId Request", Gson().toJson(request))
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
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = ImageId!!
            if (yearModelMakeData.vehicleExtColorID!! == "0") {
                request[ApiConstant.ImageProduct] = "Splash"
            } else {
                request[ApiConstant.ImageProduct] = "MultiAngle"
                request[ApiConstant.ExteriorColor] = yearModelMakeData.vehicleExtColorStr!!
            }


            imageUrlViewModel.imageUrlCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data.isNotEmpty()) {
                        arImageUrl = data
                        AppGlobal.loadImageUrl(this, ivCar, arImageUrl[0])
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
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

            tokenModel.refresh(this, request)!!.observe(
                this
            ) { data ->
                Constant.dismissLoader()
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                callImageIdAPI()
            }
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("crash", "onDestroy")
        if (Constant.isInitProgress() && Constant.progress.isShowing) {
            Constant.dismissLoader()
        }
    }

    /* fun loadData(arList:ArrayList<FindUcdDealData>) = CoroutineScope(Dispatchers.Main).launch {
         adapterUnlockedCarDeal.addAll(arList)
         Constant.dismissLoader()
     }*/
}