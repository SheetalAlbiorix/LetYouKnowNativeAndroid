package com.letyouknow.view.transaction_history

import android.view.View
import com.letyouknow.R
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_transaction_history.view.*

class TransactionHistoryAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<Boolean>(layout), BaseAdapter.OnBind<Boolean> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: Boolean) {
        view.run {
            cardTransaction.tag = position
            cardTransaction.setOnClickListener(clickListener)
            if (data) {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                tvDetail.setTextColor(context.resources.getColor(R.color.white))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.white))
                tvZipCode.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.white))
            } else {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvDetail.setTextColor(context.resources.getColor(R.color.textDarkGrey))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.textGray))
                tvZipCode.setTextColor(context.resources.getColor(R.color.textDarkGrey))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.textGray))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.textDarkGrey))
            }
        }
    }
}