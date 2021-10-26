package com.letyouknow.view.dashboard.drawer

import android.content.res.ColorStateList
import android.view.View
import com.letyouknow.R
import com.letyouknow.model.DrawerData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_drawer.view.*

class DrawerListAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<DrawerData>(layout), BaseAdapter.OnBind<DrawerData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: DrawerData) {
        view.run {
            data.run {
                tvDrawer.text = title
                llDrawer.tag = position
                llDrawer.setOnClickListener(clickListener)

                if (isSelect!!) {
                    tvDrawer.setTextColor(context.resources.getColor(R.color.white))
                    llDrawer.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.orange))
                    ivDrawer.setImageResource(iconSelect!!)
                } else {
                    tvDrawer.setTextColor(context.resources.getColor(R.color.color545d64))
                    llDrawer.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white))
                    ivDrawer.setImageResource(icon!!)
                }

            }
        }
    }
}