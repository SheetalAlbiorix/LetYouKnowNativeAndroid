package com.letyouknow.view.samsungpay

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.letyouknow.R
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import kotlinx.android.synthetic.main.activity_sam_payment.*

class SamPaymentActivity : AppCompatActivity(), View.OnClickListener {
    private var mSampleAppPartnerInfoHolder: SampleAppPartnerInfoHolder? = null
    private var mPaymentManager: PaymentManager? = null
    private var mActivityResumed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sam_payment)
        init()
    }

    private fun init() {
        mSampleAppPartnerInfoHolder = SampleAppPartnerInfoHolder(this)
        updateSamsungPayButton()
        tvSamsungPay.setOnClickListener(this)
    }

    protected fun updateSamsungPayButton() {
        val samsungPay = SamsungPay(this, mSampleAppPartnerInfoHolder!!.partnerInfo)
        try {

            /*
             * Method to get the Samsung Pay status on the device.
             * Partner (Issuers, Merchants, Wallet providers, and so on) applications must call this method to
             * check the current state of Samsung Pay before doing any operation.
             */
            samsungPay.getSamsungPayStatus(object : StatusListener {
                override fun onSuccess(status: Int, bundle: Bundle) {
                    when (status) {
                        SpaySdk.SPAY_READY -> {
                            tvSamsungPay.visibility = View.VISIBLE
                            if (mPaymentManager == null) {
                                mPaymentManager = PaymentManager(
                                    this@SamPaymentActivity,
                                    mSampleAppPartnerInfoHolder!!.partnerInfo
                                )
                                // Get Card List.
                            }

                        }
                        SpaySdk.SPAY_NOT_SUPPORTED, SpaySdk.SPAY_NOT_READY, SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> tvSamsungPay.visibility =
                            View.INVISIBLE
                        else -> tvSamsungPay.visibility = View.INVISIBLE
                    }
                    /* showOnSuccessLog(status, bundle) // Print log
                     showOnSuccessMessage(status, bundle) // Print messages.*/
                }

                override fun onFail(errorCode: Int, bundle: Bundle) {
                    tvSamsungPay.visibility = View.INVISIBLE
//                    showOnFailLogAndMessage(errorCode, bundle) // Print log and messages.
                }
            })
        } catch (e: NullPointerException) {
            Log.e(
                "SamsungPay", e.message!!
            )
        }
    }

    protected fun startInAppPayWithCustomSheet() {
        /*  Log.d(
              "Spay", "startInAppPayWithCustomSheet"
          )
          // PaymentManager.startInAppPayWithCustomSheet method to show custom payment sheet.
  //        disablePayButton()
          mPaymentManager = PaymentManager(this, mSampleAppPartnerInfoHolder!!.partnerInfo)

          mPaymentManager!!.startInAppPayWithCustomSheet(
              makeTransactionDetailsWithSheet(),
              transactionListener
          )*/
    }

    private fun displayToastMessageIfRequired(msg: String) {
        displayToastMessageIfRequired(msg, false)
    }

    private fun displayToastMessageIfRequired(msg: String, isRetry: Boolean) {
        if (mActivityResumed) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        } else if (!isRetry) {
            Handler(Looper.getMainLooper()).postDelayed({
                displayToastMessageIfRequired(
                    msg,
                    true
                )
            }, 1000)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSamsungPay -> {
                startInAppPayWithCustomSheet()
            }
        }
    }

}