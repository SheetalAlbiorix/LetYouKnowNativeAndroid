package com.letyouknow.view.ucd.ucdstep1

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUcdstep1Binding
import com.letyouknow.model.FindUCDMainData
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.FindUCDDealViewModel
import com.letyouknow.retrofit.viewmodel.ImageIdGroupViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.UCDStep1ViewModel
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.activity_ucdstep1.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*


class UCDStep1Activity : BaseActivity(), View.OnClickListener {

    private var searchRadius = ""
    private var zipCode = ""
    private lateinit var findUCDDealViewModel: FindUCDDealViewModel
    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var ucdStep1ViewModel: UCDStep1ViewModel
    private lateinit var binding: ActivityUcdstep1Binding
    private lateinit var adapterUCD: UCDStep1ListAdapter
    private var arUCD: ArrayList<FindUCDMainData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ucdstep1)
        init()
    }

    private fun init() {
        findUCDDealViewModel = ViewModelProvider(this)[FindUCDDealViewModel::class.java]
        ucdStep1ViewModel = ViewModelProvider(this)[UCDStep1ViewModel::class.java]
        if (intent.hasExtra(Constant.ARG_YEAR_MAKE_MODEL) && intent.hasExtra(
                Constant.ARG_RADIUS
            ) && intent.hasExtra(Constant.ARG_ZIPCODE)
        ) {
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            searchRadius = intent.getStringExtra(Constant.ARG_RADIUS)!!
            zipCode = intent.getStringExtra(Constant.ARG_ZIPCODE)!!
//            ucdStep1ViewModel.initUCD(this)

            callSearchFindDealAPI()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        rvUnlockedCarList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        println("SCROLL_STATE_IDLE")
                        val lManager = rvUnlockedCarList.layoutManager as LinearLayoutManager
                        println(lManager.findFirstVisibleItemPosition())
                        val pos = lManager.findLastVisibleItemPosition()
                        if (TextUtils.isEmpty(adapterUCD.getItem(pos).imgUrl)) {
//                            callImageIdAPI(pos, adapterUCD.getItem(pos).arUCD)
                        }
                    }
                    else -> {}
                }
            }
        })

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
            if (yearModelMakeData.LowPrice != "ANY PRICE") {
                request[ApiConstant.LowPrice] =
                    if (TextUtils.isEmpty(
                            yearModelMakeData.LowPrice!!
                        )
                    ) "1" else yearModelMakeData.LowPrice!!
                request[ApiConstant.HighPrice] =
                    if (TextUtils.isEmpty(yearModelMakeData.HighPrice!!)) "1000000" else yearModelMakeData.HighPrice!!
            }
            Log.e("Request Find Deal", Gson().toJson(request))
            findUCDDealViewModel.findDeal(this, request)!!
                .observe(this, Observer { data ->
                    if (data.isNullOrEmpty()) {
                        Constant.dismissLoader()
                        tvNotFound.visibility = View.VISIBLE
//                        callImageIdAPI()
                    } else {
                        tvNotFound.visibility = View.GONE
                        val arGroup = data.groupBy { data -> data?.vehicleModel }
                        val arGroup1 = arGroup.values
                        val arList: ArrayList<FindUCDMainData?> = ArrayList()
                        for ((i, group) in arGroup1.withIndex()) {
                            arList.add(
                                FindUCDMainData(
                                    i,
                                    "",
                                    "",
                                    group as ArrayList<FindUcdDealData?>
                                )
                            )
                        }

                        adapterUCD = UCDStep1ListAdapter(
                            arList,
                            this,
                            false,
                            yearModelMakeData.zipCode,
                            yearModelMakeData.radius,
                            this,
                            this,
                            this,
                            yearModelMakeData.vehicleExtColorID,
                            yearModelMakeData.vehicleExtColorStr
                        )
                        adapterUCD.notifyDataSetChanged()
                        rvUnlockedCarList.adapter = adapterUCD
                        Constant.dismissLoader()

//                        callImageIdAPI(0, adapterUCD.getItem(0).arUCD)
                        Log.e("GroupData", Gson().toJson(arList))
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun callImageIdAPI(pos: Int, findData: ArrayList<FindUcdDealData?>) {
        var imageId = ""
        val imageIdViewModel: ImageIdGroupViewModel =
            ViewModelProvider(this)[ImageIdGroupViewModel::class.java]
        if (Constant.isOnline(this)) {
            /*  if (!Constant.isInitProgress()) {
                  Constant.showLoader(this)
              } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                  Constant.showLoader(this)
              }*/
            val request = HashMap<String, Any>()
            findData[0]?.run {
                request[ApiConstant.vehicleYearID] = yearId!!
                request[ApiConstant.vehicleMakeID] = makeId!!
                request[ApiConstant.vehicleModelID] = modelId!!
                request[ApiConstant.vehicleTrimID] = trimId!!
            }
            Log.e("ImageId Request", Gson().toJson(request))
            imageIdViewModel.imageIdCall(this, request)!!
                .observe(this, Observer { data ->
//                    Constant.dismissLoader()
                    imageId = data
                    callImageUrlAPI(pos, data, findData)
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(pos: Int, imageId: String, findData: ArrayList<FindUcdDealData?>) {
        var imgUrl = ""
        val imageUrlViewModel: ImageUrlViewModel =
            ViewModelProvider(this)[ImageUrlViewModel::class.java]
        if (Constant.isOnline(this)) {

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = imageId
            if (yearModelMakeData.vehicleExtColorID!! == "0") {
                request[ApiConstant.ImageProduct] = "Splash"
            } else {
                request[ApiConstant.ImageProduct] = "MultiAngle"
            }
            request[ApiConstant.ExteriorColor] = yearModelMakeData.vehicleExtColorStr!!
            imageUrlViewModel.imageUrlCall(this, request)!!
                .observe(this, Observer { data ->
                    if (data.isNotEmpty()) {
                        imgUrl = data[0]
                    }
                    val mainData = FindUCDMainData()
                    mainData.position = pos
                    mainData.imageID = imageId
                    mainData.imgUrl = imgUrl
                    mainData.arUCD = findData
                    adapterUCD.updateItem(pos, mainData)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        /*if (isNotification || isFromLYK) {
            startActivity(
                intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                    .newTask()
            )
        } else {*/
        super.onBackPressed()
//        }
    }
}