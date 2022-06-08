package com.letyouknow.utils

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
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.google.android.gms.wallet.WalletConstants
import com.kaopiz.kprogresshud.KProgressHUD
import com.letyouknow.R
import java.util.regex.Pattern

class Constant {
    companion object {
        //google payment
        private const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"
        val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
            "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
            "gatewayMerchantId" to "exampleGatewayMerchantId"
        )

        val SUPPORTED_NETWORKS = listOf(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA"
        )

        val SUPPORTED_METHODS = listOf(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS"
        )

        const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST
        const val COUNTRY_CODE = "US"
        const val CURRENCY_CODE = "USD"

        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1

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
        /* var HUB_CONNECTION_URL =
             "https://lyksignalrwebapidemo.azurewebsites.net/buyerhub"*/
        var HUB_CONNECTION_URL =
            "https://lyksignalrapi.azurewebsites.net/buyerhub"

        //            "https://lyksignalrwebapiprofessionaldev.azurewebsites.net/buyerhub"
        var REFERRAL_LINK = "https://www.lyk.com/user1/referral"

        var ClientSecretId =
            "sk_test_51HaDBECeSnBm0gpFYr32CeQF4lOudcFSXzt7XP4ZLw0dvLGS1yfkk9KEgjbtuq9rZNkp7hCUKEQDm32Qn8XdMlOh0056dBLbq7"

        var DEMO_PANORAMA_LINK = "http://reznik.lt/wp-content/uploads/2017/09/preview3000.jpg"


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
        var ARG_UCD_DATA = "ARG_UCD_DATA"
        var ARG_IS_NOTIFICATION = "ARG_IS_NOTIFICATION"
        var ARG_NOTIFICATIONS = "ARG_NOTIFICATIONS"
        var ARG_SEL_TAB = "ARG_SEL_TAB"
        var ARG_IS_SHOW_PER = "ARG_IS_SHOW_PER"
        var ARG_IS_BID = "ARG_IS_BID"
        var ARG_IS_LCD = "ARG_IS_LCD"
        var ARG_TYPE_PRODUCT = "ARG_TYPE_PRODUCT"
        var ARG_IS_LYK_SHOW = "ARG_IS_LYK_SHOW"
        var ARG_MSRP_RANGE = "ARG_MSRP_RANGE"
        var ARG_DEAL_ID = "ARG_DEAL_ID"
        var ARG_CAL_TAX_DATA = "ARG_CAL_TAX_DATA"

        var TYPE_DEBIT_CREDIT_CARD = 1
        var TYPE_PAYPAL = 2
        var TYPE_BANK_ACCOUNT = 3
        var TYPE_SUBMIT_PRICE = 0
        var TYPE_ONE_DEAL_NEAR_YOU = 1
        var TYPE_SEARCH_DEAL = 2
        var LYK100Dollars = "LYK100Dollars"
        var CHANNEL_ID = "CHANNEL_ID"


        var KEY_NOTIFICATION_TYPE = "type"
        var TransactionCode = "TransactionCode"
        var Data = "data"
        var PROMO_CODE = "PromoCode"
        var MARKET_CONDITION = "MarketCondition"
        var HOT_PRICE = "HotPrice"
        var LYK_DEAL = "LYKDeal"
        var LCD_DEAL = "LCDDeal"
        var UCD_DEAL = "UCDDeal"


        lateinit var progress: KProgressHUD

        fun isInitProgress(): Boolean {
            return ::progress.isInitialized
        }

        val patternPassword =
            Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~_`#?{}()+!@\$%^&*-]).{8,16}\$")


        val patternEmail =
            Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$")

        fun emailValidator(strEmail: String): Boolean {
            return patternEmail.matcher(strEmail).matches()
        }


        fun passwordValidator(strPassword: String): Boolean {
            return patternPassword.matcher(strPassword).matches()
        }

        val patternMiddleName =
            Pattern.compile("^[a-zA-Z]+")

        fun middleNameValidator(str: String): Boolean {
            return patternMiddleName.matcher(str).matches()
        }

        fun middleNameValidatorText(str: String): Boolean {
            return patternMiddleName.matcher(str).matches()
        }

        private val firstNameValidator1 = Pattern.compile("^[A-Za-z]{0,50}?\$")
        private val firstNameValidator2 = Pattern.compile("^[A-Za-z]{0,2}'[A-Za-z]{0,47}?\$")
        private val firstNameValidator3 = Pattern.compile("^[A-Za-z]{0,25}? [A-Za-z]{0,24}?\$")

        fun firstNameValidator(str: String): Boolean {
            return !(firstNameValidator1.matcher(str).matches() || firstNameValidator2.matcher(str)
                .matches() || firstNameValidator3.matcher(str).matches())
        }

        private val lastNameValidator1 = Pattern.compile("^[A-Za-z]{0,50}?\$")
        private val lastNameValidator2 = Pattern.compile("^[A-Za-z]{0,2}'[A-Za-z]{0,47}?\$")
        private val lastNameValidator3 = Pattern.compile("^[A-Za-z]{0,25}? [A-Za-z]{0,24}?\$/")
        private val lastNameValidator4 = Pattern.compile("^[A-Za-z]{0,25}?\\-[A-Za-z]{1,24}?\$")


        fun lastNameValidator(str: String): Boolean {
            return !(lastNameValidator1.matcher(str).matches() || lastNameValidator2.matcher(str)
                .matches() || lastNameValidator3.matcher(str)
                .matches() || lastNameValidator4.matcher(str).matches())
        }

        private val cityValidator =
            Pattern.compile("^[a-zA-Z\\u0080-\\u024F\\s\\/\\-\\)\\(\\`\\.\\\"\\']{3,50}\$")


        fun cityValidator(str: String): Boolean {
            return !(cityValidator.matcher(str).matches())
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

        fun onTextChangeFirstName(context: Context, edtText: EditText, errorText: TextView) {
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
                        if (firstNameValidator(str)) {
                            Constant.setErrorBorder(edtText, errorText)
                            errorText.text = context.getString(R.string.enter_valid_first_name)
                            errorText.visibility = View.VISIBLE
                        } else {
                            edtText.setBackgroundResource(R.drawable.bg_edittext)
                            errorText.visibility = View.GONE
                        }
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                    } else {
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }

        fun onTextChangeMiddleName(context: Context, edtText: EditText) {
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
                        /* if (middleNameValidatorText(str)) {
                             setErrorBorder(edtText, errorText)
                             errorText.text = context.getString(R.string.enter_valid_middle_name)
                             errorText.visibility = View.VISIBLE
                         } else {
                             edtText.setBackgroundResource(R.drawable.bg_edittext)
                             errorText.visibility = View.GONE
                         }*/
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                    } else {
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                    }

                }

                override fun afterTextChanged(s: Editable?) {
                    val str = edtText.text.toString().trim()

                    if (s != null) {
                        if (str.length > 0) {
                            if (!str.substring(s.length - 1)
                                    .isDigitsOnly() && middleNameValidatorText(str)
                            ) {
                                if (s.length > 1) {
                                    edtText.setText(s.subSequence(1, s.length))
                                    edtText.setSelection(1)
                                }
                            } else {
                                edtText.setText("")
                            }

                        }
                    }
                }

            })
        }

        fun onTextChangeLastName(context: Context, edtText: EditText, errorText: TextView) {
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
                        if (lastNameValidator(str)) {
                            setErrorBorder(edtText, errorText)
                            errorText.text = context.getString(R.string.enter_valid_last_name)
                            errorText.visibility = View.VISIBLE
                        } else {
                            edtText.setBackgroundResource(R.drawable.bg_edittext)
                            errorText.visibility = View.GONE
                        }
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                    } else {
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }

        fun onTextChangeAddress1(context: Context, edtText: EditText, errorText: TextView) {
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
                    if (str?.length!! > 0) {
                        if (str.length == 0) {
                            setErrorBorder(edtText, errorText)
                            errorText.text = context.getString(R.string.enter_addressline1)
                        } else if (str.length > 1 && str.length < 3) {
                            setErrorBorder(edtText, errorText)
                            errorText.text =
                                context.getString(R.string.address1_must_be_minimum_three_characters)
                            errorText.visibility = View.VISIBLE
                        } else {
                            edtText.setBackgroundResource(R.drawable.bg_edittext)
                            errorText.visibility = View.GONE
                        }
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,activeDrawable),null, null,  null)
                    } else {
                        setErrorBorder(edtText, errorText)
                        errorText.text = context.getString(R.string.enter_addressline1)
                        //edtText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,deActiveDrawable),null, null,  null)
                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }

        fun onTextChangeCity(context: Context, edtText: EditText, errorText: TextView) {
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
                        if (cityValidator(str)) {
                            setErrorBorder(edtText, errorText)
                            errorText.text = context.getString(R.string.enter_valid_City)
                            errorText.visibility = View.VISIBLE
                        } else {
                            edtText.setBackgroundResource(R.drawable.bg_edittext)
                            errorText.visibility = View.GONE
                        }
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