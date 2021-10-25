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
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class EditNotificationActivity : BaseActivity(), View.OnClickListener {
    private val arNotification: ArrayList<NotificationsData> = ArrayList()
    private lateinit var binding: ActivityEditNotificationBinding
    private lateinit var adapterEditNotification: EditNotification1Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notification)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notification)
        init()
    }

    private fun init() {
        setNotificationData()
        backButton()
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }


    private fun setNotificationData() {
        arNotification.add(NotificationsData("Email", R.color.orange, false))
        arNotification.add(
            NotificationsData(
                "SMS",
                R.color.color36c050,
                false
            )
        )
        arNotification.add(NotificationsData("Push Notification", R.color.colorPrimary, false))
        adapterEditNotification =
            EditNotification1Adapter(R.layout.list_item_edit_notification1, this)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}