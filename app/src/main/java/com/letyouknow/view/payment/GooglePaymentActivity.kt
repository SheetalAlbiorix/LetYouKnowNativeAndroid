package com.letyouknow.view.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.GooglePayClient
import com.braintreepayments.api.GooglePayRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.pay.PayClient
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.WalletConstants
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.retrofit.viewmodel.CheckoutViewModel
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayPaymentMethodLauncher
import kotlinx.android.synthetic.main.activity_google_payment.*
import org.json.JSONException
import org.json.JSONObject

class GooglePaymentActivity : BaseActivity(), View.OnClickListener {
    private val addToGoogleWalletRequestCode = 1000

    private val model: CheckoutViewModel by viewModels()

    var a = "aa"
    var b = "aaa"
    var c = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_payment)
        init()
//        initGoogle()
        initLiveGoogle()
        if (a === b) {
            Log.e("tripple", "equal")
        }
        if (a == b) {
            Log.e("double", "equal")
        }
    }

    private fun init() {
        llGooglePay.setOnClickListener(this)
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llGooglePay -> {
//                requestPayment()
//                clickBTN()
                clickGoogle()
            }
        }
    }

    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val dummyPriceCents = 100L
        val shippingCostCents = 900L
        val task = model.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }
                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                    " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }

            // Re-enables the Google Pay payment button.
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            Toast.makeText(
                this,
                getString(R.string.payments_show_name, billingName),
                Toast.LENGTH_LONG
            ).show()

            // Logging token string.
            Log.d(
                "Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )

        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }
    }


    // Handle potential conflict from calling loadPaymentData
    private val resolvePaymentForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                    }

                RESULT_CANCELED -> {
                    // The user cancelled the payment attempt
                }
            }
        }

    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == addToGoogleWalletRequestCode) {
            when (resultCode) {
                RESULT_OK -> Toast
                    .makeText(
                        this,
                        getString(R.string.add_google_wallet_success),
                        Toast.LENGTH_LONG
                    )
                    .show()

                RESULT_CANCELED -> {
                    // Save canceled
                }

                PayClient.SavePassesResult.SAVE_ERROR -> data?.let { intentData ->
                    val apiErrorMessage =
                        intentData.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE)
                    handleError(resultCode, apiErrorMessage)
                }

                else -> handleError(
                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!"
                )
            }
            // Re-enables the Google Pay payment button.
        }

        /*  googlePayClient.onActivityResult(resultCode, data) { paymentMethodNonce, error ->
              Log.e("response Google",Gson().toJson(paymentMethodNonce))
              // send paymentMethodNonce.string to your server
          }*/
    }


    private lateinit var braintreeClient: BraintreeClient
    private lateinit var googlePayClient: GooglePayClient
    private fun initGoogle() {
        braintreeClient = BraintreeClient(this, "sandbox_f252zhq7_hh4cpc39zq4rgjcg")
        googlePayClient = GooglePayClient(braintreeClient)
    }

    private fun clickBTN() {
        val googlePayRequest = GooglePayRequest()

        googlePayRequest.transactionInfo = TransactionInfo.newBuilder()
            .setTotalPrice("1.00")
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setCurrencyCode("USD")
            .build()
        googlePayRequest.isBillingAddressRequired = true

        googlePayClient.requestPayment(this, googlePayRequest) { error ->
            error?.let {
                // handle error
                error.message?.let { it1 -> Log.e("error", it1) }
            }
        }
    }

    private lateinit var googlePayLauncher: GooglePayPaymentMethodLauncher
    private fun initLiveGoogle() {
        googlePayLauncher = GooglePayPaymentMethodLauncher(
            activity = this,
            config = GooglePayPaymentMethodLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "US",
                merchantName = "Widget Store",
                isEmailRequired = false,
                existingPaymentMethodRequired = false
            ),
            readyCallback = ::onGooglePayReady,
            resultCallback = ::onGooglePayResult
        )
    }

    private fun clickGoogle() {
        googlePayLauncher.present(
            currencyCode = "USD",
            amount = 2500
        )
    }

    private fun onGooglePayReady(isReady: Boolean) {
        llGooglePay.isEnabled = isReady
    }

    private fun onGooglePayResult(
        result: GooglePayPaymentMethodLauncher.Result
    ) {
        when (result) {
            is GooglePayPaymentMethodLauncher.Result.Completed -> {
                // Payment details successfully captured.
                // Send the paymentMethodId to your server to finalize payment.
                val paymentMethodId = result.paymentMethod.id

                paymentMethodId?.let { Log.e("PaymentId", it) }
            }
            GooglePayPaymentMethodLauncher.Result.Canceled -> {
                // User canceled the operation
                Log.e("Canceled", "Canceled")
            }
            is GooglePayPaymentMethodLauncher.Result.Failed -> {
                result.error.message?.let { Log.e("Failed", it) }
                // Operation failed; inspect `result.error` for the exception
            }
        }
    }
}