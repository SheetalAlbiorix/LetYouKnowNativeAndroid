package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.VehicleMakeData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_make.view.*

class MakeAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<VehicleMakeData>(layout), BaseAdapter.OnBind<VehicleMakeData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehicleMakeData) {
        view.run {
            tvTitleMake.text = data.make
            tvTitleMake.tag = position
            tvTitleMake.setOnClickListener(clickListener)
        }
    }
}