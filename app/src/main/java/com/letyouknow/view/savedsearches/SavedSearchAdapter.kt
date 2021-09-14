package com.letyouknow.view.savedsearches

import android.view.View
import com.letyouknow.R
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_saved_search.view.*

class SavedSearchAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<Boolean>(layout), BaseAdapter.OnBind<Boolean> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: Boolean) {
        view.run {
            cardSaved.tag = position
            cardSaved.setOnClickListener(clickListener)
            if (data) {
                cardSaved.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                tvDetail.setTextColor(context.resources.getColor(R.color.white))
            } else {
                cardSaved.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvDetail.setTextColor(context.resources.getColor(R.color.textDarkGrey))
            }
        }
    }
}