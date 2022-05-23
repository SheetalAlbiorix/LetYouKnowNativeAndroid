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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.letyouknow.LetYouKnowApp
import com.letyouknow.R
import com.letyouknow.model.BidPriceData
import com.letyouknow.model.MarketConditionsData
import com.letyouknow.model.PrefSubmitPriceData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockViewModel
import com.letyouknow.retrofit.viewmodel.VehicleYearViewModel
import com.letyouknow.utils.AppGlobal.Companion.pref
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
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


class MyFirebaseMessageService : FirebaseMessagingService(), ViewModelStoreOwner, LifecycleOwner {

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


    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel
    private fun callCheckVehicleStockAPI(data: BidPriceData) {
        if (Constant.isOnline(this)) {
            val pkgList = JsonArray()
            if (!data.vehicleInStockCheckInput?.packageList.isNullOrEmpty()) {
                for (i in 0 until data.vehicleInStockCheckInput?.packageList?.size!!) {
                    pkgList.add(data.vehicleInStockCheckInput.packageList[i])
                }
            }
            val accList = JsonArray()
            if (!data.vehicleInStockCheckInput?.accessoryList.isNullOrEmpty()) {
                for (i in 0 until data.vehicleInStockCheckInput?.accessoryList?.size!!) {
                    accList.add(data.vehicleInStockCheckInput.accessoryList[i])
                }
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = getProductType(data.label!!)
            request[ApiConstant.YearId1] = data.vehicleInStockCheckInput?.yearId!!
            request[ApiConstant.MakeId1] = data.vehicleInStockCheckInput.makeId!!
            request[ApiConstant.ModelID] = data.vehicleInStockCheckInput.modelId!!
            request[ApiConstant.TrimID] = data.vehicleInStockCheckInput.trimId!!
            request[ApiConstant.ExteriorColorID] = data.vehicleInStockCheckInput.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = data.vehicleInStockCheckInput.interiorColorId!!
            request[ApiConstant.ZipCode1] = data.vehicleInStockCheckInput.zipcode!!
            request[ApiConstant.SearchRadius1] = data.vehicleInStockCheckInput.searchRadius!!
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { dataStock ->
                    Constant.dismissLoader()
                    if (dataStock) {
//                        setPrefSubmitPriceData(data)
                    } else {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SUBMIT_PRICE).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProductType(data: String): Int {
        var productType = 0
        when (data) {
            "LYK" -> {
                productType = 1
            }
            "LCD" -> {
                productType = 2
            }
            "UCD" -> {
                productType = 3
            }
            "DIY" -> {
                productType = 4
            }
            else -> {
                productType = 0
            }
        }
        return productType
    }

    override fun getViewModelStore(): ViewModelStore {
        return ViewModelStore()
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    private lateinit var vehicleYearModel: VehicleYearViewModel

    private fun callVehicleYearAPI() {
        try {
            if (Constant.isOnline(this)) {
                vehicleYearModel.getYear(
                    this,
                    "1",
                    ""
                )!!
                    .observe(this, Observer { data ->
                        try {
                            Log.e("Year Data", Gson().toJson(data))
                        } catch (e: Exception) {
                        }
                    }
                    )
            } else {
                Toast.makeText(applicationContext, Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
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