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
            if (position % 2 == 2) {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom2)
            } else if (position % 3 == 0) {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom1)
            } else {
                ivLCDLTYUCD.setImageResource(R.drawable.ic_bottom3)
            }
            if (data) {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                tvDetail.setTextColor(context.resources.getColor(R.color.white))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.white))
                tvZipCode.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.white))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.white))
                tvExteriorTitle.setTextColor(context.resources.getColor(R.color.white))
                tvExterior.setTextColor(context.resources.getColor(R.color.white))
                tvInteriorTitle.setTextColor(context.resources.getColor(R.color.white))
                tvInterior.setTextColor(context.resources.getColor(R.color.white))
                tvDealerTitle.setTextColor(context.resources.getColor(R.color.white))
                tvDealer.setTextColor(context.resources.getColor(R.color.white))
                tvOptionalTitle.setTextColor(context.resources.getColor(R.color.white))
                tvOptional.setTextColor(context.resources.getColor(R.color.white))
            } else {
                cardTransaction.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvDetail.setTextColor(context.resources.getColor(R.color.black))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.black))
                tvZipCode.setTextColor(context.resources.getColor(R.color.black))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.black))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.black))
                tvExteriorTitle.setTextColor(context.resources.getColor(R.color.black))
                tvExterior.setTextColor(context.resources.getColor(R.color.black))
                tvInteriorTitle.setTextColor(context.resources.getColor(R.color.black))
                tvInterior.setTextColor(context.resources.getColor(R.color.black))
                tvDealerTitle.setTextColor(context.resources.getColor(R.color.black))
                tvDealer.setTextColor(context.resources.getColor(R.color.black))
                tvOptionalTitle.setTextColor(context.resources.getColor(R.color.black))
                tvOptional.setTextColor(context.resources.getColor(R.color.black))
            }
        }
    }
}