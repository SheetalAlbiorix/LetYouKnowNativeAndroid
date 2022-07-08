package com.letyouknow.view.transaction_history

import android.text.TextUtils
import android.view.View
import com.letyouknow.R
import com.letyouknow.model.TransactionHistoryData
import com.letyouknow.utils.AppGlobal
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_transaction_history1.view.*
import java.text.NumberFormat
import java.util.*

class TransactionHistoryAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<TransactionHistoryData>(layout), BaseAdapter.OnBind<TransactionHistoryData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: TransactionHistoryData) {
        view.run {


            data.run {
                var type = ""
                when (label) {
                    "LCD" -> type = "LightningCarDeals"
                    "UCD" -> type = "UnlockedCarDeals"
                    "LYK" -> type = "LetYouKnow"
                }
                tvType.text = type
                tvVehicle.text = "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvExterior.text =
                    if (TextUtils.isEmpty(vehicleExteriorColor)) "ANY" else vehicleExteriorColor
                tvInterior.text =
                    if (TextUtils.isEmpty(vehicleInteriorColor)) "ANY" else vehicleInteriorColor
                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }
                tvPackages.text = if (packages.isEmpty()) "ANY" else packages

                var accessories = ""
                for (i in 0 until vehicleAccessories?.size!!) {
                    accessories = if (i == 0) {
                        vehicleAccessories[i].accessory!!
                    } else {
                        accessories + ",\n" + vehicleAccessories[i].accessory!!
                    }
                }
                tvOptions.text = if (accessories.isEmpty()) "ANY" else accessories
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDisclosure.text =
                            context.getString(R.string.miles_approximate_odometer_reading, miles)

                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDisclosure.text = condition
                        } else {
                            tvDisclosure.text =
                                tvDisclosure.text.toString().trim() + ", " + condition
                        }
                    }
                    llDisc.visibility = View.VISIBLE
                } else {
                    llDisc.visibility = View.GONE
                }
                tvZipCode.text = zipCode
                tvSearchRadius.text = if (searchRadius == "6000") "All" else searchRadius
                tvDate.text = timeStampFormatted
                tvPrice.text = NumberFormat.getCurrencyInstance(Locale.US).format(price)
            }
            tvSeeMore.tag = position
            tvSeeMore.setOnClickListener(clickListener)
            llTransaction.tag = position
            llTransaction.setOnClickListener(clickListener)
            tvDealerReceipt.tag = position
            tvDealerReceipt.setOnClickListener(clickListener)

        }
    }
}