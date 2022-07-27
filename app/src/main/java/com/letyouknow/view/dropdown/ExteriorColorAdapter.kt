package com.letyouknow.view.dropdown

import android.view.View
import com.letyouknow.model.ExteriorColorData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_exterior_color.view.*

class ExteriorColorAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<ExteriorColorData>(layout), BaseAdapter.OnBind<ExteriorColorData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: ExteriorColorData) {
        view.run {
            tvTitleExterior.text = data.exteriorColor
            tvTitleExterior.tag = position
            tvTitleExterior.setOnClickListener(clickListener)
        }
    }
}