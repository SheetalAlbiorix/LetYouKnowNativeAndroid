package com.letyouknow.view.transaction_history

import android.text.TextUtils
import android.view.View
import com.letyouknow.model.TransactionHistoryData
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
            /*cardTransaction.tag = position
            cardTransaction.setOnClickListener(clickListener)
            if (position % 2 == 2) {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom2)
            } else if (position % 3 == 0) {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom1)
            } else {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom3)
            }
            if (data) {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                tvDetail.setTextColor(context.resources.getColor(R.color.white))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.white))
                tvZipCode.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.white))
                tvExteriorTitle.setTextColor(context.resources.getColor(R.color.white))
                tvExterior.setTextColor(context.resources.getColor(R.color.white))
                tvInteriorTitle.setTextColor(context.resources.getColor(R.color.white))
                tvInterior.setTextColor(context.resources.getColor(R.color.white))
                tvDealerTitle.setTextColor(context.resources.getColor(R.color.white))
                tvDealer.setTextColor(context.resources.getColor(R.color.white))
                tvOptionalTitle.setTextColor(context.resources.getColor(R.color.white))
                tvOptional.setTextColor(context.resources.getColor(R.color.white))
            } else {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvDetail.setTextColor(context.resources.getColor(R.color.black))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.black))
                tvZipCode.setTextColor(context.resources.getColor(R.color.black))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.black))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.black))
                tvExteriorTitle.setTextColor(context.resources.getColor(R.color.black))
                tvExterior.setTextColor(context.resources.getColor(R.color.black))
                tvInteriorTitle.setTextColor(context.resources.getColor(R.color.black))
                tvInterior.setTextColor(context.resources.getColor(R.color.black))
                tvDealerTitle.setTextColor(context.resources.getColor(R.color.black))
                tvDealer.setTextColor(context.resources.getColor(R.color.black))
                tvOptionalTitle.setTextColor(context.resources.getColor(R.color.black))
                tvOptional.setTextColor(context.resources.getColor(R.color.black))
            }*/

            data.run {
                var type = ""
                when (label) {
                    "LCD" -> type = "LightingCarDeals"
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

                tvZipCode.text = zipCode
                tvSearchRadius.text = if (searchRadius == "6000") "All" else searchRadius
                tvDate.text = timeStampFormatted
                tvPrice.text = NumberFormat.getCurrencyInstance(Locale.US).format(price)
            }
            tvSeeMore.tag = position
            tvSeeMore.setOnClickListener(clickListener)
            llTransaction.tag = position
            llTransaction.setOnClickListener(clickListener)

        }
    }
}