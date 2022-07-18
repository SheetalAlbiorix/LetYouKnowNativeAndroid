package com.letyouknow.view.spinneradapter

import android.text.Html
import android.view.View
import com.letyouknow.R
import com.letyouknow.model.RebateListData
import kotlinx.android.synthetic.main.list_item_rebate_disc.view.*
import java.text.NumberFormat
import java.util.*

class RebateDiscAdapter(layout: Int, val clickListener: View.OnClickListener) :
    com.logispeed.ui.base.BaseAdapter<RebateListData>(layout),
    com.logispeed.ui.base.BaseAdapter.OnBind<RebateListData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: RebateListData) {
        view.run {
            data.run {
                llRebate.isEnabled = !isGray!!
                if (isGray!!) {
                    llRebate.setBackgroundColor(resources.getColor(R.color.textLightGrey))
                    ivSelect.setImageResource(R.drawable.ic_checkbox_unchecked_border_gray)
                } else {
                    llRebate.setBackgroundColor(resources.getColor(R.color.white))
                    if (isOtherSelect!!) {
                        ivSelect.setImageResource(R.drawable.ic_checked_icon_gray)
                        llRebate.isEnabled = false
                    } else {
                        llRebate.isEnabled = true
                        if (isSelect!!)
                            ivSelect.setImageResource(R.drawable.ic_checked_icon)
                        else
                            ivSelect.setImageResource(R.drawable.ic_checkbox_unchecked_border_gray)
                    }
                }
                chkRebate.text =
                    Html.fromHtml(resources.getString(R.string.rebate_name_cross, rebateName))
                tvRebatePrice.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(rebatePrice)!!
                llRebate.tag = position
                llRebate.setOnClickListener(clickListener)
            }

        }
    }
}