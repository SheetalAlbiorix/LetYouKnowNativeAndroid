package com.letyouknow

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.logispeed.data.prefs.AppPreferencesHelper
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
            "pk_test_51BTUDGJAJfZb9HEBwDg86TN1KNprHjkfipXmEDMb0gSCassK5T3ZfxsAbcgKVmAIXF7oZ6ItlZZbXO6idTHE67IM007EwQ4uN3"
        )
//        FirebaseApp.initializeApp(this)

        //Generate 11 digit key for sms retrive
//        Log.i("AppApplication","appSignatures = ${AppSignatureHelper(this).getAppSignatures()}")
    }

    fun getAppPreferencesHelper(): AppPreferencesHelper? {
        return appPreferencesHelper
    }


}