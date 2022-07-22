package com.letyouknow.view.ucd

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.letyouknow.R
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockPriceBidViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.pref
import com.letyouknow.utils.AppGlobal.Companion.setLayoutParam
import com.letyouknow.utils.Constant
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.lyk.summary.LYKStep1Activity
import com.letyouknow.view.ucd.unlockeddealdetail.UCDDealSummaryStep2Activity
import kotlinx.android.synthetic.main.dialog_option_accessories_unlocked.*
import kotlinx.android.synthetic.main.list_item_unlocked_car.view.*
import kotlinx.android.synthetic.main.progress_loading.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class UCDStep1InnerAdapter(
    private var itemsCells: ArrayList<FindUcdDealData?>,
    var clickListener: View.OnClickListener,
    var isShowPriceBid: Boolean?,
    var imageId: String?,
    var radius: String?,
    var zipcode: String?,
    var lifecycleOwner: LifecycleOwner?,
    var activity: Activity?,
    var viewModelStoreOwner: ViewModelStoreOwner
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<FindUcdDealData?>) {
        this.itemsCells.addAll(dataViews)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //add loading item
        Handler().post {
            itemsCells.add(null)
            notifyItemInserted(itemsCells.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (itemsCells.size != 0) {
            itemsCells.removeAt(itemsCells.size - 1)
            notifyItemRemoved(itemsCells.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_unlocked_car, parent, false)
            ItemViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mcontext).inflate(R.layout.progress_loading, parent, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.progressbar.indeterminateDrawable.colorFilter =
                    BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                view.progressbar.indeterminateDrawable.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.MULTIPLY
                )
            }
            LoadingViewHolder(view)
        }
    }

    fun getItem(pos: Int): FindUcdDealData {
        return itemsCells[pos]!!
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsCells[position] == null) {
            Constant.VIEW_TYPE_LOADING
        } else {
            Constant.VIEW_TYPE_ITEM
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            holder.itemView.tvSelectDeal.tag = position
            holder.itemView.tvSelectDeal.setOnClickListener {
                submitDeal(itemsCells[position]!!)
            }
            holder.itemView.tvPriceBid.tag = position
            holder.itemView.tvPriceBid.setOnClickListener {
                callCheckVehicleStockAPI(itemsCells[position]!!)
            }

            holder.itemView.tvViewOptions.tag = position
            holder.itemView.tvViewOptions.setOnClickListener {
                popupOption(itemsCells[position]!!)
            }

            holder.itemView.tvTitle.text =
                itemsCells[position]!!.vehicleYear + " " + itemsCells[position]!!.vehicleMake + " " + itemsCells[position]!!.vehicleModel + " " + itemsCells[position]!!.vehicleTrim
            holder.itemView.tvExterior.text = itemsCells[position]!!.vehicleExteriorColor
            holder.itemView.tvInterior.text = itemsCells[position]!!.vehicleInteriorColor

            val currency: String =
                NumberFormat.getCurrencyInstance(Locale.US).format(itemsCells[position]!!.price)
            if (itemsCells[position]!!.msrp == 0.0f) {
                holder.itemView.tvMSRP.visibility = View.GONE
            }
            val msrpcurrency: String = NumberFormat.getCurrencyInstance(Locale.US)
                .format(itemsCells[position]!!.msrp) + " MSRP"
            holder.itemView.tvPrice.text = currency
            holder.itemView.tvMSRP.text = msrpcurrency
            if (AppGlobal.isNotEmpty(itemsCells[position]!!.miles) || AppGlobal.isNotEmpty(
                    itemsCells[position]!!.condition
                )
            ) {
                if (AppGlobal.isNotEmpty(itemsCells[position]!!.miles))
                    holder.itemView.tvDisclosure.text =
                        mcontext.getString(
                            R.string.miles_approximate_odometer_reading,
                            itemsCells[position]!!.miles
                        )
                if (AppGlobal.isNotEmpty(itemsCells[position]!!.condition)) {
                    if (TextUtils.isEmpty(itemsCells[position]!!.miles)) {
                        holder.itemView.tvDisclosure.text = itemsCells[position]!!.condition
                    } else {
                        holder.itemView.tvDisclosure.text =
                            holder.itemView.tvDisclosure.text.toString()
                                .trim() + ", " + itemsCells[position]!!.condition
                    }
                }
                holder.itemView.llDisclosure.visibility = View.VISIBLE
            } else {
                holder.itemView.llDisclosure.visibility = View.GONE
                if (isShowPriceBid!!)
                    holder.itemView.tvPriceBid.visibility = View.VISIBLE
            }
            AppGlobal.strikeThrough(holder.itemView.tvMSRP)
        }
    }

    private fun submitDeal(data: FindUcdDealData) {
        val yearModelMakeData = YearModelMakeData()
        yearModelMakeData.vehicleYearID = data.yearId
        yearModelMakeData.vehicleMakeID = data.makeId
        yearModelMakeData.vehicleModelID = data.modelId
        yearModelMakeData.vehicleTrimID = data.trimId
        yearModelMakeData.vehicleExtColorID = data.exteriorColorId
        yearModelMakeData.vehicleIntColorID = data.interiorColorId
        yearModelMakeData.vehicleYearStr = data.vehicleYear
        yearModelMakeData.vehicleMakeStr = data.vehicleMake
        yearModelMakeData.vehicleModelStr = data.vehicleModel
        yearModelMakeData.vehicleTrimStr = data.vehicleTrim
        yearModelMakeData.vehicleExtColorStr = data.vehicleExteriorColor
        yearModelMakeData.vehicleIntColorStr = data.vehicleInteriorColor
        yearModelMakeData.radius = data.searchRadius
        yearModelMakeData.zipCode = data.zipCode
        yearModelMakeData.loanType = data.loanType
        yearModelMakeData.price = data.price
        yearModelMakeData.msrp = data.msrp
        yearModelMakeData.discount = data.discount
        yearModelMakeData.promotionId = data.promotionId
        yearModelMakeData.initials = data.initial
        yearModelMakeData.arPackages = data.vehiclePackages
        yearModelMakeData.arOptions = data.vehicleAccessories
        yearModelMakeData.vehicleExtColorID = data.exteriorColorId
        yearModelMakeData.vehicleExtColorID = data.interiorColorId

        mcontext.startActivity<UCDDealSummaryStep2Activity>(
            Constant.ARG_UCD_DEAL to Gson().toJson(data),
            Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
            Constant.ARG_IMAGE_ID to imageId
        )
    }

    private fun popupOption(data: FindUcdDealData) {
        val dialog = Dialog(mcontext, R.style.FullScreenDialog)
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
                            context.getString(R.string.miles_approximate_odometer_reading, miles)
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

    private lateinit var checkVehicleStockViewModel: CheckVehicleStockPriceBidViewModel
    private fun callCheckVehicleStockAPI(dataFind: FindUcdDealData) {
        checkVehicleStockViewModel =
            ViewModelProvider(viewModelStoreOwner)[CheckVehicleStockPriceBidViewModel::class.java]
        if (Constant.isOnline(mcontext)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(activity!!)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(activity!!)
            }
            val pkgList = JsonArray()
            for (i in 0 until dataFind.vehiclePackages?.size!!) {
                pkgList.add(dataFind.vehiclePackages[i].vehiclePackageID)
            }
            val accList = JsonArray()
            for (i in 0 until dataFind.vehicleAccessories?.size!!) {
                accList.add(dataFind.vehicleAccessories[i].dealerAccessoryID)
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.product1] = 3
            request[ApiConstant.yearID] = dataFind.yearId!!
            request[ApiConstant.makeID] = dataFind.makeId!!
            request[ApiConstant.modelID1] = dataFind.modelId!!
            request[ApiConstant.trimID1] = dataFind.trimId!!
            request[ApiConstant.interiorColorID1] = dataFind.interiorColorId!!
            request[ApiConstant.zipCode] = zipcode!!
            request[ApiConstant.searchRadius] =
                if (radius == "ALL") "6000" else radius!!.replace(
                    "mi",
                    ""
                ).trim()


            Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(mcontext, request)!!
                .observe(lifecycleOwner!!, androidx.lifecycle.Observer { data ->
                    Constant.dismissLoader()
                    Log.e("check Stock", data.toString())
                    if (data) {
                        setLYKData(dataFind)
                    } else {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        mcontext.startActivity(
                            mcontext.intentFor<MainActivity>(Constant.ARG_IS_LYK_SHOW to true)
                                .clearTask()
                                .newTask()
                        )
                    }
                }

                )
        } else {
            Toast.makeText(mcontext, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLYKData(data: FindUcdDealData) {
        val arSelectPackages = ArrayList<VehiclePackagesData>()
        for (i in 0 until data.vehiclePackages!!.size) {
            data.vehiclePackages[i].isSelect = true
            arSelectPackages.add(data.vehiclePackages[i])
        }

        val arSelectAccessories = ArrayList<VehicleAccessoriesData>()
        for (i in 0 until data.vehicleAccessories!!.size) {
            data.vehicleAccessories[i].isSelect = true
            arSelectAccessories.add(data.vehicleAccessories[i])
        }

        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSubmitPriceTime(date)

        val submitData = PrefSubmitPriceData()
        submitData.yearId = data.yearId!!
        submitData.makeId = data.makeId!!
        submitData.modelId = data.modelId!!
        submitData.trimId = data.trimId!!
        submitData.extColorId = data.exteriorColorId!!
        submitData.intColorId = data.interiorColorId!!
        submitData.yearStr = data.vehicleYear!!
        submitData.makeStr = data.vehicleMake!!
        submitData.modelStr = data.vehicleModel!!
        submitData.trimStr = data.vehicleTrim!!
        submitData.extColorStr =
            if (data.exteriorColorId == "0" || TextUtils.isEmpty(data.vehicleExteriorColor!!)) "ANY" else data.vehicleExteriorColor
        submitData.intColorStr =
            if (data.interiorColorId == "0" || TextUtils.isEmpty(data.vehicleInteriorColor!!)) "ANY" else data.vehicleInteriorColor
        submitData.packagesData = arSelectPackages
        submitData.optionsData = arSelectAccessories
        pref?.setSubmitPriceData(Gson().toJson(submitData))

        val yearMakeData = YearModelMakeData()
        yearMakeData.vehicleYearID = data.yearId
        yearMakeData.vehicleMakeID = data.makeId
        yearMakeData.vehicleModelID = data.modelId
        yearMakeData.vehicleTrimID = data.trimId
        yearMakeData.vehicleExtColorID = data.exteriorColorId
        yearMakeData.vehicleIntColorID = data.interiorColorId
        yearMakeData.vehicleYearStr = data.vehicleYear
        yearMakeData.vehicleMakeStr = data.vehicleMake
        yearMakeData.vehicleModelStr = data.vehicleModel
        yearMakeData.vehicleTrimStr = data.vehicleTrim
        yearMakeData.vehicleExtColorStr =
            if (data.exteriorColorId == "0" || TextUtils.isEmpty(data.vehicleExteriorColor!!)) "ANY" else data.vehicleExteriorColor
        yearMakeData.vehicleIntColorStr =
            if (data.interiorColorId == "0" || TextUtils.isEmpty(data.vehicleInteriorColor!!)) "ANY" else data.vehicleInteriorColor
        yearMakeData.arPackages = arSelectPackages
        yearMakeData.arOptions = arSelectAccessories
        yearMakeData.price = data.price!! - 100.0f
        yearMakeData.radius = radius
        yearMakeData.zipCode = zipcode

        Handler().postDelayed({
            mcontext.startActivity<LYKStep1Activity>(
                Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(
                    yearMakeData
                ),
                Constant.ARG_IS_BID to true
            )

        }, 1000)
    }

}