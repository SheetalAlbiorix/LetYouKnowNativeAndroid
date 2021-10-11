package com.letyouknow.view.unlockedcardeal

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.strikeThrough
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_unlocked_car.view.*
import java.text.NumberFormat
import java.util.*

class UnlockedCarDealAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<FindUcdDealData>(layout), BaseAdapter.OnBind<FindUcdDealData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: FindUcdDealData) {
        view.run {

            tvSelectDeal.tag = position
            tvSelectDeal.setOnClickListener(clickListener)

            tvViewOptions.tag = position
            tvViewOptions.setOnClickListener(clickListener)

            data.run {
                tvTitle.text = "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvExterior.text = vehicleExteriorColor
                tvInterior.text = vehicleInteriorColor
                val currency: String = NumberFormat.getCurrencyInstance(Locale.US).format(price)
                val msrpcurrency: String = NumberFormat.getCurrencyInstance(Locale.US).format(msrp)
                tvPrice.text = currency
                tvMSRP.text = msrpcurrency
                if (AppGlobal.isNotEmpty(miles)) {
                    tvDisclosure.text =
                        resources.getString(R.string.miles_approximate_odometer_reading, miles)
                    llDisclosure.visibility = View.VISIBLE
                } else {
                    llDisclosure.visibility = View.GONE
                }
                strikeThrough(tvMSRP)

            }
        }
    }
}