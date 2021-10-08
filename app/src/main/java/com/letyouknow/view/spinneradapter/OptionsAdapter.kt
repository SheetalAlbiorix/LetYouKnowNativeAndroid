package com.letyouknow.view.spinneradapter

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.VehicleAccessoriesData
import kotlinx.android.synthetic.main.list_item_options.view.*

class OptionsAdapter(layout: Int, val clickListener: View.OnClickListener) :
    com.logispeed.ui.base.BaseAdapter<VehicleAccessoriesData>(layout),
    com.logispeed.ui.base.BaseAdapter.OnBind<VehicleAccessoriesData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehicleAccessoriesData) {
        view.run {
            data.run {
                if (isSelect!!)
                    ivSelectOptions.setImageResource(R.drawable.ic_checked_icon)
                else
                    ivSelectOptions.setImageResource(R.drawable.ic_checkbox_unchecked)

                llOptions.isEnabled = !isGray!!
                if (isGray!!) {
                    llOptions.setBackgroundColor(resources.getColor(R.color.textLightGrey))
                } else {
                    llOptions.setBackgroundColor(resources.getColor(R.color.white))
                }
                chkOptions.text = accessory

                llOptions.tag = position
                llOptions.setOnClickListener(clickListener)
            }

        }
    }
}