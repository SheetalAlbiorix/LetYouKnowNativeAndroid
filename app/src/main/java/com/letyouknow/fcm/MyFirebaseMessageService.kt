package com.letyouknow.fcm

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.letyouknow.LetYouKnowApp
import com.letyouknow.R
import com.letyouknow.model.MarketConditionsData
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IS_NOTIFICATION
import com.letyouknow.utils.Constant.Companion.HOT_PRICE
import com.letyouknow.utils.Constant.Companion.KEY_NOTIFICATION_TYPE
import com.letyouknow.utils.Constant.Companion.LCD_DEAL
import com.letyouknow.utils.Constant.Companion.LYK_DEAL
import com.letyouknow.utils.Constant.Companion.MARKET_CONDITION
import com.letyouknow.utils.Constant.Companion.PROMO_CODE
import com.letyouknow.utils.Constant.Companion.UCD_DEAL
import com.letyouknow.view.dashboard.MainActivity


class MyFirebaseMessageService : FirebaseMessagingService() {

    val NOTIFICATION_CHANNEL_ID = "10001"
    val default_notification_channel_id = "default"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val pref = LetYouKnowApp.getInstance()?.getAppPreferencesHelper()
        pref?.setFirebaseToken(token)
        Log.e("FB_Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Handler(Looper.getMainLooper()).post { processFcmMessage(remoteMessage) }
    }


    private fun processFcmMessage(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        if (data.containsKey(KEY_NOTIFICATION_TYPE)) {
            var intent = Intent()
            if (data[KEY_NOTIFICATION_TYPE] == PROMO_CODE) {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra(ARG_IS_NOTIFICATION, true)

            } else if (data[KEY_NOTIFICATION_TYPE] == HOT_PRICE) {
                intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra(Constant.ARG_UCD_DATA, data["data"])
                intent.putExtra(Constant.ARG_IS_NOTIFICATION, true)
            } else if (data[KEY_NOTIFICATION_TYPE] == LYK_DEAL || data[KEY_NOTIFICATION_TYPE] == LCD_DEAL || data[KEY_NOTIFICATION_TYPE] == UCD_DEAL) {
                intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra(Constant.ARG_TRANSACTION_CODE, data["TransactionCode"])
                intent.putExtra(Constant.ARG_IS_NOTIFICATION, true)
            } else if (data[KEY_NOTIFICATION_TYPE] == MARKET_CONDITION) {
                val dataMarket = Gson().fromJson(data["data"], MarketConditionsData::class.java)
                intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra(Constant.ARG_DEAL_ID, dataMarket.DealId)
                intent.putExtra(Constant.ARG_IS_NOTIFICATION, true)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            showNotification(
                remoteMessage.notification?.title!!,
                remoteMessage.notification?.body!!,
                intent
            )
        }
    }

    private fun showNotification(title: String?, body: String, intent: Intent) {
        var pendingIntent: PendingIntent;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, default_notification_channel_id)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.logo_redraw);
                mBuilder.setColor(resources.getColor(R.color.white));
            } else {
                mBuilder.setSmallIcon(R.drawable.logo_redraw);
            }
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
            assert(mNotificationManager != null)
            mNotificationManager.notify(101, mBuilder.build())
        } catch (e: Exception) {

        }
    }

    companion object {
        fun clearNotifications(activity: Activity) {
            val manager = activity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(101)
        }
    }
}