package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.InteriorColorData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_interior_color.view.*

class InteriorColorAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<InteriorColorData>(layout), BaseAdapter.OnBind<InteriorColorData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: InteriorColorData) {
        view.run {
            tvTitleInterior.text = data.interiorColor
            tvTitleInterior.tag = position
            tvTitleInterior.setOnClickListener(clickListener)
        }
    }
}