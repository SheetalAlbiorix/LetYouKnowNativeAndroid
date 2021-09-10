package com.letyouknow.view.signup

import android.view.View
import com.letyouknow.model.CardListData
import com.letyouknow.utils.ResourceUtils
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_card.view.*

class CardListAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<CardListData>(layout), BaseAdapter.OnBind<CardListData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: CardListData) {
        view.run {
            data.run {
                if (isSelect!!)
                    cardMain.strokeWidth = 2
                else
                    cardMain.strokeWidth = 0

                ivCardImg.setImageDrawable(ResourceUtils.getDrawableByName(context, cardImg!!))
                tvCardNumber.text = cardNo
                tvExpDate.text = "Expires $expDate"
                cardMain.tag = position
                cardMain.setOnClickListener(clickListener)
            }

        }
    }
}