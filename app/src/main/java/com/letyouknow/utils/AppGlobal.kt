package com.letyouknow.utils


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
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
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.letyouknow.LetYouKnowApp
import com.letyouknow.R
import com.letyouknow.model.LoginData
import com.letyouknow.view.login.LoginActivity
import kotlinx.android.synthetic.main.dialog_authorization.*
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.dialog_error_close.*
import kotlinx.android.synthetic.main.dialog_privacy_policy_terms_conditions.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.singleLine
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AppGlobal {
    companion object {
        private var progressDialog: Dialog? = null;
        private val TAG: String = AppGlobal::class.java.simpleName
        private var userData: LoginData? = null;
        var pref = LetYouKnowApp.getInstance()?.getAppPreferencesHelper()

        fun printRequestAuth(key: String, request: String) {
            Log.e("Auth", getAuthToken())
            Log.e(key, Gson().toJson(request))
        }

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

        fun isEmpty(str: String?): Boolean {
            return TextUtils.isEmpty(str) || str == "null" || str == ""
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

        fun stringToDate(aDate: String?): Date? {
            val format = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
            try {
                val date = format.parse(aDate)
//                println(date)
                return date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
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
//            tvView.allCaps = true
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
                        if (url!!.startsWith("tel:")) {
                            val tel = Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            context.startActivity(tel);
                            return true
                        } else if (url.contains("mailto:")) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                            return true

                        } else {
                            view?.loadUrl(url)
                        }
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

        fun setLayoutParam(dialog: Dialog) {
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

        fun loadImageUrlFitCenter(context: Context, imageView: ImageView, url: String) {
            Glide.with(context)
                .load(url) // image url
                .placeholder(R.drawable.ic_image_car) // any placeholder to load at start
                .error(R.drawable.ic_image_car)  // any image in case of error
                .fitCenter()
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
                    pref?.setLogOutData()
                    context.startActivity(
                        context.intentFor<LoginActivity>().clearTask().newTask()
                    )
                }
            }
            setLayoutParam(dialog)
            dialog.show()
        }

        fun insertString(
            originalString: String,
            stringToBeInserted: String,
            index: Int
        ): String? {

            // Create a new string

            // return the modified String
            try {
                return (originalString.substring(0, index + 1)
                        + stringToBeInserted
                        + originalString.substring(index + 1))
            } catch (e: Exception) {

            }
            return ""
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

        fun getUserID(): Int {
            userData = pref?.getUserData()!!
            return userData!!.buyerId!!;
        }

        fun getAuthToken(): String {
            userData = pref?.getUserData()!!
            return userData!!.authToken!!;
        }

        fun formatPhoneNo(phon: String?): String? {
            val mNo = "(" + phon
            val mno1 = AppGlobal.insertString(mNo, ")", 3)
            val mno2 = AppGlobal.insertString(mno1!!, "-", 7)
            return mno2
        }

        fun alertError(context: Context, message: String?) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_error)
            dialog.run {
                tvErrorMessage.text = message
                tvErrorOk.setOnClickListener {
                    dismiss()
                }
            }
            setLayoutParam(dialog)
            dialog.show()
        }

        fun alertCardError(context: Context, message: String?) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_error_close)
            dialog.run {
                tvCardErrorMessage.text = message
                tvCardClose.setOnClickListener {
                    dismiss()
                }
            }
            setLayoutParam(dialog)
            dialog.show()
        }

        fun alertErrorDialog(context: Activity, message: String?) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_error)
            dialog.run {
                tvErrorMessage.text = message
                tvErrorOk.setOnClickListener {
                    dialog.dismiss()
                    Handler().postDelayed({
                        context.finish()
                    }, 500)
                }
            }
            setLayoutParam(dialog)
            dialog.show()
        }

        fun callDialerOpen(context: Context, number: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            context.startActivity(intent)
        }

        fun getTimeZoneOffset(): String {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
            val currentLocalTime = calendar.time
            val date = SimpleDateFormat("Z")
            val localTime = date.format(currentLocalTime)
            var min = "-330"
            if (localTime.length == 5) {
                val subTime = localTime.substring(1, 3)
                val subMinutes = localTime.substring(3, 5)
                val setPrefix = if (localTime.substring(0, 1) == "-") "+" else "-"
                min = setPrefix + ((subTime.toInt() * 60) + subMinutes.toInt())
                println("${min} ${localTime}  TimeZone1   ")
            }

            println("${min} ${localTime}  TimeZone   ")

            return min
        }

        fun setEmojiKeyBoard(edtView: AppCompatEditText) {
            edtView.filters = EmojiFilter.getFilter()
        }

        fun showWarningDialog(context: Context) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_car_not_available)
            dialog.run {
                Handler().postDelayed({
                    dismiss()
                }, 3000)
            }
            setLayoutParam(dialog)
            dialog.show()
        }
        fun alertPaymentError(context: Context, message: String?) {
            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_error)
            dialog.run {
                tvErrorMessage.text = message
                tvErrorOk.setOnClickListener {
                    dismiss()
                }
            }
            AppGlobal.setLayoutParam(dialog)
            dialog.show()
        }

        fun setNoData(context: Context, spinner: Spinner) {
            Constant.dismissLoader()
            spinner.isEnabled = false
            showWarningDialog(context)
        }
    }
}