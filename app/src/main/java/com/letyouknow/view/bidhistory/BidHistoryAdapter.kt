package com.letyouknow.view.bidhistory

import android.view.View
import androidx.core.content.ContextCompat
import com.letyouknow.R
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_bid_history.view.*

class BidHistoryAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<String>(layout), BaseAdapter.OnBind<String> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: String) {
        view.run {
            if (position % 2 == 0) {
                tvUnSuccessFulMatch.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.colore9e9e9)
                tvUnSuccessFulMatch.setTextColor(resources.getColor(R.color.textGray))
                llLastBid.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.orange)
            } else {
                tvUnSuccessFulMatch.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.color36c050)
                tvUnSuccessFulMatch.setTextColor(resources.getColor(R.color.white))
                llLastBid.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.colorPrimary)
            }
        }
    }
}