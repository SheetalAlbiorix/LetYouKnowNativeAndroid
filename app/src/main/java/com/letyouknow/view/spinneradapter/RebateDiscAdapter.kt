package com.letyouknow.view.spinneradapter

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.RebateDiscData
import kotlinx.android.synthetic.main.list_item_rebate_disc.view.*

class RebateDiscAdapter(layout: Int, val clickListener: View.OnClickListener) :
    com.logispeed.ui.base.BaseAdapter<RebateDiscData>(layout),
    com.logispeed.ui.base.BaseAdapter.OnBind<RebateDiscData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: RebateDiscData) {
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
                chkRebate.text = rebateName
                llRebate.tag = position
                llRebate.setOnClickListener(clickListener)
            }

        }
    }
}