package com.letyouknow.view.unlockedcardeal

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.FindUcdDealGuestData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_unlocked_car.view.*

class UnlockedCarDealAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<FindUcdDealGuestData>(layout), BaseAdapter.OnBind<FindUcdDealGuestData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: FindUcdDealGuestData) {
        view.run {
            cardUnlocked.tag = position
            cardUnlocked.setOnClickListener(clickListener)
            data.run {
                if (isSelect!!) {
                    cardUnlocked.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                    tvDetail.setTextColor(context.resources.getColor(R.color.white))
                    tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.white))
                    tvZipCode.setTextColor(context.resources.getColor(R.color.white))
                    tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.white))
                    tvDisclosure.setTextColor(context.resources.getColor(R.color.white))
                    tvPrice.setBackgroundResource(R.drawable.bg_white_border_round_rect7)
                    ivSelect.visibility = View.VISIBLE
                } else {
                    cardUnlocked.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvDetail.setTextColor(context.resources.getColor(R.color.textDarkGrey))
                tvZipCodeTitle.setTextColor(context.resources.getColor(R.color.textGray))
                tvZipCode.setTextColor(context.resources.getColor(R.color.textDarkGrey))
                tvDisclosureTitle.setTextColor(context.resources.getColor(R.color.textGray))
                tvDisclosure.setTextColor(context.resources.getColor(R.color.textDarkGrey))
                tvPrice.setBackgroundResource(R.drawable.bg_button3)
                ivSelect.visibility = View.INVISIBLE
            }
            }
        }
    }
}