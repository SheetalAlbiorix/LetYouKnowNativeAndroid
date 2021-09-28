package com.letyouknow.view.spinneradapter

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.VehiclePackagesData
import kotlinx.android.synthetic.main.list_item_options.view.*

class OptionsAdapter(layout: Int, val clickListener: View.OnClickListener) :
    com.logispeed.ui.base.BaseAdapter<VehiclePackagesData>(layout),
    com.logispeed.ui.base.BaseAdapter.OnBind<VehiclePackagesData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehiclePackagesData) {
        view.run {
            data.run {
                if (isSelect!!)
                    ivSelectOptions.setImageResource(R.drawable.ic_checked_icon)
                else
                    ivSelectOptions.setImageResource(R.drawable.ic_checkbox_unchecked)
                chkOptions.text = packageName

                llOptions.tag = position
                llOptions.setOnClickListener(clickListener)
            }

        }
    }
}