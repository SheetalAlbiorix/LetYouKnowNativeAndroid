package com.letyouknow.view.bidhistory

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
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
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IS_BID
import com.letyouknow.utils.Constant.Companion.ARG_TRANSACTION_CODE
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.lyk.summary.LYKStep1Activity
import com.letyouknow.view.transaction_history.TransactionCodeDetailActivity
import kotlinx.android.synthetic.main.activity_bid_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.*
import kotlinx.android.synthetic.main.dialog_bid_history.ivDialogClose
import kotlinx.android.synthetic.main.dialog_dealer_receipt.*
import kotlinx.android.synthetic.main.dialog_my_receipt.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BidHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterBidHistory: BidHistoryAdapter
    private lateinit var binding: ActivityBidHistoryBinding
    private lateinit var bidHistoryViewModel: BidHistoryViewModel
    private lateinit var isSoldViewModel: IsSoldViewModel
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel
    private lateinit var packagesModel: VehiclePackagesViewModel
    private lateinit var packagesOptional: VehicleOptionalViewModel
    private var arSelectAccessories: ArrayList<VehicleAccessoriesData> = ArrayList()
    private var arSelectPackages: ArrayList<VehiclePackagesData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_history)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid_history)
        init()
    }

    private fun init() {
        packagesModel = ViewModelProvider(this).get(VehiclePackagesViewModel::class.java)
        packagesOptional = ViewModelProvider(this).get(VehicleOptionalViewModel::class.java)
        checkVehicleStockViewModel =
            ViewModelProvider(this).get(CheckVehicleStockViewModel::class.java)
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
//                popupBidHistory(data)
                popupMyReceipt(data)
            }
            R.id.llBidDetail -> {
                val pos = v.tag as Int
                val data = adapterBidHistory.getItem(pos)
                if (!TextUtils.isEmpty(data.transactionCode)) {
                    startActivity<TransactionCodeDetailActivity>(ARG_TRANSACTION_CODE to data.transactionCode)
                } else {
                    callVehiclePackagesAPI(data)
                }
            }
            R.id.tvDealerReceipt -> {
                val pos = v.tag as Int
                showDealerReceiptDialog(adapterBidHistory.getItem(pos))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callBidHistoryAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }

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
                if (vehiclePackages.isNullOrEmpty() && isPackageNone!!) {
                    packages = "NONE"
                } else {
                    for (i in 0 until vehiclePackages?.size!!) {
                        packages = if (i == 0) {
                            vehiclePackages[i].packageName!!
                        } else {
                            packages + ",\n" + vehiclePackages[i].packageName!!
                        }
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


    private fun callVehiclePackagesAPI(dataBid: BidPriceData) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            packagesModel.getPackages(
                this,
                getProductType(dataBid.label!!).toString(),
                dataBid.vehicleInStockCheckInput?.yearId,
                dataBid.vehicleInStockCheckInput?.makeId,
                dataBid.vehicleInStockCheckInput?.modelId,
                dataBid.vehicleInStockCheckInput?.trimId,
                dataBid.vehicleInStockCheckInput?.exteriorColorId,
                dataBid.vehicleInStockCheckInput?.interiorColorId, ""
            )!!
                .observe(this, Observer { data ->
                    // Log.e("Packages Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
                            val packagesData = VehiclePackagesData()
                            packagesData.vehiclePackageID = "0"
                            packagesData.packageName = "ANY"
                            data.add(0, packagesData)
                            selectPackageData(dataBid, data)

                        } else {
                            val arData = ArrayList<VehiclePackagesData>()
                            val packagesData = VehiclePackagesData()
                            packagesData.vehiclePackageID = "0"
                            packagesData.packageName = "ANY"
                            arData.add(0, packagesData)
                            selectPackageData(dataBid, arData)
                        }
                    } catch (e: Exception) {
                    }

                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectPackageData(dataBid: BidPriceData, arData: ArrayList<VehiclePackagesData>) {
        for (i in 0 until arData.size) {
            for (j in 0 until dataBid.vehicleInStockCheckInput?.packageList?.size!!) {
                if (arData[i].vehiclePackageID == dataBid.vehicleInStockCheckInput?.packageList[j]) {
                    val packData = arData[i]
                    packData.isSelect = true
                    arSelectPackages.add(packData)
                }
            }
        }
        // Log.e("selectData", Gson().toJson(arSelectPackages))
        callOptionalAccessoriesAPI(dataBid)
    }

    private fun callOptionalAccessoriesAPI(dataBid: BidPriceData) {
        if (Constant.isOnline(this)) {
            val jsonArray = JsonArray()
            for (i in 0 until arSelectPackages.size) {
                jsonArray.add(arSelectPackages[i].vehiclePackageID)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArray
            request[ApiConstant.productId] = getProductType(dataBid.label!!)
            request[ApiConstant.yearId] = dataBid.vehicleInStockCheckInput?.yearId!!
            request[ApiConstant.makeId] = dataBid.vehicleInStockCheckInput.makeId!!
            request[ApiConstant.modelId] = dataBid.vehicleInStockCheckInput.modelId!!
            request[ApiConstant.trimId] = dataBid.vehicleInStockCheckInput.trimId!!
            request[ApiConstant.exteriorColorId] =
                dataBid.vehicleInStockCheckInput.exteriorColorId!!
            request[ApiConstant.interiorColorId] =
                dataBid.vehicleInStockCheckInput.interiorColorId!!
            request[ApiConstant.zipCode] = ""

            packagesOptional.getOptional(
                this,
                request
            )!!
                .observe(this, Observer { data ->
                    //  Log.e("Options Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
                            val accessoriesData = VehicleAccessoriesData()
                            accessoriesData.dealerAccessoryID = "0"
                            accessoriesData.accessory = "ANY"
                            data.add(0, accessoriesData)
                            selectAccessoriesData(dataBid, data)
                        } else {
                            val arData = ArrayList<VehicleAccessoriesData>()
                            val accessoriesData = VehicleAccessoriesData()
                            accessoriesData.dealerAccessoryID = "0"
                            accessoriesData.accessory = "ANY"
                            arData.add(0, accessoriesData)
                            selectAccessoriesData(dataBid, arData)
                        }
                    } catch (e: Exception) {
                    }
                    callCheckVehicleStockAPI(dataBid)
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectAccessoriesData(
        dataBid: BidPriceData,
        arData: ArrayList<VehicleAccessoriesData>
    ) {
        for (i in 0 until arData.size) {
            for (j in 0 until dataBid.vehicleInStockCheckInput?.accessoryList?.size!!) {
                if (arData[i].dealerAccessoryID == dataBid.vehicleInStockCheckInput?.accessoryList[j]) {
                    val accData = arData[i]
                    accData.isSelect = true
                    arSelectAccessories.add(accData)
                }
            }
        }
        // Log.e("selectAccData", Gson().toJson(arSelectAccessories))
    }

    private fun callCheckVehicleStockAPI(data: BidPriceData) {
        if (Constant.isOnline(this)) {
            val pkgList = JsonArray()
            if (!data.vehicleInStockCheckInput?.packageList.isNullOrEmpty()) {
                for (i in 0 until data.vehicleInStockCheckInput?.packageList?.size!!) {
                    pkgList.add(data.vehicleInStockCheckInput.packageList[i])
                }
            }
            val accList = JsonArray()
            if (!data.vehicleInStockCheckInput?.accessoryList.isNullOrEmpty()) {
                for (i in 0 until data.vehicleInStockCheckInput?.accessoryList?.size!!) {
                    accList.add(data.vehicleInStockCheckInput.accessoryList[i])
                }
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = getProductType(data.label!!)
            request[ApiConstant.YearId1] = data.vehicleInStockCheckInput?.yearId!!
            request[ApiConstant.MakeId1] = data.vehicleInStockCheckInput.makeId!!
            request[ApiConstant.ModelID] = data.vehicleInStockCheckInput.modelId!!
            request[ApiConstant.TrimID] = data.vehicleInStockCheckInput.trimId!!
            request[ApiConstant.ExteriorColorID] = data.vehicleInStockCheckInput.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = data.vehicleInStockCheckInput.interiorColorId!!
            request[ApiConstant.ZipCode1] = data.vehicleInStockCheckInput.zipcode!!
            request[ApiConstant.SearchRadius1] = data.vehicleInStockCheckInput.searchRadius!!
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            // Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { dataStock ->
                    Constant.dismissLoader()
                    if (dataStock) {
                        setPrefSubmitPriceData(data)
                    } else {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SUBMIT_PRICE).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setPrefSubmitPriceData(data: BidPriceData) {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSubmitPriceTime(date)

        val submitData = PrefSubmitPriceData()
        submitData.yearId = data.vehicleInStockCheckInput?.yearId!!
        submitData.makeId = data.vehicleInStockCheckInput.makeId!!
        submitData.modelId = data.vehicleInStockCheckInput.modelId!!
        submitData.trimId = data.vehicleInStockCheckInput.trimId!!
        submitData.extColorId = data.vehicleInStockCheckInput.exteriorColorId!!
        submitData.intColorId = data.vehicleInStockCheckInput.interiorColorId!!
        submitData.yearStr = data.vehicleYear!!
        submitData.makeStr = data.vehicleMake!!
        submitData.modelStr = data.vehicleModel!!
        submitData.trimStr = data.vehicleTrim!!
        submitData.extColorStr =
            if (data.vehicleInStockCheckInput.exteriorColorId == "0" || TextUtils.isEmpty(data.vehicleExteriorColor!!)) "ANY" else data.vehicleExteriorColor!!
        submitData.intColorStr =
            if (data.vehicleInStockCheckInput.interiorColorId == "0" || TextUtils.isEmpty(data.vehicleInteriorColor!!)) "ANY" else data.vehicleInteriorColor!!
        submitData.packagesData = arSelectPackages
        submitData.optionsData = arSelectAccessories
        pref?.setSubmitPriceData(Gson().toJson(submitData))


        val yearMakeData = YearModelMakeData()
        yearMakeData.vehicleYearID = data.vehicleInStockCheckInput.yearId
        yearMakeData.vehicleMakeID = data.vehicleInStockCheckInput.makeId
        yearMakeData.vehicleModelID = data.vehicleInStockCheckInput.modelId
        yearMakeData.vehicleTrimID = data.vehicleInStockCheckInput.trimId
        yearMakeData.vehicleExtColorID = data.vehicleInStockCheckInput.exteriorColorId
        yearMakeData.vehicleIntColorID = data.vehicleInStockCheckInput.interiorColorId
        yearMakeData.vehicleYearStr = data.vehicleYear
        yearMakeData.vehicleMakeStr = data.vehicleMake
        yearMakeData.vehicleModelStr = data.vehicleModel
        yearMakeData.vehicleTrimStr = data.vehicleTrim
        yearMakeData.vehicleExtColorStr =
            if (data.vehicleInStockCheckInput.exteriorColorId == "0" || TextUtils.isEmpty(data.vehicleExteriorColor!!)) "ANY" else data.vehicleExteriorColor
        yearMakeData.vehicleIntColorStr =
            if (data.vehicleInStockCheckInput.interiorColorId == "0" || TextUtils.isEmpty(data.vehicleInteriorColor!!)) "ANY" else data.vehicleInteriorColor
        yearMakeData.arPackages = arSelectPackages
        yearMakeData.arOptions = arSelectAccessories
        yearMakeData.price = data.price
        yearMakeData.radius = data.searchRadius
        yearMakeData.zipCode = data.zipCode

        Handler().postDelayed({
            startActivity<LYKStep1Activity>(
                Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(
                    yearMakeData
                ),
                ARG_IS_BID to true
            )
            finish()
        }, 1000)

    }

    private fun getProductType(data: String): Int {
        var productType = 0
        when (data) {
            "LYK" -> {
                productType = 1
            }
            "LCD" -> {
                productType = 2
            }
            "UCD" -> {
                productType = 3
            }
            "DIY" -> {
                productType = 4
            }
            else -> {
                productType = 0
            }
        }

        return productType
    }

    private fun showDealerReceiptDialog(transData: BidPriceData) {
        val dialogReceipt = Dialog(this, R.style.FullScreenDialog)
        dialogReceipt.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogReceipt.setCancelable(false)
        dialogReceipt.setContentView(R.layout.dialog_dealer_receipt)
        dialogReceipt.run {
            tvAdjustLYKPrice.text =
                NumberFormat.getCurrencyInstance(Locale.US).format(transData?.adjustedLYKPrice)
            tvDocFee.text =
                "+${
                    NumberFormat.getCurrencyInstance(Locale.US).format(transData?.documentationFee)
                }"
            tvPrePayment.text =
                "-${
                    NumberFormat.getCurrencyInstance(Locale.US)
                        .format(transData?.prePaymentToDealer)
                }"
            tvRemainingBal.text =
                NumberFormat.getCurrencyInstance(Locale.US).format(transData?.remainingBalance)

            tvEstimatedTax.text =
                "+${NumberFormat.getCurrencyInstance(Locale.US).format(transData?.carSalesTax)}"
            tvEstimatedReg.text =
                "+${
                    NumberFormat.getCurrencyInstance(Locale.US).format(transData?.nonTaxRegFee)
                }"
            tvEstimatedTotal.text = NumberFormat.getCurrencyInstance(Locale.US)
                .format(transData?.estimatedTotalRemainingBalance)
            tvBasedState.text =
                getString(R.string.based_on_selected_state_of, transData?.buyerState)
            ivDialogClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogReceipt)
        dialogReceipt.show()
    }

    private fun popupMyReceipt(data: BidPriceData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_my_receipt)
        dialog.run {
            ivDialogCloseRec.setOnClickListener {
                dismiss()
            }
            data.run {
                tvDialogSuccessUnSuccessRec.text =
                    if (TextUtils.isEmpty(transactionCode)) resources.getString(R.string.un_successful_match) else resources.getString(
                        R.string.successful_match
                    )
                tvDialogTitleRec.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColorRec.text =
                    if (TextUtils.isEmpty(vehicleExteriorColor)) "ANY" else vehicleExteriorColor
                tvDialogInteriorColorRec.text =
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


                tvDialogPackageRec.text = if (packages.isEmpty()) "ANY" else packages
                tvDialogOptionsRec.text = if (accessories.isEmpty()) "ANY" else accessories
                tvDialogZipCodeRec.text = zipCode
                tvDialogRadiusRec.text = if (searchRadius == "6000") "All" else searchRadius
                tvDialogSubmittedDateRec.text = timeStampFormatted
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDialogDisclosureRec.text =
                            context.getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDialogDisclosureRec.text = condition
                        } else {
                            tvDialogDisclosureRec.text =
                                tvDialogDisclosureRec.text.toString().trim() + ", " + condition
                        }

                    }
                    llDiscRec.visibility = View.VISIBLE
                } else {
                    llDiscRec.visibility = View.GONE
                }

                tvPriceRec.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data?.price)
                tvPrePaymentRec.text =
                    "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.reservationPrepayment)
                    }"
                tvRemainingBalRec.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data?.remainingBalance)

                tvEstimatedTaxRec.text =
                    "+${NumberFormat.getCurrencyInstance(Locale.US).format(data?.carSalesTax)}"
                tvEstimatedRegRec.text =
                    "+${
                        NumberFormat.getCurrencyInstance(Locale.US).format(data?.nonTaxRegFee)
                    }"
                tvEstimatedTotalRec.text = NumberFormat.getCurrencyInstance(Locale.US)
                    .format(data?.estimatedTotalRemainingBalance)
                if (data.discount != null && data.discount != 0.0) {
                    tvPromoRec.text = "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.discount)
                    }"
                    llPromoRec.visibility = View.VISIBLE
                } else {
                    llPromoRec.visibility = View.GONE
                }
                if (data.lykDollar != null && data.lykDollar != 0.0) {
                    tvDollarsRec.text = "-${
                        NumberFormat.getCurrencyInstance(Locale.US)
                            .format(data?.lykDollar)
                    }"
                    llDollarRec.visibility = View.VISIBLE
                } else {
                    llDollarRec.visibility = View.GONE
                }
                tvBasedStateRec.text =
                    getString(R.string.based_on_selected_state_of, data?.buyerState)
            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }
}