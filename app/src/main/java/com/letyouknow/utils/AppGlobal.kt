package com.letyouknow.utils


import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.letyouknow.R
import org.jetbrains.anko.singleLine

class AppGlobal {
    companion object {
        private var progressDialog: Dialog? = null;
        private val TAG: String = AppGlobal::class.java.simpleName

        fun isNetworkAvailable(ctx: Context): Boolean {
            val connectivityManager = ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager!!
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED
            ) {
                val networkInfo = connectivityManager.activeNetworkInfo
                networkInfo != null && networkInfo.isConnected
            } else if (connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager!!
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    ?.state == NetworkInfo.State.CONNECTED
            ) {
                isWifiEnabled(ctx)
                //return true;
            } else {
                false
            }
        }

        private fun isWifiEnabled(context: Context): Boolean {
            val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return wifiNetwork != null && wifiNetwork.isConnected
        }

        fun isInternetAvailable(context: Context): Boolean {
            val info =
                (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo as NetworkInfo
            return if (info == null)
                false
            else {
                if (info.isConnected)
                    true
                else
                    true

            }
        }

        fun showProgressDialog(context: Context?, message: String?) {
            if (context != null && !(context as AppCompatActivity).isFinishing) {
                progressDialog = Dialog(context)
                progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                try {
                    val dividerId: Int = progressDialog?.context?.resources?.getIdentifier(
                        "android:id/titleDivider", null, null
                    )!!
                    val divider: View = progressDialog?.findViewById(dividerId)!!
                    divider.setBackgroundColor(
                        context.getResources().getColor(android.R.color.transparent)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    val mTitle = progressDialog?.findViewById<View>(R.id.title) as TextView
                    mTitle.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    val x = Resources.getSystem().getIdentifier("titleDivider", "id", "android")
                    val titleDivider: View = progressDialog?.findViewById<View>(x)!!
                    titleDivider.setBackgroundColor(
                        context.getResources().getColor(android.R.color.transparent)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                progressDialog?.setContentView(R.layout.custom_progressbar)
                /*TextView tvMessage = (TextView) progressDialog.findViewById(R.id.txtMessage);
            if (!message.equals("")) {
                tvMessage.setText(message);
            }*/progressDialog?.setCancelable(false)
                if (!context.isFinishing) progressDialog?.show()
            } else {
                assert(context != null)
                Log.e(TAG, context.toString() + " Context Null")
            }
        }

        fun hideProgressDialog() {
            try {
                if (progressDialog != null && progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
            } catch (ignored: Throwable) {
            } finally {
                progressDialog = null
            }
        }

        fun setSpinnerTextColor(spinner: Spinner, context: Context) {
            spinner.setSelection(0, true)
            val v = spinner.selectedView as TextView
            v.setTextColor(context.resources.getColor(R.color.white))
            v.textSize = context.resources.getDimension(R.dimen._6sdp)
        }

        fun setSpinnerTextColorPos(pos: Int, spinner: Spinner, context: Context) {
            spinner.setSelection(pos, true)
            val v = spinner.selectedView as TextView
            v.setTextColor(context.resources.getColor(R.color.white))
            v.textSize = context.resources.getDimension(R.dimen._6sdp)
        }

        fun setSpinnerLayoutPos(pos: Int, spinner: Spinner, context: Context) {
            spinner.setSelection(pos, true)
            val v = spinner.selectedView as LinearLayoutCompat
            val llView = v.getChildAt(0) as LinearLayoutCompat
            llView.backgroundTintList = context.resources.getColorStateList(R.color.color556473)
            val tvView = llView.getChildAt(0) as TextView
            tvView.setTextColor(context.resources.getColor(R.color.white))
            tvView.singleLine = true
            tvView.textSize = context.resources.getDimension(R.dimen._6sdp)
        }
    }
}