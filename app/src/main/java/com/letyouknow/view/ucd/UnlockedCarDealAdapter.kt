package com.letyouknow.view.ucd

import android.text.TextUtils
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
                if (msrp == 0.0f) {
                    tvMSRP.visibility = View.GONE
                }
                val msrpcurrency: String =
                    NumberFormat.getCurrencyInstance(Locale.US).format(msrp) + " MSRP"
                tvPrice.text = currency
                tvMSRP.text = msrpcurrency
                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDisclosure.text =
                            resources.getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (TextUtils.isEmpty(tvDisclosure.text.toString().trim())) {
                            tvDisclosure.text = condition
                        } else {
                            tvDisclosure.text =
                                tvDisclosure.text.toString().trim() + ", " + condition
                        }
                    }
                    llDisclosure.visibility = View.VISIBLE
                } else {
                    llDisclosure.visibility = View.GONE
                }
                strikeThrough(tvMSRP)
            }
        }
    }
}