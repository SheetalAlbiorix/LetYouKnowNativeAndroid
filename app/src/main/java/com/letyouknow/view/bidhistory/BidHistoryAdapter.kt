package com.letyouknow.view.bidhistory

import android.text.TextUtils
import android.view.View
import com.letyouknow.R
import com.letyouknow.model.BidPriceData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_bid_history1.view.*
import java.text.NumberFormat
import java.util.*

class BidHistoryAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<BidPriceData>(layout), BaseAdapter.OnBind<BidPriceData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: BidPriceData) {
        view.run {
            tvSeeMore.tag = position
            tvSeeMore.setOnClickListener(clickListener)
            data.run {
                tvSuccessUnSuccess.text =
                    if (TextUtils.isEmpty(transactionCode)) resources.getString(R.string.un_successful_match) else resources.getString(
                        R.string.successful_match
                    )
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

                tvZipCode.text = zipCode
                tvSearchRadius.text = if (searchRadius == "6000") "All" else searchRadius
                tvDate.text = timeStampFormatted
                tvPrice.text = NumberFormat.getCurrencyInstance(Locale.US).format(price)
            }

        }
    }


}