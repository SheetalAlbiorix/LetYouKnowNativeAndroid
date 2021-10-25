package com.letyouknow.view.account.editnotification

import android.view.View
import com.letyouknow.R
import com.letyouknow.model.NotificationsData
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_edit_notification1.view.*

class EditNotification1Adapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<NotificationsData>(layout), BaseAdapter.OnBind<NotificationsData> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: NotificationsData) {
        view.run {
            data.run {
                if (isSelect!!)
                    ivOnOff.setImageResource(R.drawable.ic_toggle_on)
                else
                    ivOnOff.setImageResource(R.drawable.ic_toggle_off)
                tvTitle.text = title

                ivOnOff.tag = position
                ivOnOff.setOnClickListener(clickListener)
            }
        }
    }
}