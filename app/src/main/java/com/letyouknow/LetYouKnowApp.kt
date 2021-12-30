package com.letyouknow

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.logispeed.data.prefs.AppPreferencesHelper
import com.stripe.android.PaymentAuthConfig
import com.stripe.android.PaymentConfiguration

class LetYouKnowApp : Application() {
    private var appPreferencesHelper: AppPreferencesHelper? = null

    companion object {
        private var mInstance: LetYouKnowApp? = null

        @Synchronized
        fun getInstance(): LetYouKnowApp? {
            return mInstance

        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().log("Crash")
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
//        FacebookSdk.sdkInitialize(this)
//        AppEventsLogger.activateApp(this);
        mInstance = this
//        Fabric.with(this, Crashlytics())

        appPreferencesHelper = AppPreferencesHelper(this, getString(R.string.app_name))
        PaymentConfiguration.init(
            applicationContext,
            getString(R.string.stripe_publishable_key)
        )
        val uiCustomization =
            PaymentAuthConfig.Stripe3ds2UiCustomization.Builder()
                .setToolbarCustomization(
                    PaymentAuthConfig.Stripe3ds2ToolbarCustomization.Builder()
                        .setStatusBarColor("#0082cf").setBackgroundColor("#FFFFFF")
                        .setHeaderText("#0082cf").build()
                )
                .setLabelCustomization(
                    PaymentAuthConfig.Stripe3ds2LabelCustomization.Builder()
                        .setTextFontSize(12)
                        .setHeadingTextColor("#0082cf")
                        .build()
                )
                .build()
        PaymentAuthConfig.init(
            PaymentAuthConfig.Builder()
                .set3ds2Config(
                    PaymentAuthConfig.Stripe3ds2Config.Builder()
                        .setTimeout(5)
                        .setUiCustomization(uiCustomization)
                        .build()
                )
                .build()
        )
//        FirebaseApp.initializeApp(this)

        //Generate 11 digit key for sms retrive
//        Log.i("AppApplication","appSignatures = ${AppSignatureHelper(this).getAppSignatures()}")
    }

    fun getAppPreferencesHelper(): AppPreferencesHelper? {
        return appPreferencesHelper
    }


}