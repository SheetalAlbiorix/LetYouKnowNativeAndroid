package com.pionymessenger.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD

class Constant {
    companion object {

        //String
        var noInternet = "No internet connection"

        //blank error
        var emailBlank = "email can not be blank"
        var emailInvalid = "invalid email"
        var passwordBlank = "password can not be blank"
        var passwordLength = "password length must be at least 6 characters"
        var firstNameBlank = "first name can not be blank"
        var lastNameBlank = "last name can not be blank"
        var userNameBlank = "username can not be blank"

        //Arguments
        var ARG_LINK = "ARG_LINK"
        var ARG_TITLE = "ARG_TITLE"

        var TYPE_DEBIT_CREDIT_CARD = 1
        var TYPE_PAYPAL = 2
        var TYPE_BANK_ACCOUNT = 3

        lateinit var progress: KProgressHUD


        fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
            val spannableString = SpannableString(this.text)
            var startIndexOfLink = -1
            for (link in links) {
                val clickableSpan = object : ClickableSpan() {
                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.color = textPaint.linkColor
                        textPaint.isUnderlineText = true
                    }

                    override fun onClick(view: View) {
                        Selection.setSelection((view as TextView).text as Spannable, 0)
                        view.invalidate()
                        link.second.onClick(view)
                    }
                }
                startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
                spannableString.setSpan(
                    clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            this.movementMethod =
                LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
            this.setText(spannableString, TextView.BufferType.SPANNABLE)
        }

        fun showLoader(context: Activity) {
            context.hideKeyboard()
            progress = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

            progress.show()
        }

        fun dismissLoader() {
            progress.dismiss()
        }

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
            return false
        }

        fun Fragment.hideKeyboard() {
            view?.let { activity?.hideKeyboard(it) }
        }

        fun Activity.hideKeyboard() {
            hideKeyboard(currentFocus ?: View(this))
        }

        fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}