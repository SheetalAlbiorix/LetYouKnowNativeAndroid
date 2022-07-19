package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.VehicleYearData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_year.view.*

class MakeAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<VehicleYearData>(layout), BaseAdapter.OnBind<VehicleYearData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehicleYearData) {
        view.run {
            tvTitleYear.text = data.year
            tvTitleYear.tag = position
            tvTitleYear.setOnClickListener(clickListener)
        }
    }
}