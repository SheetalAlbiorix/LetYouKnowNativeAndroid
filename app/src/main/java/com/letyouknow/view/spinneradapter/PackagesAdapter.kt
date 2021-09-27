package com.letyouknow.view.spinneradapter

import android.view.View
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
                chkPackages.text = packageName

                llPackages.tag = position
                llPackages.setOnClickListener(clickListener)
            }

        }
    }
}