package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.VehicleModelData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_model.view.*

class ModelAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<VehicleModelData>(layout), BaseAdapter.OnBind<VehicleModelData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: VehicleModelData) {
        view.run {
            tvTitleModel.text = data.model
            tvTitleModel.tag = position
            tvTitleModel.setOnClickListener(clickListener)
        }
    }
}