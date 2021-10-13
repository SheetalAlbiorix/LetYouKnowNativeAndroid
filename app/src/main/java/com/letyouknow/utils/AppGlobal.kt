package com.letyouknow.utils


import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.letyouknow.R
import com.letyouknow.view.login.LoginActivity
import kotlinx.android.synthetic.main.dialog_authorization.*
import kotlinx.android.synthetic.main.dialog_privacy_policy_terms_conditions.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.singleLine

class AppGlobal {
    companion object {
        private var progressDialog: Dialog? = null;
        private val TAG: String = AppGlobal::class.java.simpleName

        fun isNotEmpty(str: String?): Boolean {
            return str != null && str != ""
        }

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
//            v.textSize = context.resources.getDimension(R.dimen._6sdp)
        }

        fun setSpinnerTextColorPos(pos: Int, spinner: Spinner, context: Context) {
            spinner.setSelection(pos, true)
            val v = spinner.selectedView as TextView
            v.setTextColor(context.resources.getColor(R.color.white))
//            v.textSize = context.resources.getDimension(R.dimen._6sdp)
        }

        fun setSpinnerLayoutPos(pos: Int, spinner: Spinner, context: Context) {
            spinner.setSelection(pos, true)
            val v = spinner.selectedView as LinearLayoutCompat
            val llView = v.getChildAt(0) as LinearLayoutCompat
            llView.backgroundTintList = context.resources.getColorStateList(R.color.color556473)
            val tvView = llView.getChildAt(0) as TextView
            tvView.setTextColor(context.resources.getColor(R.color.white))
            tvView.setTextAppearance(context, R.style.bold)
            tvView.singleLine = true
        }

        fun setWhiteSpinnerLayoutPos(pos: Int, spinner: Spinner, context: Context) {
            spinner.setSelection(pos, true)
            val v = spinner.selectedView as LinearLayoutCompat
            val llView = v.getChildAt(0) as LinearLayoutCompat
            llView.backgroundTintList = context.resources.getColorStateList(R.color.offWhite)
            val tvView = llView.getChildAt(0) as TextView
            tvView.setTextColor(context.resources.getColor(R.color.color495a6b))
//            tvView.typeface = Typeface.createFromFile( "font/roboto_bold.ttf")
            tvView.singleLine = true
        }

        fun dialogWebView(context: Context, url: String) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.attributes?.windowAnimations = (R.style.dialogAnimation)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_privacy_policy_terms_conditions)
            dialog.run {
                ivClose.setOnClickListener {
                    dismiss()
                }

                webView.settings.loadWithOverviewMode = true
                webView.settings.useWideViewPort = true
                webView.settings.pluginState = WebSettings.PluginState.ON
                webView.settings.domStorageEnabled = true
                webView.settings.javaScriptEnabled = true
                webView.webViewClient = (object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view?.loadUrl(url!!)
                        return true
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        Log.e("WebView Wrror", error.toString())
                    }
                })

                webView.loadUrl(url)

            }
            setLayoutParam(dialog)
            dialog.show()
        }

        class WebClient : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }

        private fun setLayoutParam(dialog: Dialog) {
            val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window?.attributes = layoutParams
        }

        fun loadImageUrl(context: Context, imageView: ImageView, url: String) {
            Glide.with(context)
                .load(url) // image url
                .placeholder(R.drawable.ic_image_car) // any placeholder to load at start
                .error(R.drawable.ic_image_car)  // any image in case of error
                .centerCrop()
                .into(imageView)
        }

        fun strikeThrough(tvStrike: TextView) {
            tvStrike.paintFlags = tvStrike.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        fun isAuthorizationFailed(context: Context) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.dialog_authorization)
            dialog.run {
                tvOk.setOnClickListener {
                    context.startActivity(
                        context.intentFor<LoginActivity>().clearTask().newTask()
                    )
                }
            }
            setLayoutParam(dialog)
            dialog.show()
        }

        var arState = arrayListOf(
            "State",
            "AL",
            "AK",
            "AZ",
            "AR",
            "CA",
            "CO",
            "CT",
            "DC",
            "DE",
            "FL",
            "GA",
            "HI",
            "ID",
            "IL",
            "IN",
            "IA",
            "KS",
            "KY",
            "LA",
            "ME",
            "MD",
            "MA",
            "MI",
            "MN",
            "MS",
            "MO",
            "MT",
            "ME",
            "MV",
            "NH",
            "NJ",
            "NM",
            "NY",
            "NC",
            "ND",
            "OH",
            "OK",
            "OR",
            "PA",
            "RI",
            "SC",
            "SD",
            "TN",
            "TX",
            "UT",
            "VT",
            "VA",
            "WA",
            "WV",
            "WI",
            "WY"
        )

    }

}