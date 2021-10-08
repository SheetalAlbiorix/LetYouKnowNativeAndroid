package com.letyouknow.view.spinneradapter

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.VehiclePackagesData
import kotlinx.android.synthetic.main.list_item_packages.view.*

class PackagesAdapter(layout: Int, val clickListener: View.OnClickListener) :
    com.logispeed.ui.base.BaseAdapter<VehiclePackagesData>(layout),
    com.logispeed.ui.base.BaseAdapter.OnBind<VehiclePackagesData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehiclePackagesData) {
        view.run {
            data.run {
                if (isSelect!!)
                    ivSelect.setImageResource(R.drawable.ic_checked_icon)
                else
                    ivSelect.setImageResource(R.drawable.ic_checkbox_unchecked)
                llPackages.isEnabled = !isGray!!

                if (isGray!!)
                    llPackages.setBackgroundColor(resources.getColor(R.color.textLightGrey))
                else
                    llPackages.setBackgroundColor(resources.getColor(R.color.white))

                chkPackages.text = packageName

                llPackages.tag = position
                llPackages.setOnClickListener(clickListener)
            }

        }
    }
}