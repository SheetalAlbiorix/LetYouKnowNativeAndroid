package com.letyouknow.view.account.editnotification

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditNotificationBinding
import com.letyouknow.model.NotificationsData
import kotlinx.android.synthetic.main.activity_edit_notification.*

class EditNotificationActivity : BaseActivity(), View.OnClickListener {
    private val arNotification: ArrayList<NotificationsData> = ArrayList()
    private lateinit var binding: ActivityEditNotificationBinding
    private lateinit var adapterEditNotification: EditNotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notification)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notification)
        init()
    }

    private fun init() {
        setNotificationData()
    }

    private fun setNotificationData() {
        arNotification.add(NotificationsData("Promos and Deals", R.color.orange, false))
        arNotification.add(
            NotificationsData(
                "Deals Reservation and \nConfirmations",
                R.color.color36c050,
                false
            )
        )
        arNotification.add(NotificationsData("Reservation Reminders", R.color.colorPrimary, false))
        adapterEditNotification =
            EditNotificationAdapter(R.layout.list_item_edit_notification, this)
        rvEditNotification.adapter = adapterEditNotification
        adapterEditNotification.addAll(arNotification)

    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivOnOff -> {
                val pos = v.tag as Int
                val data = adapterEditNotification.getItem(pos)
                data.isSelect = !data.isSelect!!
                adapterEditNotification.update(pos, data)
            }
        }
    }
}