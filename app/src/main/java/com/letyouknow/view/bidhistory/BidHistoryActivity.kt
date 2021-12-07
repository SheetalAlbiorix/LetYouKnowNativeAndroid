package com.letyouknow.view.bidhistory

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityBidHistoryBinding
import com.letyouknow.model.BidPriceData
import com.letyouknow.model.PrefSubmitPriceData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.BidHistoryViewModel
import com.letyouknow.retrofit.viewmodel.IsSoldViewModel
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.transaction_history.TransactionCodeDetailActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_TRANSACTION_CODE
import kotlinx.android.synthetic.main.activity_bid_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*

class BidHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterBidHistory: BidHistoryAdapter
    private lateinit var binding: ActivityBidHistoryBinding
    private lateinit var bidHistoryViewModel: BidHistoryViewModel
    private lateinit var isSoldViewModel: IsSoldViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_history)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid_history)
        init()
    }

    private fun init() {
        bidHistoryViewModel = ViewModelProvider(this).get(BidHistoryViewModel::class.java)
        isSoldViewModel =
            ViewModelProvider(this).get(IsSoldViewModel::class.java)
//        backButton()
        adapterBidHistory = BidHistoryAdapter(R.layout.list_item_bid_history1, this)
        rvBidHistory.adapter = adapterBidHistory
        callBidHistoryAPI()
        ivBack.setOnClickListener(this)
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.color3e5365))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSeeMore -> {
                val pos = v.tag as Int
                val data = adapterBidHistory.getItem(pos)
                popupBidHistory(data)
            }
            R.id.llBidDetail -> {
                val pos = v.tag as Int
                val data = adapterBidHistory.getItem(pos)
                if (!TextUtils.isEmpty(data.transactionCode)) {
                    startActivity<TransactionCodeDetailActivity>(ARG_TRANSACTION_CODE to data.transactionCode)
                } else {
                    callIsSoldAPI(data)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callBidHistoryAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            bidHistoryViewModel.bidHistoryApiCall(this)!!.observe(this, Observer { data ->
                Constant.dismissLoader()
                Log.e("Bid Data", Gson().toJson(data))
                adapterBidHistory.addAll(data)
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun popupBidHistory(data: BidPriceData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_bid_history)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            data.run {

                tvDialogSuccessUnSuccess.text =
                    if (TextUtils.isEmpty(transactionCode)) getString(R.string.un_successful_match) else getString(
                        R.string.successful_match
                    )
                tvDialogTitle.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text =
                    if (TextUtils.isEmpty(vehicleExteriorColor)) "ANY" else vehicleExteriorColor
                tvDialogInteriorColor.text =
                    if (TextUtils.isEmpty(vehicleInteriorColor)) "ANY" else vehicleInteriorColor
                var accessories = ""
                for (i in 0 until vehicleAccessories?.size!!) {
                    accessories = if (i == 0) {
                        vehicleAccessories[i].accessory!!
                    } else {
                        accessories + ",\n" + vehicleAccessories[i].accessory!!
                    }
                }

                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }

                tvDialogPackage.text = if (packages.isEmpty()) "ANY" else packages
                tvDialogOptions.text = if (accessories.isEmpty()) "ANY" else accessories
                tvDialogZipCode.text = zipCode
                tvDialogRadius.text = if (searchRadius == "6000") "All" else searchRadius
                tvDialogPrice.text = NumberFormat.getCurrencyInstance(Locale.US).format(price)
                tvDialogSubmittedDate.text = timeStampFormatted
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

    private fun callIsSoldAPI(data: BidPriceData) {
        if (Constant.isOnline(this)) {
            val pkgList = JsonArray()
            for (i in 0 until data.vehiclePackages?.size!!) {
                pkgList.add(data.vehiclePackages!![i].vehiclePackageID!!)
            }
            val accList = JsonArray()
            for (i in 0 until data.vehicleAccessories!!.size) {
                accList.add(data.vehicleAccessories!![i].dealerAccessoryID)
            }
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = 1
            request[ApiConstant.YearId1] = data.vehicleInStockCheckInput?.yearId!!
            request[ApiConstant.MakeId1] = data.vehicleInStockCheckInput.makeId!!
            request[ApiConstant.ModelID] = data.vehicleInStockCheckInput.modelId!!
            request[ApiConstant.TrimID] = data.vehicleInStockCheckInput.trimId!!
            request[ApiConstant.ExteriorColorID] = data.vehicleInStockCheckInput.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = data.vehicleInStockCheckInput.interiorColorId!!
            request[ApiConstant.ZipCode1] = data.vehicleInStockCheckInput.zipcode!!
            request[ApiConstant.SearchRadius1] =
                if (data.vehicleInStockCheckInput.searchRadius!! == "ALL") "6000" else data.vehicleInStockCheckInput.searchRadius.replace(
                    " mi",
                    ""
                )
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            isSoldViewModel.isSoldCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SUBMIT_PRICE).clearTask()
                                .newTask()
                        )
                    } else {
                        finish()
                    }

                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}