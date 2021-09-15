package com.letyouknow.view.account.editrefer

import android.view.View
import com.letyouknow.model.ReferStepData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_refer_step.view.*

class ReferStepAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<ReferStepData>(layout), BaseAdapter.OnBind<ReferStepData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: ReferStepData) {
        view.run {
            data.run {
                tvStepNo.text = "" + (position + 1)
                tvTitle.text = title
                tvDetail.text = detail
                if (list.size - 1 == position) {
                    ivSteps.visibility = View.GONE
                } else {
                    ivSteps.visibility = View.VISIBLE
                }
            }
        }
    }
}