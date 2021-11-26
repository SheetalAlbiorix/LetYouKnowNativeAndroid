package com.pionymessenger.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.letyouknow.R
import java.util.regex.Pattern


class Constant {
    companion object {

        //String
        var noInternet = "No internet connection"

        //        var TERMS_CONDITIONS_LINK = "https://github.com/flutter/flutter/issues/70284"
        var HELP = "https://demo.letyouknow.com/helpandcontact"
        var FAQ = "https://demo.letyouknow.com/helpandcontact"
        var HOW_IT_WORKS = "https://demo.letyouknow.com/howitworksvid"
        var TERMS_CONDITIONS_LINK = "https://demo.letyouknow.com/termsandconditions"

        //        var TERMS_CONDITIONS_LINK = "https://www.demo.letyouknow.com/termsandconditions"
        var PRIVACY_POLICY_LINK = "https://demo.letyouknow.com/privacypolicy"

        //        var PRIVACY_POLICY_LINK = "https://letyouknow.com/privacypolicy"
        var HUB_CONNECTION_URL =
            "https://lyksignalrwebapidemo.azurewebsites.net/buyerhub"
//            "https://lyksignalrwebapiprofessionaldev.azurewebsites.net/buyerhub"

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
        var ARG_WEB_URL = "ARG_WEB_URL"
        var ARG_TITLE = "ARG_TITLE"
        var ARG_POLICY = "ARG_POLICY"
        var ARG_TYPE_VIEW = "ARG_TYPE_VIEW"
        var ARG_LCD_DEAL_GUEST = "ARG_LCD_DEAL_GUEST"
        var ARG_IMAGE_ID = "ARG_IMAGE_ID"
        var ARG_UCD_DEAL = "ARG_UCD_DEAL"
        var ARG_UCD_DEAL_PENDING = "ARG_UCD_DEAL_PENDING"
        var ARG_RADIUS = "ARG_RADIUS"
        var ARG_ZIPCODE = "ARG_ZIPCODE"
        var ARG_YEAR_MAKE_MODEL = "ARG_YEAR_MAKE_MODEL"
        var ARG_IMAGE_URL = "ARG_IMAGE_URL"
        var ARG_SUBMIT_DEAL = "ARG_SUBMIT_DEAL"
        var ARG_TRANSACTION_CODE = "ARG_TRANSACTION_CODE"
        var ARG_NOTIFICATIONS = "ARG_NOTIFICATIONS"
        var ARG_SEL_TAB = "ARG_SEL_TAB"

        var TYPE_DEBIT_CREDIT_CARD = 1
        var TYPE_PAYPAL = 2
        var TYPE_BANK_ACCOUNT = 3
        var TYPE_SUBMIT_PRICE = 0
        var TYPE_ONE_DEAL_NEAR_YOU = 1
        var TYPE_SEARCH_DEAL = 2

        lateinit var progress: KProgressHUD

        fun isInitProgress(): Boolean {
            return ::progress.isInitialized
        }

        val patternPassword =
            Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~_`#?{}()+!@\$%^&*-]).{8,16}\$")

        fun emailValidator(strEmail: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()
        }

        fun passwordValidator(strPassword: String): Boolean {
            return patternPassword.matcher(strPassword).matches()
        }

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
                .setCancellable(false)
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

        fun onTextChange(context: Context, edtText: EditText, errorText: TextView) {
            edtText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val str = s?.toString()
                    if (str?.length!! >= 0) {
                        edtText.setBackgroundResource(R.drawable.bg_edittext)
                        errorText.visibility = View.GONE
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                    } else {
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }

        fun setErrorBorder(edtView: EditText, tvError: TextView) {
            edtView.requestFocus()
            edtView.setBackgroundResource(R.drawable.bg_edittext_error)
            tvError.visibility = View.VISIBLE
        }
    }
}