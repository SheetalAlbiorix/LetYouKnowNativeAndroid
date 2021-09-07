package com.letyouknow.base

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.letyouknow.LetYouKnowApp
import com.letyouknow.R
import com.letyouknow.utils.AppGlobal

public abstract class BaseActivity : AppCompatActivity(), BaseView {

    protected val TAG = this.javaClass.simpleName
    var progressDialog: ProgressDialog? = null
    var receiver: BroadcastReceiver? = null
    private var intentFilter: IntentFilter? = null
    var pref = LetYouKnowApp.getInstance()?.getAppPreferencesHelper()
//  private val mViewModel: UserDeviceViewModel? = null

    companion object {
        var CurrentActivity: BaseActivity? = null

        fun isNetworkConnected(context: Context): Boolean {
            val cm = (context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        //overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog!!.setMessage("loading...")
        progressDialog!!.setCancelable(false)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onNetworkStateChange(isNetworkConnected(this@BaseActivity))
                Log.e("status", isNetworkConnected(this@BaseActivity).toString())
            }
        }
        intentFilter = IntentFilter()
        intentFilter?.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    override fun onResume() {
//        this.registerReceiver(receiver, intentFilter)
        super.onResume()
        CurrentActivity = this
    }

    override fun onPause() {
//        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun hideProgress() {
        AppGlobal.hideProgressDialog()
    }

    override fun showProgress() {
        AppGlobal.showProgressDialog(getViewActivity(), "")
    }

    open fun showAlertBox(message: String?, clickListener: SweetAlertDialog.OnSweetClickListener?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .setConfirmClickListener(clickListener)
            .show()
    }

    override fun showToast(message: String?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .setConfirmClickListener { sweetAlertDialog -> sweetAlertDialog?.dismiss() }.show()
    }

    override fun showError(message: String?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .show()
    }


}