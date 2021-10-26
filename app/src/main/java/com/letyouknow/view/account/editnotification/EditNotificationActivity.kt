package com.letyouknow.view.account.editnotification

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditNotificationBinding
import com.letyouknow.model.NotificationOptionsData
import com.letyouknow.model.NotificationsData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.NotificationOptionsUpdateViewModel
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_NOTIFICATIONS
import kotlinx.android.synthetic.main.activity_edit_notification.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import kotlinx.android.synthetic.main.layout_toolbar_blue.*

class EditNotificationActivity : BaseActivity(), View.OnClickListener {
    private val arNotification: ArrayList<NotificationsData> = ArrayList()
    private lateinit var binding: ActivityEditNotificationBinding
    private lateinit var adapterEditNotification: EditNotification1Adapter
    private lateinit var notificationOptionsUpdateViewModel: NotificationOptionsUpdateViewModel
    private lateinit var data: NotificationOptionsData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notification)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notification)
        init()
    }

    private fun init() {
        notificationOptionsUpdateViewModel =
            ViewModelProvider(this).get(NotificationOptionsUpdateViewModel::class.java)
        if (intent.hasExtra(ARG_NOTIFICATIONS)) {
            data = Gson().fromJson(
                intent.getStringExtra(ARG_NOTIFICATIONS),
                NotificationOptionsData::class.java
            )
            setNotificationData(data)
        }
        ivBack.setOnClickListener(this)
//        backButton()
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


    private fun setNotificationData(data: NotificationOptionsData) {
        arNotification.add(NotificationsData("Email", R.color.orange, data.Email))
        arNotification.add(
            NotificationsData(
                "SMS",
                R.color.color36c050,
                data.SMS
            )
        )
        arNotification.add(
            NotificationsData(
                "Push Notification",
                R.color.colorPrimary,
                data.PushNotification
            )
        )
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
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.ivOnOff -> {
                val pos = v.tag as Int
                val data = adapterEditNotification.getItem(pos)
                data.isSelect = !data.isSelect!!
                adapterEditNotification.update(pos, data)
                callNotificationOptionsAPI()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callNotificationOptionsAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val map: HashMap<String, Any> = HashMap()
            map[ApiConstant.Email_NOTI] = adapterEditNotification.getItem(0).isSelect!!
            map[ApiConstant.SMS] = adapterEditNotification.getItem(1).isSelect!!
            map[ApiConstant.PushNotification] = adapterEditNotification.getItem(2).isSelect!!
            Log.e("request", Gson().toJson(map))
            notificationOptionsUpdateViewModel.notificationOptionUpdateApiCall(this, map)!!
                .observe(this, {
                    Constant.dismissLoader()
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}