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

                llPackages.isEnabled = !isGray!!

                if (isGray!!) {
                    llPackages.setBackgroundColor(resources.getColor(R.color.textLightGrey))
                    ivSelect.setImageResource(R.drawable.ic_checkbox_unchecked_border_gray)
                } else {
                    llPackages.setBackgroundColor(resources.getColor(R.color.white))
                    if (isOtherSelect!!) {
                        ivSelect.setImageResource(R.drawable.ic_checked_icon_gray)
                        llPackages.isEnabled = false
                    } else {
                        llPackages.isEnabled = true
                        if (isSelect!!)
                            ivSelect.setImageResource(R.drawable.ic_checked_icon)
                        else
                            ivSelect.setImageResource(R.drawable.ic_checkbox_unchecked_border_gray)
                    }
                }

                chkPackages.text = packageName
                llPackages.tag = position
                llPackages.setOnClickListener(clickListener)
            }

        }
    }
}