package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.VehicleTrimData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_trim.view.*

class TrimAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<VehicleTrimData>(layout), BaseAdapter.OnBind<VehicleTrimData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehicleTrimData) {
        view.run {
            tvTitleTrim.text = data.trim
            tvTitleTrim.tag = position
            tvTitleTrim.setOnClickListener(clickListener)
        }
    }
}