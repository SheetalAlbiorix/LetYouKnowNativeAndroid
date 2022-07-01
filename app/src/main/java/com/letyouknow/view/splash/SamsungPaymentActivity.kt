package com.letyouknow.view.splash


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.letyouknow.R
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.SpaySdk.EXTRA_ERROR_REASON
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import kotlinx.android.synthetic.main.activity_samsung_payment.*


class SamsungPaymentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var paymentManager: PaymentManager
    private lateinit var partnerInfo: PartnerInfo
    private lateinit var samsungPay: SamsungPay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samsung_payment)
        init()
    }

    private fun init() {
        tvSamsungPay.setOnClickListener(this)
        val bundle = Bundle()
        bundle.putString(
            SamsungPay.PARTNER_SERVICE_TYPE,
            SpaySdk.ServiceType.INAPP_PAYMENT.toString()
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            tvSamsungPay.visibility = View.GONE
        }
        partnerInfo = PartnerInfo(getString(R.string.samsung_pay_service_id), bundle)
        samsungPay = SamsungPay(this, partnerInfo)
        paymentManager = PaymentManager(this, partnerInfo)
        requestCardInfo()
        samsungPay.getSamsungPayStatus(object : StatusListener {
            override fun onSuccess(status: Int, bundle: Bundle?) {
                when (status) {
                    SamsungPay.SPAY_NOT_SUPPORTED -> {
                        Log.e("not support", bundle?.getInt(EXTRA_ERROR_REASON).toString())
                        tvSamsungPay.visibility = View.GONE
                    }
                    SamsungPay.SPAY_NOT_READY -> {
                        val extraReason = bundle!!.getInt(SamsungPay.EXTRA_ERROR_REASON)
                        when (extraReason) {
                            SamsungPay.ERROR_SPAY_APP_NEED_TO_UPDATE -> {
                                samsungPay.goToUpdatePage()
                            }
                            SamsungPay.ERROR_SPAY_SETUP_NOT_COMPLETED -> {
                                samsungPay.activateSamsungPay()
                            }
                            else -> {
                                tvSamsungPay.visibility = View.GONE
                            }
                        }
                    }
                    SamsungPay.SPAY_READY -> {
                        tvSamsungPay.visibility = View.VISIBLE
                    }
                    else -> {
                        tvSamsungPay.visibility = View.GONE
                    }
                }
            }

            override fun onFail(errorCode: Int, p1: Bundle?) {
                Log.d("TAG", "checkSamsungPayStatus onFail() : " + errorCode)
            }
        })
    }

    private fun requestCardInfo() {
        paymentManager.requestCardInfo(Bundle(), cardInfoListener)
    }

    val cardInfoListener = object : PaymentManager.CardInfoListener {

        override fun onResult(cardResponse: MutableList<CardInfo>?) {
            var visaCount = 0
            var mcCount = 0
            var amexCount = 0
            var dsCount = 0
            var brandStrings = "Card Info : "
            if (cardResponse != null) {
                var brand: SpaySdk.Brand
                for (i in 0 until cardResponse.size) {
                    brand = cardResponse[i].brand
                    when (brand) {
                        SpaySdk.Brand.AMERICANEXPRESS -> {
                            amexCount++
                        }
                        SpaySdk.Brand.MASTERCARD -> {
                            mcCount++
                        }
                        SpaySdk.Brand.VISA -> {
                            visaCount++
                        }
                        SpaySdk.Brand.DISCOVER -> {
                            dsCount++
                        }
                        else -> {
                        }
                    }
                }
                brandStrings += " VI=" + visaCount + ", MC=" + mcCount + ", AX=" + amexCount + ",DS=" + dsCount;
            }
        }

        override fun onFailure(errorCode: Int, p1: Bundle?) {
            Toast.makeText(
                this@SamsungPaymentActivity,
                "cardInfoListner onFailure : " + errorCode,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun makeTransactionDetails():
            com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo? {
        val brandList: ArrayList<SpaySdk.Brand> = ArrayList()
        // If the supported brand is not specified, all card brands in Samsung Pay are listed
        // in the Payment Sheet.
        brandList.add(SpaySdk.Brand.MASTERCARD)
        brandList.add(SpaySdk.Brand.VISA)
        brandList.add(SpaySdk.Brand.AMERICANEXPRESS)
        val shippingAddress =

            com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.Address.Builder()
                .setAddressee("name")
                .setAddressLine1("addLine1")
                .setAddressLine2("addLine2")
                .setCity("city")
                .setState("state")
                .setCountryCode("United States")
                .setPostalCode("zip")
                .build()
        val amount =
            com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.Amount.Builder()
                .setCurrencyCode("USD")
                .setItemTotalPrice("1000")
                .setShippingPrice("10")
                .setTax("50")
                .setTotalPrice("1060")
                .build()
        return com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.Builder()
            .setMerchantId("123456")
            .setMerchantName("Sample Merchant")
            .setOrderNumber("AMZ007MAR")
            .setPaymentProtocol(
                com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.PaymentProtocol.PROTOCOL_3DS
            ) // Merchant requires billing address from Samsung Pay and
            // sends the shipping address to Samsung Pay.
            // Option shows both billing and shipping address on the payment sheet.
            .setAddressInPaymentSheet(
                com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.AddressInPaymentSheet.NEED_BILLING_SEND_SHIPPING
            )
            .setShippingAddress(shippingAddress)
            .setAllowedCardBrands(brandList)
            .setCardHolderNameEnabled(true)
            .setRecurringEnabled(false)
            .setAmount(amount)
            .build()
    }

    private fun requestSamsungPay() {
        paymentManager.startInAppPay(
            makeTransactionDetails(),
            transactionListener
        )
    }

    private val transactionListener: PaymentManager.TransactionInfoListener =
        object : PaymentManager.TransactionInfoListener {
            // This callback is received when the user modifies or selects a new address
            // on the payment sheet.
            override fun onAddressUpdated(paymentInfo: com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo?) {
                try {
                    // Do address verification by merchant app
                    /* setAddressInPaymentSheet(PaymentInfo.AddressInPaymentSheet.
			* NEED_BILLING_SEND_SHIPPING)
			* If you set NEED_BILLING_SEND_SHIPPING or NEED_BILLING_SPAY with
			* like upper codes,
			* you can get Billing Address with getBillingAddress().
			* If you set NEED_BILLING_AND_SHIPPING or NEED_SHIPPING_SPAY,
			* you can get Shipping Address with getShippingAddress().
			 */
                    /*    val billing_address: PaymentInfo.Address = paymentInfo.getBillingAddress()
                        val billing_errorCode: Int = validateBillingAddress(billing_address)
                        // Call updateAmount() or updateAmountFailed() method. This is mandatory.
                        if (billing_errorCode != PaymentManager.ERROR_NONE) paymentManager.updateAmountFailed(
                            billing_errorCode
                        ) else {*/
                    val amount =
                        com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.Amount.Builder()
                            .setCurrencyCode("USD")
                            .setItemTotalPrice("1000.00")
                            .setShippingPrice("10.00")
                            .setTax("50.00")
                            .setTotalPrice("1060.00")
                            .build()
                    paymentManager.updateAmount(amount)
//                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }

            //This callback is received when the user changes card on the payment sheet in Samsung Pay
            override fun onCardInfoUpdated(selectedCardInfo: CardInfo) {
                /*
		 * Called when the user changes card in Samsung Pay.
		 * Newly selected cardInfo is passed and merchant app can update transaction
                   * amount based on new card (if needed).
		 */
                try {
                    val amount =
                        com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo.Amount.Builder()
                            .setCurrencyCode("USD")
                            .setItemTotalPrice("1000.00")
                            .setShippingPrice("10.00")
                            .setTax("50.00")
                            .setTotalPrice("1060.00")
                            .build()

                    // Call updateAmount() method. This is mandatory.
                    paymentManager.updateAmount(amount)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }

            override fun onSuccess(
                paymentInfo: com.samsung.android.sdk.samsungpay.v2.payment.PaymentInfo?,
                p1: String?,
                p2: Bundle?
            ) {
                Log.e("Payment : ", "Success " + paymentInfo?.paymentCurrencyCode)
            }

            /*
	 * This callback is received when the online (In-App) payment transaction is approved by
	 * user and able to successfully generate In-App payload.
	 * The payload could be an encrypted cryptogram (direct In-App Payment)
	 * or Payment Gateway's token reference ID (indirect In-App Payment).
	 */
            fun onSuccess(
                response: PaymentManager?,
                paymentCredential: String?,
                extraPaymentData: Bundle?
            ) {

                Toast.makeText(
                    this@SamsungPaymentActivity,
                    "Transaction : onSuccess",
                    Toast.LENGTH_LONG
                ).show()
                // You can use PaymentInfo, paymentCredentials and extraPaymentData.
            }

            // This callback is received when the online payment transaction has failed.
            override fun onFailure(errorCode: Int, errorData: Bundle) {
                Toast.makeText(
                    this@SamsungPaymentActivity,
                    "Transaction : onFailure : $errorCode",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSamsungPay -> {
                requestSamsungPay()
            }
        }
    }
}
